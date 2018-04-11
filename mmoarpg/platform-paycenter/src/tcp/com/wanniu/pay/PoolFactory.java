package com.wanniu.pay;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author agui
 *
 */
public final class PoolFactory implements ThreadFactory {

	static final AtomicInteger poolNumber = new AtomicInteger(1);
	final AtomicInteger threadNumber = new AtomicInteger(1);
	final String namePrefix;
	private boolean daemon = false;
	private int priority = Thread.NORM_PRIORITY;

	public void setDaemon(boolean daemon) {
		this.daemon = daemon;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public PoolFactory(String name) {
		namePrefix = "pool-" + poolNumber.getAndIncrement() + "-" + name;
	}
	
	public PoolFactory(String name, boolean deamon) {
		this(name);
		this.daemon = deamon;	
	}

	public Thread newThread(Runnable r) {
		Thread t = new Thread(r, namePrefix + threadNumber.getAndIncrement());
		t.setDaemon(daemon);
		t.setPriority(priority);
		return t;
	}
}
