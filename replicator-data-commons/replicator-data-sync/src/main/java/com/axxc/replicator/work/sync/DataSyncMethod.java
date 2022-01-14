package com.axxc.replicator.work.sync;

import java.lang.reflect.Method;

import lombok.*;

@Builder
@Getter
public class DataSyncMethod {
	
	private final String database;
	
	private String table;
	
	private boolean skip;
	
	private Method method;
	
	private Object subscriber;
	
	private String subscriberName;
}
