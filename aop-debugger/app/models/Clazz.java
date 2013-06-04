package models;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.google.common.collect.Sets;

public class Clazz implements Type {
	private Set<MyMethod> methods = Sets.newHashSet();
	private Class<?> clazz;
	
	public static Map<String, ArrayList<Clazz>> sortByPackage(Set<Clazz> klasses) {
		Map<String, ArrayList<Clazz>> out = new HashMap<String, ArrayList<Clazz>>();
		for(Clazz klass : klasses) {
			String packageName = klass.getPackageName();
			if(!out.containsKey(packageName)) {
				out.put(packageName, new ArrayList<Clazz>());
			}
			out.get(packageName).add(klass);
		}
		
		return out;
	}

	public Clazz(ZipEntry zipEntry, ClassLoader loader) {
		try {
			String className = fullyQualifiedFromPath(zipEntry.getName());
			clazz = loader.loadClass(className);

			initializeMethods();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initializeMethods() {
		Method[] fetchedMethods = clazz.getDeclaredMethods();
		for (Method method : fetchedMethods)
			methods.add(new MyMethod(method));
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

	public String getQualifiedName() {
		return getPackageName() + "." + getName();
	}

	public Set<MyMethod> getMethods() {
		return methods;
	}

	public boolean containsMainMethod() {
		for (MyMethod method : methods) {
			if (method.isMainMethod())
				return true;
		}
		return false;
	}

	private String fullyQualifiedFromPath(String path) {
		path = path.substring(0, path.indexOf("."));
		return path.replace(File.separator, ".");
	}
}
