package controllers.helpers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import models.Clazz;

import com.google.common.collect.Sets;

public class ReflectionHelper {
	public static Set<Clazz> getClassesFromJar(File jarFile) {
		Set<Clazz> classes = null;
		try {
			classes = getClassesFromJar(jarFile.toURI().toURL());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return classes;
	}

	public static Set<Clazz> getClassesFromJar(URL jarFileURL) {
		Set<Clazz> classes = null;
		try {
			ZipInputStream zip = new ZipInputStream(jarFileURL.openStream());
			URL[] url = { jarFileURL };
			URLClassLoader loader = new URLClassLoader(url);
			return getClassesFromStream(zip, loader);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return classes;
	}

	public static Set<Clazz> getMainClassesFromJar(URL jarFileURL) {
		Set<Clazz> classes = getClassesFromJar(jarFileURL);
		Set<Clazz> mainClasses = Sets.newHashSet();
		for (Clazz clazz : classes) {
			if (clazz.containsMainMethod())
				mainClasses.add(clazz);
		}
		return mainClasses;
	}

	private static Set<Clazz> getClassesFromStream(ZipInputStream zipInputStream, ClassLoader classLoader) {
		Set<Clazz> classes = Sets.newHashSet();
		try {
			ZipEntry zipEntry = zipInputStream.getNextEntry();
			while (zipInputStream.available() == 1) {
				if (zipEntry.getName().endsWith(".class")) {
					Clazz clazz = new Clazz(zipEntry, classLoader);
					if (clazz.isClass())
						classes.add(clazz);
				}
				zipEntry = zipInputStream.getNextEntry();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return classes;
	}
}
