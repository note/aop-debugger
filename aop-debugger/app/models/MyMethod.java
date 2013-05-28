package models;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class MyMethod {
	private ScopeModifier scopeModifier;
	private List<Type> argumentsTypes = Lists.newArrayList();
	private Type returnType;
	private Method method;

	public MyMethod(Method method) {
		this.method = method;
		returnType = new SimpleType(method.getReturnType());
		initializeArgumentsTypes();
		initializeScopeModifier();
	}

	public ScopeModifier getScopeModifier() {
		return scopeModifier;
	}

	public String getName() {
		return method.getName();
	}

	public List<Type> getArgumentsTypes() {
		return argumentsTypes;
	}

	public Type getReturnType() {
		return returnType;
	}

	@Override
	public String toString() {
		String result = getName() + "(";
		result += Joiner.on(", ").join(argumentsTypes);
		result += "): " + returnType;
		return result;
	}

	private void initializeScopeModifier() {
		int modifiers = method.getModifiers();
		if (Modifier.isPrivate(modifiers)) {
			scopeModifier = ScopeModifier.PRIVATE;
			return;
		}
		if (Modifier.isProtected(modifiers)) {
			scopeModifier = ScopeModifier.PROTECTED;
			return;
		}
		if (Modifier.isPublic(modifiers)) {
			scopeModifier = ScopeModifier.PUBLIC;
			return;
		}
		scopeModifier = ScopeModifier.DEFAULT;
	}

	private void initializeArgumentsTypes() {
		Class<?>[] classes = method.getParameterTypes();
		for (Class<?> clazz : classes)
			argumentsTypes.add(new SimpleType(clazz));
	}

	private String expectedMainMethodName() {
		return "public static void " + method.getDeclaringClass().getName() + ".main(java.lang.String[])";
	}

	public boolean isMainMethod() {
		return expectedMainMethodName().equals(method.toString());
	}
}
