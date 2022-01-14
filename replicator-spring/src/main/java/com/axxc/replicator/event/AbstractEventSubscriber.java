package com.axxc.replicator.event;

import java.util.concurrent.*;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.util.ClassUtils;

public abstract class AbstractEventSubscriber
		implements EventSubscriber, EventThreadWorker, SmartInitializingSingleton {
	
	ThreadPoolExecutor executor = new ThreadPoolExecutor(this.max(), this.max(), 1L, TimeUnit.SECONDS,
			new LinkedBlockingQueue<>(), new CustomizableThreadFactory(ClassUtils.getShortName(getClass()) + "-"));
	
	@Override
	public void afterSingletonsInstantiated() {
		executor.prestartAllCoreThreads();
	}
	
	@Override
	public Executor executor() {
		return executor;
	}
}
