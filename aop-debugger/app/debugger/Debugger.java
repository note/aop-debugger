package debugger;

import org.aspectj.lang.JoinPoint;

import debugger.impl.DebuggerWeb;

public class Debugger {
	private static Debugger appInstance = null;
	private DebuggerInterface debuggerInterface = new DebuggerWeb();

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
			debuggerInterface.takeCommand(point, stack);
	}
	
	public boolean shouldStop(JoinPoint point) {
		//		String packageName = point.getTarget().getClass().getPackage().getName();
//		String className = point.getTarget().getClass().getSimpleName();
//		String methodName = point.getSignature().getName();
//		System.out.println(packageName + "  " + className + "  " + methodName);
		return true;
	}

	public void setInterface(DebuggerInterface debuggerInterface) {
		this.debuggerInterface = debuggerInterface;
	}

	public void continueExecution() {
	}
}
