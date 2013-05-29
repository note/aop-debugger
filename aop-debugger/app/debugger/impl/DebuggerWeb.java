package debugger.impl;

import models.DebuggerWebsocketHandler;

import org.aspectj.lang.JoinPoint;

import debugger.DebuggerInterface;

public class DebuggerWeb implements DebuggerInterface {

	// TODO: add override annotation (removed in hurry - ajc complained)
	public void setup() {
		// TODO Auto-generated method stub

	}

	public void takeCommand(JoinPoint point, StackTraceElement[] stack) {
		Thread thread = Thread.currentThread();
		DebuggerWebsocketHandler.sendBreakpointMessage(point, stack, thread);
		synchronized (thread) {
			try {
				Thread.currentThread().wait();
			} catch (InterruptedException e) {
			}
		}
	}

	public String getRootPackageOfDebuggedJar() {
		return DebuggerWebsocketHandler.getRootPackageOfDebuggedJar();
	}

	public void takeDown() {

	}

}
