package com.axxc.replicator.mysql.disruptor;

import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.stream.Collectors.toList;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.ClassUtils;

import com.axxc.replicator.disruptor.Disruptors;
import com.axxc.replicator.mysql.data.BinlogBatchData;
import com.axxc.replicator.utils.*;
import com.lmax.disruptor.dsl.Disruptor;

public class MySqlPublisher {
	
	private static Disruptor<BinlogBatchData> disruptor;
	private static Executor executor;
	static {
		ThreadFactory threadFactory = ThreadFactorys.threadFactory(ClassUtils.getShortClassName(MySqlPublisher.class));
		executor = Executors.newFixedThreadPool(1, threadFactory);
		
		// @formatter:off
		List<MySqlHandler> handlers = StreamSupport.stream(ServiceLoader.load(MySqlHandler.class).spliterator(), false).collect(toList());
		Disruptors<BinlogBatchData> disruptors = Disruptors.<BinlogBatchData>builder()
				.eventFactory(BinlogBatchData::new)
				.handlers(handlers)
				.ringBufferSize(1 << 10)
				.build();
		disruptors.start();
		disruptor = disruptors.disruptor();
		// @formatter:on
	}
	
	public static void publish(BinlogBatchData data) {
		runAsync(() -> disruptor.publishEvent((e, s) -> BeanUtils.copy(data, e)), executor);
	}
	
}
