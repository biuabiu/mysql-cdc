package com.axxc.replicator.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadFactorys implements ThreadFactory {
	
	private AtomicInteger atomicInteger = new AtomicInteger();
	
	private String threadPrefixName;
	
	private boolean daemon;
	
	private ThreadFactorys(String threadPrefixName, boolean daemon) {
		super();
		this.threadPrefixName = threadPrefixName;
		this.daemon = daemon;
	}
	
	@Override
	public Thread newThread(Runnable r) {
		String threadName = threadPrefixName + atomicInteger.incrementAndGet();
		Thread newThread = new Thread(r, threadName);
		newThread.setDaemon(daemon);
		if (newThread.getPriority() != Thread.NORM_PRIORITY) {
			newThread.setPriority(Thread.NORM_PRIORITY);
		}
		return newThread;
	}
	
	public static ThreadFactory threadFactory(String threadPrefixName) {
		return new ThreadFactorys(threadPrefixName, false);
	}
	
	public static ThreadFactory threadFactory(String threadPrefixName, boolean daemon) {
		return new ThreadFactorys(threadPrefixName, daemon);
	}
	
}