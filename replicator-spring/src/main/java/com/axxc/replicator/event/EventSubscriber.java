package com.axxc.replicator.event;

import java.util.concurrent.Executor;

import com.axxc.replicator.mysql.data.BinlogBatchData;

public interface EventSubscriber {
	
	void consume(BinlogBatchData data);
	
	Executor executor();
	
	String topic();
	
}
