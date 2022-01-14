package com.axxc.replicator.handler;

import org.springframework.context.*;

import com.axxc.replicator.mysql.data.BinlogBatchData;
import com.axxc.replicator.mysql.disruptor.MySqlHandler;

public class InternalMysqlHandler implements MySqlHandler, ApplicationEventPublisherAware {
	static ApplicationEventPublisher applicationEventPublisher;
	
	@Override
	public void onEvent(BinlogBatchData event, long sequence, boolean endOfBatch) throws Exception {
		applicationEventPublisher.publishEvent(event);
	}
	
	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		InternalMysqlHandler.applicationEventPublisher = applicationEventPublisher;
	}
	
}
