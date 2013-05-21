package models;

import java.io.File;
import java.util.Set;
import java.util.zip.ZipEntry;

import org.codehaus.jackson.annotate.JsonIgnore;

public class Clazz {
	private Set<MyMethod> methods;
	private Class<?> clazz;

	public Clazz(ZipEntry zipEntry, ClassLoader loader) {
		try {
			String className = fullyQualifiedFromPath(zipEntry.getName());
			clazz = loader.loadClass(className);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (!(obj instanceof Clazz))
			return false;

		Clazz comparable = (Clazz) obj;
		return clazz.equals(comparable.clazz);
	}

	@JsonIgnore
	public boolean isClass() {
		return !clazz.isInterface();
	}

	public String getPackageName() {
		return clazz.getPackage().getName();
	}

	public String getName() {
		return clazz.getSimpleName();
	}

	public Set<MyMethod> getMethods() {
		return methods;
	}

	private String fullyQualifiedFromPath(String path) {
		path = path.substring(0, path.indexOf("."));
		return path.replace(File.separator, ".");
	}
}
