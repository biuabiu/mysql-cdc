package com.axxc.replicator.work.event;

import org.springframework.context.ApplicationEvent;

public class DataSyncResultEvent extends ApplicationEvent {
	
	public DataSyncResultEvent(Object source) {
		super(source);
	}
	
}
