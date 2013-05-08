package debugger.impl;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import models.DebuggerWebsocketHandler;

import org.aspectj.lang.JoinPoint;

import debugger.DebuggerInterface;

public class DebuggerWeb implements DebuggerInterface {
	private Lock lock = new ReentrantLock();

	@Override
	public void setup() {
		// TODO Auto-generated method stub

	}

	@Override
	public void takeCommand(JoinPoint point, StackTraceElement[] stack) {
		DebuggerWebsocketHandler.sendBreakpointMessage(point, stack, Thread.currentThread());
		try {
			while (true)
				Thread.sleep(10000);
		} catch (InterruptedException e) {
			return;
		}
	}

	@Override
	public void takeDown() {
		// TODO Auto-generated method stub

	}

}
