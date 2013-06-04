package models;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.SourceLocation;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.libs.Akka;
import play.libs.F.Callback;
import play.libs.Json;
import play.mvc.WebSocket;
import streams.ThreadPrintStream;
import threads.DebuggedProgramThread;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import debugger.Debugger;

public class DebuggerWebsocketHandler extends UntypedActor {
	private static WebSocket.Out<JsonNode> out;
	private static int counter = 0;
	private static Thread debuggedThread;
	private static Clazz classWithMain;

	static ActorRef actor = Akka.system().actorOf(new Props(DebuggerWebsocketHandler.class));

	public static void register(WebSocket.In<JsonNode> inWebsocket, WebSocket.Out<JsonNode> outWebsocket, final String jarToDebug) {
		handleWebsockets(inWebsocket, outWebsocket);

		// We have to replaceSystemOut in this thread to enable newly created
		// thread to operate on System.out as ThreadPrintStream
		ThreadPrintStream.replaceSystemOut();

		DebuggedProgramThread debugged = new DebuggedProgramThread(jarToDebug);
		classWithMain = debugged.getClassWithMain();
		new Thread(debugged).start();
	}

	private static void handleWebsockets(WebSocket.In<JsonNode> inWebsocket, WebSocket.Out<JsonNode> outWebsocket) {
		out = outWebsocket;

		inWebsocket.onMessage(new Callback<JsonNode>() {
			public void invoke(JsonNode data) {
				System.out.println("Otrzymano wiadomosc");
				actor.tell(new Message(data));
			}
		});
	}

	public static void sendBreakpointMessage(JoinPoint point, StackTraceElement[] stack, Object[] args, Thread thread) {
		debuggedThread = thread;
		ObjectNode breakpointJson = Json.newObject();
		breakpointJson.put("methodName", point.getSignature().getName());
		breakpointJson.put("signature", point.getSignature().toLongString());
		ArrayNode arguments = breakpointJson.putArray("arguments");
		ArrayNode stackTrace = breakpointJson.putArray("stackTrace");
		for (Object o : args) {
			Class klass = o.getClass();
			ObjectNode argNode = Json.newObject();
			argNode.put("editable", klass == String.class || klass == Integer.class);
			argNode.put("string", o.toString());
			argNode.put("klass", klass.getName());
			arguments.add(argNode);
		}
		for (StackTraceElement o : stack) {
			stackTrace.add(o.toString());
		}
		SourceLocation location = point.getSourceLocation();
		breakpointJson.put("sourceLocation", location.getFileName() + ":" + location.getLine());
		try {
			breakpointJson.put("standardOutput", getOutputForThread(thread));
		} catch (IOException e) {
			breakpointJson.put("standardOutput", "");
		}

		out.write(breakpointJson);
	}

	private static String getOutputForThread(Thread thread) throws IOException {
		String content = Files.toString(new File(DebuggedProgramThread.getOutputFilePathForCurrentThread()), Charsets.UTF_8);
		return content;
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof Message) {
			JsonNode json = ((Message) message).data;
			String action = json.get("action").getTextValue();

			if (action.equals("breakpoint")) {
				setBreakpoint(json);
			}
			if (action.equals("outside-debug")) {
				setOutsideDebugging(json);
			}
			if (action.equals("continue")) {
				continueDebugger(json);
			}
			if (action.equals("step")) {
				stepDebugger(json);
			}
		}
	}

	private void setOutsideDebugging(JsonNode json) {
		Debugger debugger = Debugger.getInstance();
		System.out.println(json.get("enabled").asBoolean());
		debugger.debugOutsideJar = json.get("enabled").asBoolean();
	}

	private void setBreakpoint(JsonNode json) {
		Debugger debugger = Debugger.getInstance();

		JsonNode breakPointData = json.get("data");
		String packageName = breakPointData.get("package").getTextValue();
		String className = breakPointData.get("klass").getTextValue();
		Boolean enabled = breakPointData.get("enabled").getBooleanValue();
		String fullName;
		if (breakPointData.has("method")) {
			String methodName = breakPointData.get("method").getTextValue();
			fullName = packageName + "." + className + "." + methodName;
			if (!enabled)
				debugger.forbiddenMethods.add(fullName);
			else
				debugger.forbiddenMethods.remove(fullName);
		} else {
			fullName = packageName + "." + className;
			if (!enabled)
				debugger.forbiddenClasses.add(fullName);
			else
				debugger.forbiddenClasses.remove(fullName);
		}
	}

	private void continueDebugger(JsonNode json) {
		Debugger debugger = Debugger.getInstance();
		debugger.stepMode = false;
		nextBreakpoint(json);
	}

	private void stepDebugger(JsonNode json) {
		Debugger debugger = Debugger.getInstance();
		debugger.stepMode = true;
		nextBreakpoint(json);
	}

	private void nextBreakpoint(JsonNode json) {
		readArguments(json.get("arguments"));
		synchronized (debuggedThread) {
			debuggedThread.notify();
		}
	}

	private void readArguments(JsonNode json) {
		Debugger debugger = Debugger.getInstance();
		Iterator<Entry<String, JsonNode>> iterator = json.getFields();
		while (iterator.hasNext()) {
			Entry<String, JsonNode> item = iterator.next();
			int key = Integer.parseInt(item.getKey());
			debugger.arguments[key] = item.getValue().asText();
		}
	}

	public static class Message {
		JsonNode data;

		public Message(JsonNode data) {
			this.data = data;
		}
	}

	public static String getRootPackageOfDebuggedJar() {
		return classWithMain.getPackageName();
	}

}
