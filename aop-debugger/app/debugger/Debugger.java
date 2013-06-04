package debugger;

import java.util.HashSet;
import java.util.Set;

import org.aspectj.lang.JoinPoint;

import debugger.impl.DebuggerWeb;

public class Debugger {
	private static Debugger appInstance = null;
	private DebuggerInterface debuggerInterface = new DebuggerWeb();

	public boolean stepMode = false;
	public boolean debugOutsideJar = true;

	public Set<String> forbiddenPackages = new HashSet<String>();
	public Set<String> forbiddenClasses = new HashSet<String>();
	public Set<String> forbiddenMethods = new HashSet<String>();

	public static Debugger getInstance() {
		if (appInstance == null) {
			appInstance = new Debugger();
		}
		return appInstance;
	}

	private Debugger() {
	}

	// TODO: Add methods to ignore/enable classes and methods

	public Object[] arguments;

	public void takeControl(JoinPoint point, StackTraceElement[] stack,
			Object[] args) {
		if (shouldStop(point)) {
			arguments = args;
			debuggerInterface.takeCommand(point, clearStack(stack), arguments);
		}
	}

	StackTraceElement[] clearStack(StackTraceElement[] stack) {
		StackTraceElement[] newStack = new StackTraceElement[stack.length - 10];
		for (int i = 2, j = 0; i < stack.length - 8; i++, j++) {
			newStack[j] = stack[i];
		}
		return newStack;
		// Primitive approach: remove first 2 and last 8 lines
	}

	public boolean shouldStop(JoinPoint point) {
		if (stepMode)
			return true;
		Class type = point.getSignature().getDeclaringType();
		String className = type.getSimpleName();
		String packageName = type.getPackage().getName();
		String methodName = point.getSignature().getName();
		if (forbiddenPackages.contains(packageName)
				|| forbiddenClasses.contains(packageName + "." + className)
				|| forbiddenMethods.contains(packageName + "." + className + "." + methodName))
			return false;
		String jarPackage = debuggerInterface.getRootPackageOfDebuggedJar();
		if (!debugOutsideJar) {
			if (!packageName.startsWith(jarPackage))
				return false;
		}

		return true;
	}

	public void setInterface(DebuggerInterface debuggerInterface) {
		this.debuggerInterface = debuggerInterface;
	}

	public void continueExecution() {
	}
}
