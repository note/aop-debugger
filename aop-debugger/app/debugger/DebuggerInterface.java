package debugger;

import org.aspectj.lang.JoinPoint;

public interface DebuggerInterface {
	void setup();

	void takeCommand(JoinPoint point, StackTraceElement[] stack, Object[] args);

	void takeDown();

	public String getRootPackageOfDebuggedJar();
}
