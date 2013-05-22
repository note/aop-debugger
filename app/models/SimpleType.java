package models;

public class SimpleType implements Type {
	private String packageName;
	private String name;

	@Override
	public String getPackageName() {
		return packageName;
	}

	@Override
	public String getName() {
		return name;
	}

	public SimpleType(Class<?> clazz) {
		packageName = (clazz.getPackage() != null) ? clazz.getPackage().getName() : null;
		name = clazz.getSimpleName();
	}

	@Override
	public String toString() {
		if (packageName == null)
			return name;
		else
			return packageName + "." + name;
	}

}
