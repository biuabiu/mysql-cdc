package com.axxc.repicator.async;

import java.util.concurrent.*;

import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AsyncConfig implements AsyncConfigurer {
	
	@Override
	public Executor getAsyncExecutor() {
		log.info("init async ThreadPoolExecutor");
		ThreadFactory threadFactory = new CustomizableThreadFactory("Async-");
		return new ThreadPoolExecutor(5, 15, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1 << 11), threadFactory);
	}
	
}
