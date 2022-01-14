package com.axxc.replicator.disruptor;

import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ClassUtils;

import com.axxc.replicator.lifecycle.Lifecycle;
import com.axxc.replicator.utils.ThreadFactorys;
import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.*;

import lombok.*;

@Builder
public class Disruptors<T> implements Lifecycle {
	
	private final EventFactory<T> eventFactory;
	
	@Singular("handlers")
	private final List<EventHandler<T>> handlers;
	
	private final AtomicReference<Disruptor<T>> reference = new AtomicReference<>();
	
	private final int ringBufferSize;
	
	private final AtomicBoolean runner = new AtomicBoolean(false);
	
	private final WaitStrategy waitStrategy = new BlockingWaitStrategy();
	
	public Disruptor<T> disruptor() {
		return reference.get();
	}
	
	@Override
	public boolean isRunning() {
		return runner.get();
	}
	
	@Override
	public void start() {
		if (!runner.compareAndSet(false, true)) return;
		ThreadFactory tf = ThreadFactorys.threadFactory(ClassUtils.getShortClassName(this.getClass()) + "-");
		Disruptor<T> disruptor = new Disruptor<>(eventFactory, ringBufferSize, tf, ProducerType.SINGLE, waitStrategy);
		if (CollectionUtils.isNotEmpty(handlers)) {
			handlers.forEach(disruptor::handleEventsWith);
		}
		disruptor.start();
		reference.set(disruptor);
	}
	
	@Override
	public void stop() {
		if (runner.compareAndSet(true, false)) {
			reference.get().shutdown();
		}
	}
	
}
