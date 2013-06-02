package debugger.impl;

import java.io.IOException;

import org.aspectj.lang.JoinPoint;

import debugger.Debugger;
import debugger.DebuggerInterface;

public class DebuggerCLI implements DebuggerInterface {

	public void takeCommand(JoinPoint point, StackTraceElement[] stack, Object[] args) {
		System.out.println("Method: " + point.getSignature().getName());

		waitForInput();

		Debugger debugger = Debugger.getInstance();
		debugger.continueExecution();

	}

	private void waitForInput() {
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setup() {

	}

	public void takeDown() {

	}

	@Override
	public String getRootPackageOfDebuggedJar() {
		// TODO Auto-generated method stub
		return null;
	}

}
