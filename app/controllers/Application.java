package controllers;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import models.Clazz;
import models.DebuggerWebsocketHandler;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.mvc.WebSocket;
import views.html.index;
import views.html.program;

import com.google.common.io.Files;

import controllers.helpers.ReflectionHelper;

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
			File copiedFile = copyToUploads(uploadedJar);

			Set<Clazz> classesFromJar = ReflectionHelper.getClassesFromJar(copiedFile);
			return ok(Json.toJson(classesFromJar));
		} else {
			flash("error", "Missing file");
			return redirect(routes.Application.index());
		}
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

	private static File copyToUploads(FilePart uploadedFilePart) {
		String fileName = uploadedFilePart.getFilename();
		String contentType = uploadedFilePart.getContentType();
		File uploadedFile = uploadedFilePart.getFile();
		File copiedFile = new File(getUploadsPath() + uploadedFilePart.getFilename());
		try {
			Files.move(uploadedFile, copiedFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return copiedFile;
	}

	private static String getUploadsPath() {
		return "." + File.separator + "uploads" + File.separator;
	}

}
