package com.axxc.replicator.lifecycle;

public interface Lifecycle {
	
	void start();
	
	void stop();
	
	boolean isRunning();
	
}