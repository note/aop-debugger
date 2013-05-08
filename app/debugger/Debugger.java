package debugger;

import org.aspectj.lang.JoinPoint;

import debugger.impl.DebuggerCLI;

public class Debugger {
	private static Debugger appInstance = null;
	private DebuggerInterface debuggerInterface = new DebuggerCLI();

	public static Debugger getInstance() {
		if (appInstance == null) {
			appInstance = new Debugger();
		}
		return appInstance;
	}

	private Debugger() {
	}

	public void takeControl(JoinPoint point, StackTraceElement[] stack) {
		debuggerInterface.takeCommand(point, stack);
	}

	public void setInterface(DebuggerInterface debuggerInterface) {
		this.debuggerInterface = debuggerInterface;
	}

	public void continueExecution() {
	}
}
