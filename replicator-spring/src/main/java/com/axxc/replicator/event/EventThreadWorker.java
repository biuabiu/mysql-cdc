package com.axxc.replicator.event;

public interface EventThreadWorker {
	
	default int max() {
		return 4;
	}
}