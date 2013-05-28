package models;

import helpers.PathHelpers;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.SourceLocation;
import org.aspectj.weaver.loadtime.WeavingURLClassLoader;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import debugger.Debugger;
import debugger.impl.DebuggerWeb;

import play.libs.Akka;
import play.libs.F.Callback;
import play.libs.Json;
import play.mvc.WebSocket;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class DebuggerWebsocketHandler extends UntypedActor {
	private static WebSocket.Out<JsonNode> out;
	private static int counter = 0;
	private static Thread debuggedThread;

	static ActorRef actor = Akka.system().actorOf(new Props(DebuggerWebsocketHandler.class));

	public static void register(WebSocket.In<JsonNode> inWebsocket, WebSocket.Out<JsonNode> outWebsocket, final String jarToDebug) {
		handleWebsockets(inWebsocket, outWebsocket);
		new Thread(new Runnable() {
			@Override
			public void run() {
				invokeMainMethod(jarToDebug);
			}
		}).start();
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

	private static void invokeMainMethod(final String jarToDebug) {
		try {
			URL[] url = { new URL(PathHelpers.getUploadsURL() + jarToDebug), //
					new URL(PathHelpers.getPwdURL() + "debugger.jar") }; //
			URLClassLoader loader = new URLClassLoader(url, DebuggerWebsocketHandler.class.getClassLoader());
			WeavingURLClassLoader weaver = new WeavingURLClassLoader(loader);
			Class<?> cls = loader.loadClass("to.be.debugged.Simple");
			Method meth = cls.getMethod("main", String[].class);
			String[] params = null;
			meth.invoke(null, (Object) params);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void sendBreakpointMessage(JoinPoint point, StackTraceElement[] stack, Thread thread) {
		debuggedThread = thread;
		ObjectNode breakpointJson = Json.newObject();
		breakpointJson.put("methodName", point.getSignature().getName());
		breakpointJson.put("signature", point.getSignature().toLongString());
		ArrayNode arguments = breakpointJson.putArray("arguments");
		ArrayNode stackTrace = breakpointJson.putArray("stackTrace");
		for(Object o : point.getArgs()) {
			arguments.add(o.toString());
		}
		for(StackTraceElement o : stack) {
			stackTrace.add(o.toString());
		}
		SourceLocation location = point.getSourceLocation();
		breakpointJson.put("sourceLocation", location.getFileName() + ":" + location.getLine());

		out.write(breakpointJson);
	}
	

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof Message) {
			JsonNode json = ((Message) message).data;
			String action = json.get("action").getTextValue();
			
			if(action.equals("breakpoint")) {
				setBreakpoint(json);
			}
			if(action.equals("outside-debug")) {
				setOutsideDebugging(json);
			}
			if(action.equals("continue")) {
				continueDebugger();
			}
			if(action.equals("step")) {
				stepDebugger();
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
		if(breakPointData.has("method")) {
			String methodName = breakPointData.get("method").getTextValue();
			fullName = packageName + "." + className + "." + methodName;
			if(!enabled)
				debugger.forbiddenMethods.add(fullName);
			else
				debugger.forbiddenMethods.remove(fullName);
		} else {
			fullName = packageName + "." + className;
			if(!enabled)
				debugger.forbiddenClasses.add(fullName);
			else
				debugger.forbiddenClasses.remove(fullName);
		}
	}
	
	private void continueDebugger(){
		Debugger debugger = Debugger.getInstance();
		debugger.stepMode = false;
		nextBreakpoint();
	}
	
	private void stepDebugger(){
		Debugger debugger = Debugger.getInstance();
		debugger.stepMode = true;
		nextBreakpoint();
	}
	
	
	
	private void nextBreakpoint() {
		synchronized(debuggedThread) {
			debuggedThread.notify();
		}
	}

	public static class Message {
		JsonNode data;

		public Message(JsonNode data) {
			this.data = data;
		}
	}

}
