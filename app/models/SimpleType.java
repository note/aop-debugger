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
		packageName = (clazz.getPackage() != null) ? clazz.getPackage().getName() : "";
		name = clazz.getSimpleName();
	}

	@Override
	public String toString() {
		return packageName + "." + name;
	}

}
