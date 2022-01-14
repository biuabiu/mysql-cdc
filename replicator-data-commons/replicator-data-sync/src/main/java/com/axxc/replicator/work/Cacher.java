package com.axxc.replicator.work;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.*;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class Cacher {
	
	private static ConcurrentMap<String, CacheEntity> CACHE = new ConcurrentHashMap<>(512);
	
	// 3分钟
	private static final int FIXED_TIMES = 1000 * 60 * 3;
	
	private static final int INITIAL_DELAY_TIMES = 5000;
	private static final AtomicInteger CLEAN_COUNTER = new AtomicInteger();
	private volatile static long LAST_CLEAN_TIMESTAMP;
	private volatile static HashMap<String, CacheEntity> TEMP = new HashMap<>();
	static {
		CustomizableThreadFactory threadFactory = new CustomizableThreadFactory(
				ClassUtils.getShortClassName(Cacher.class) + "Clean-");
		threadFactory.setDaemon(true);
		ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1, threadFactory);
		
		Runnable cleaner = () -> {
			CLEAN_COUNTER.incrementAndGet();
			LAST_CLEAN_TIMESTAMP = System.currentTimeMillis();
			if (log.isTraceEnabled()) log.trace("#{} Cleaning the cache ", CLEAN_COUNTER.get());
			TEMP.putAll(CACHE);
			TEMP.values().forEach(entry -> {
				long currentTimeMillis = System.currentTimeMillis();
				long bornTimestamp = entry.getBornTimestamp();
				if (currentTimeMillis - bornTimestamp >= FIXED_TIMES) {
					CACHE.remove(entry.getKey());
					if (log.isTraceEnabled()) log.trace("#{} Clean the cache , {}", CLEAN_COUNTER.get(), entry);
				}
			});
			if (MapUtils.isNotEmpty(TEMP)) TEMP.clear();
			if (log.isTraceEnabled()) log.trace("#{} Cleaned the cache ", CLEAN_COUNTER.get());
		};
		scheduledExecutorService.scheduleAtFixedRate(cleaner, INITIAL_DELAY_TIMES, FIXED_TIMES, TimeUnit.MILLISECONDS);
	}
	
	public static boolean containsKey(String key) {
		return CACHE.containsKey(key);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T get(String key, Class<T> returnClazz) {
		CacheEntity value = CACHE.get(key);
		if (Objects.isNull(value)) return null;
		if (log.isDebugEnabled()) log.debug("#get the cache,key={},value={}", key, value.getValue());
		return (T) value.getValue();
	}
	
	public static int getCleanCount() {
		return CLEAN_COUNTER.get();
	}
	
	public static long getLastCleanTimestamp() {
		return LAST_CLEAN_TIMESTAMP;
	}
	
	public static void put(String key, Object value) {
		if (StringUtils.isBlank(key) || Objects.isNull(value)) return;
		if (log.isDebugEnabled()) log.debug("#put the cache,key={},value={}", key, value);
		CacheEntity entry = new CacheEntity(key, value);
		CACHE.putIfAbsent(key, entry);
	}
	
	public static void remove(String key) {
		if (StringUtils.isBlank(key)) return;
		CACHE.put(key, null);
		CACHE.remove(key);
		if (log.isDebugEnabled()) log.debug("#remove the cache,key={}", key);
	}
	
	@Data
	private static class CacheEntity {
		
		public CacheEntity(String key, Object value) {
			super();
			this.key = key;
			this.value = value;
		}
		
		private String key;
		private Object value;
		
		private long bornTimestamp = System.currentTimeMillis();
	}
	
}
