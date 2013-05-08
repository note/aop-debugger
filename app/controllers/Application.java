package controllers;

import java.io.File;
import java.io.IOException;

import models.DebuggerWebsocketHandler;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.mvc.WebSocket;
import views.html.debugger;
import views.html.index;
import views.html.program;
import views.html.websocket;

import com.google.common.io.Files;

public class Application extends Controller {

	public static Result index() {
		return ok(index.render("Your new application is ready."));
	}

	public static Result showForm() {
		return ok(program.render());
	}

	public static Result upload() {
		MultipartFormData body = request().body().asMultipartFormData();
		FilePart uploadedJar = body.getFile("jarFile");
		if (uploadedJar != null) {
			String fileName = uploadedJar.getFilename();
			String contentType = uploadedJar.getContentType();
			File uploadedFile = uploadedJar.getFile();
			File copiedFile = new File("." + File.separator + "uploads" + File.separator + uploadedJar.getFilename());
			try {
				Files.move(uploadedFile, copiedFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return ok(debugger.render(uploadedJar.getFilename()));
		} else {
			flash("error", "Missing file");
			return redirect(routes.Application.index());
		}
	}

	public static Result websocket() {
		return ok(websocket.render());
	}

	public static WebSocket<JsonNode> getWebsocket() {
		return new WebSocket<JsonNode>() {
			public void onReady(WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out) {
				ObjectNode breakpointJson = Json.newObject();
				breakpointJson.put("methodName", "Ruszamy!");
				out.write(breakpointJson);
			}
		};
	}

	public static WebSocket<JsonNode> debug(final String jarToDebug) {
		return new WebSocket<JsonNode>() {
			public void onReady(WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out) {
				ObjectNode breakpointJson = Json.newObject();
				breakpointJson.put("methodName", "Ruszamy!");
				out.write(breakpointJson);
				DebuggerWebsocketHandler.register(in, out, jarToDebug);
			}
		};
	}

}
