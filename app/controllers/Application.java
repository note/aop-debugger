package controllers;

import java.io.File;


import org.aspectj.weaver.loadtime.WeavingURLClassLoader;
import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import play.mvc.Controller;
import play.mvc.WebSocket;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import views.html.index;
import views.html.program;

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
		FilePart picture = body.getFile("picture");
		if (picture != null) {
			String fileName = picture.getFilename();
			String contentType = picture.getContentType();
			File uploadedFile = picture.getFile();
			try {
				File copiedFile = new File("." + File.separator + "uploads" + File.separator + picture.getFilename());
				Files.move(uploadedFile, copiedFile);

				URL[] url = { new URL("file://" + copiedFile.getAbsolutePath()), new URL("file:///usr/share/java/aspectjrt.jar"), new URL("file:///home/michal/Desktop/programming/code/java/LTW/timing.jar") };
				URLClassLoader loader = new URLClassLoader(url);
				WeavingURLClassLoader weaver = new WeavingURLClassLoader(loader);
				Class<?> cls = loader.loadClass("to.be.debugged.Simple");
				Method meth = cls.getMethod("main", String[].class);
				String[] params = null;
				meth.invoke(null, (Object) params);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
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
			}
			return ok("File uploaded");
		} else {
			flash("error", "Missing file");
			return redirect(routes.Application.index());
		}
	}

}
