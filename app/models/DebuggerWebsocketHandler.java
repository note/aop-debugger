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
			public void invoke(JsonNode event) {
				System.out.println("Otrzymano wiadomosc");
				actor.tell(new Message());
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
			synchronized(debuggedThread) {
				debuggedThread.notify();
			}
		}
	}

	public static class Message {
	}

}
