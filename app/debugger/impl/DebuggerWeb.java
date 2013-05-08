package debugger.impl;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import models.DebuggerWebsocketHandler;

import org.aspectj.lang.JoinPoint;

import debugger.DebuggerInterface;

public class DebuggerWeb implements DebuggerInterface {
	private Lock lock = new ReentrantLock();

	//TODO: add override annotation (removed in hurry - ajc complained)
	public void setup() {
		// TODO Auto-generated method stub

	}

	public void takeCommand(JoinPoint point, StackTraceElement[] stack) {
		DebuggerWebsocketHandler.sendBreakpointMessage(point, stack, Thread.currentThread());
		try {
			while (true)
				Thread.sleep(10000);
		} catch (InterruptedException e) {
			return;
		}
	}

	public void takeDown() {
		// TODO Auto-generated method stub

	}

}
