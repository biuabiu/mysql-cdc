package com.axxc.replicator.event.dispatch;

import static java.util.concurrent.CompletableFuture.runAsync;

import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import com.axxc.replicator.event.EventSubscriber;
import com.axxc.replicator.mysql.data.BinlogBatchData;
import com.axxc.replicator.utils.JSONUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventDispatcher {
	
	@Autowired(required = false)
	Set<EventSubscriber> subscribers;
	
	@Async
	@EventListener
	public void listener(BinlogBatchData data) {
		if (log.isDebugEnabled()) log.debug("spring bean listener data : {} ", JSONUtils.toJSonAsString(data));
		if (CollectionUtils.isEmpty(subscribers)) return;
		// @formatter:off
		subscribers.stream()
			.filter(sub -> Objects.equals(data.getDatabase(), sub.topic()))
			.forEach(sub ->runAsync(()->sub.consume(data), sub.executor()));
		// @formatter:on
	}
}
