package com.axxc.repicator.listener;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import com.axxc.replicator.utils.JSONUtils;
import com.axxc.replicator.work.event.DataSyncResultEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataSyncResultListener {
	
	@Async
	@EventListener
	public void listener(DataSyncResultEvent event) {
		log.info("to es , data {}", JSONUtils.toJSonAsString(event.getSource()));
	}
	
}
