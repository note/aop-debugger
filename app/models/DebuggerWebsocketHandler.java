package models;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.aspectj.lang.JoinPoint;
import org.aspectj.weaver.loadtime.WeavingURLClassLoader;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

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

	public static void register(WebSocket.In<JsonNode> inWebsocket, WebSocket.Out<JsonNode> outWebsocket, String jarToDebug) {
		handleWebsockets(inWebsocket, outWebsocket);
		invokeMainMethod(jarToDebug);
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

	private static void invokeMainMethod(String jarToDebug) {
		try {
			URL[] url = { new URL("file://" + System.getProperty("user.dir") + File.separator + jarToDebug), new URL("file:///usr/share/java/aspectjrt.jar"),
					new URL("file:///home/michal/Desktop/programming/code/java/aop-debugger/debugger.jar") };
			URLClassLoader loader = new URLClassLoader(url);
			WeavingURLClassLoader weaver = new WeavingURLClassLoader(loader);
			Class<?> cls = loader.loadClass("to.be.debugged.Simple");
			Method meth = cls.getMethod("main", String[].class);
			String[] params = null;
			meth.invoke(null, (Object) params);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void sendBreakpointMessage(JoinPoint point, StackTraceElement[] stack, Thread thread) {
		debuggedThread = thread;
		ObjectNode breakpointJson = Json.newObject();
		breakpointJson.put("methodName", point.getSignature().getName());
		out.write(breakpointJson);
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof Message) {
			debuggedThread.interrupt();
		}
	}

	public static class Message {
	}

}
