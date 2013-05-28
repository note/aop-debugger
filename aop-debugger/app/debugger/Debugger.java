package debugger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.aspectj.lang.JoinPoint;

import debugger.impl.DebuggerWeb;

public class Debugger {
	private static Debugger appInstance = null;
	private DebuggerInterface debuggerInterface = new DebuggerWeb();
	
	public boolean stepMode = false;
	public boolean debugOutsideJar = true;
	
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

	public void takeControl(JoinPoint point, StackTraceElement[] stack) {
		if(shouldStop(point))
			debuggerInterface.takeCommand(point, clearStack(stack));
	}
	
	StackTraceElement[] clearStack(StackTraceElement[] stack) {
		StackTraceElement[] newStack = new StackTraceElement[stack.length - 10];
		for(int i = 2, j = 0; i < stack.length - 8; i++, j++){
			newStack[j] = stack[i];
		}
		return newStack;
		// Primitive approach: remove first 2 and last 8 lines 
	}
	
	public boolean shouldStop(JoinPoint point) {
		if(stepMode) return true;
		Class type = point.getSignature().getDeclaringType();
		String className = type.getSimpleName();
		String packageName = type.getPackage().getName();
		String methodName = point.getSignature().getName();
		if(forbiddenClasses.contains(packageName + "." + className)) return false;
		if(forbiddenMethods.contains(packageName + "." + className + "." + methodName)) return false;
		String jarPackage = "to.be.debugged"; // TODO unhardcode it
		if(!debugOutsideJar) {
			if(! packageName.startsWith(jarPackage))
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
