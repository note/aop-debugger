package threads;

import helpers.PathHelpers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;

import models.Clazz;
import models.DebuggerWebsocketHandler;

import org.aspectj.weaver.loadtime.WeavingURLClassLoader;

import streams.ThreadPrintStream;
import controllers.helpers.ReflectionHelper;

public class DebuggedProgramThread implements Runnable {
	private static final String OUTPUT_DIR_PATH = "standardOutputs";
	private static final String OUTPUT_FILE_EXT = ".txt";
	private Clazz classWithMain;
	private String jarToDebug;

	public DebuggedProgramThread(String jarToDebug) {
		this.jarToDebug = jarToDebug;
		try {
			URL jarToDebugURL = new URL(PathHelpers.getUploadsURL() + jarToDebug);
			Set<Clazz> classes = ReflectionHelper.getMainClassesFromJar(jarToDebugURL);
			if (classes.size() == 0)
				throw new IllegalArgumentException("Uploaded jar does not contain main method");
			classWithMain = classes.iterator().next();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		replaceSystemOut();
		invokeMainMethod(jarToDebug);
		System.out.close();
	}

	public Clazz getClassWithMain() {
		return classWithMain;
	}

	private void replaceSystemOut() {
		try {
			FileOutputStream fos = new FileOutputStream(DebuggedProgramThread.getOutputFilePathForCurrentThread());
			PrintStream stream = new PrintStream(new BufferedOutputStream(fos));
			((ThreadPrintStream) System.out).setThreadOut(stream);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void invokeMainMethod(final String jarToDebug) {
		try {
			URL jarToDebugURL = new URL(PathHelpers.getUploadsURL() + jarToDebug);
			URL[] url = { jarToDebugURL, new URL(PathHelpers.getPwdURL() + "debugger.jar") };
			URLClassLoader loader = new URLClassLoader(url, DebuggerWebsocketHandler.class.getClassLoader());
			WeavingURLClassLoader weaver = new WeavingURLClassLoader(loader);
			Class<?> cls = weaver.loadClass(classWithMain.getQualifiedName());
			Method meth = cls.getMethod("main", String[].class);
			String[] params = null;
			meth.invoke(null, (Object) params);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String getOutputFilePathForCurrentThread() {
		String threadName = Thread.currentThread().getName();
		String filePath = OUTPUT_DIR_PATH + File.separator + threadName + OUTPUT_FILE_EXT;
		return filePath;
	}
}
