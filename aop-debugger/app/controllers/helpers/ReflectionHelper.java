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
			ZipInputStream zip = new ZipInputStream(jarFile.toURI().toURL().openStream());
			URL[] url = { jarFile.toURI().toURL() };
			URLClassLoader loader = new URLClassLoader(url);
			return getClassesFromStream(zip, loader);
		} catch (MalformedURLException e) {// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return classes;
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
