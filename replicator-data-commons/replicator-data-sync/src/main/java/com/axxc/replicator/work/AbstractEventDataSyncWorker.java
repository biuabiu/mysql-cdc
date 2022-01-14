package com.axxc.replicator.work;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.stream.Collectors.toList;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

import com.axxc.replicator.event.AbstractEventSubscriber;
import com.axxc.replicator.mysql.data.BinlogBatchData;
import com.axxc.replicator.spring.annotation.*;
import com.axxc.replicator.utils.BeanUtils;
import com.axxc.replicator.work.annotation.DataSyncListener;
import com.axxc.replicator.work.event.DataSyncResultEvent;
import com.axxc.replicator.work.sync.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractEventDataSyncWorker extends AbstractEventSubscriber {
	
	static HashMap<String, Set<DataSyncMethod>> SUBSCRIBERS_MAP = new HashMap<>();
	
	@Autowired
	ApplicationContext applicationContext;
	
	@Autowired(required = false)
	Set<DataSyncAccessor> accessors;
	
	@Override
	public void afterSingletonsInstantiated() {
		super.afterSingletonsInstantiated();
		this.initSubsciber();
	}
	
	@Override
	public void consume(BinlogBatchData data) {
		// TODO 黑洞功能?
		Set<DataSyncMethod> set = SUBSCRIBERS_MAP.get(data.getTable());
		if (CollectionUtils.isEmpty(set)) return;
		BinlogBatchData copy = this.immutable(data);
		for (DataSyncMethod method : set) {
			String key = method.getSubscriberName() + data.getXid();
			if (method.isSkip() && skip(key, method)) {
				if (CollectionUtils.isNotEmpty(accessors))
					accessors.forEach(acc -> acc.skip(method, Cacher.get(key, DataSyncMethod.class), copy));
				continue;
			}
			
			AtomicReference<Exception> reference = new AtomicReference<>();
			CompletableFuture<Object> future = supplyAsync(() -> {
				try {
					if (CollectionUtils.isNotEmpty(accessors)) accessors.forEach(acc -> acc.invokeBefore(method, copy));
					return MethodUtils.invokeMethod(method.getSubscriber(), true, method.getMethod().getName(), data);
				} catch (Exception e) {
					reference.set(e);
					return null;
				} finally {
					if (CollectionUtils.isNotEmpty(accessors))
						accessors.forEach(acc -> acc.invokeAfter(method, copy, reference.get()));
					reference.set(null);
				}
			}, executor());
			
			try {
				Object object = future.get(15, timeoutUint());
				if (Objects.nonNull(object)) {
					this.applicationContext.publishEvent(new DataSyncResultEvent(object));
				}
			} catch (Exception e) {
				reference.set(e);
			} finally {
				reference.get();
				// TODO log
			}
		}
	}
	
	protected TimeUnit timeoutUint() {
		// 这里放开一个调试口子供debug
		return TimeUnit.SECONDS;
	}
	
	private BinlogBatchData immutable(BinlogBatchData data) {
		BinlogBatchData copy = null;
		if (CollectionUtils.isNotEmpty(accessors)) {
			copy = BeanUtils.copy(data, BinlogBatchData.class);
			if (log.isDebugEnabled()) {
				log.debug("has more DataSyncAccessor , build immutable data");
			}
		}
		return copy;
	}
	
	private void initSubsciber() {
		String database = topic();
		log.debug("init methods for database {}", database);
		
		Map<String, Object> maps1 = applicationContext.getBeansWithAnnotation(DataValue.class);
		Map<String, Object> maps2 = applicationContext.getBeansWithAnnotation(DataValueLists.class);
		
		Arrays.asList(maps1.values(), maps2.values()).stream().flatMap(Collection::stream).forEach(v -> {
			DataValue dataValue = AnnotationUtils.findAnnotation(v.getClass(), DataValue.class);
			List<DataValue> datas = new ArrayList<>();
			if (Objects.nonNull(dataValue)) {
				datas.add(dataValue);
			}
			
			DataValueLists dataValueLists = AnnotationUtils.findAnnotation(v.getClass(), DataValueLists.class);
			if (Objects.nonNull(dataValueLists)) {
				List<DataValue> collect = Stream.of(dataValueLists.value()).collect(toList());
				datas.addAll(collect);
			}
			List<String> datavalues = datas.stream().map(DataValue::value).distinct().collect(toList());
			log.info("bean {} subsciber database {}", v.getClass(), datavalues);
			
			// 感觉这里的一段逻辑可以不要,这里由topic决定是否有消息进来,这里有消息进来,handler就可以处理
			// 所以即使这里构造了method 但是没有消息进来,也无法消费
			// @formatter:off
			if (datavalues.contains(database)) {
				MethodUtils.getMethodsListWithAnnotation(v.getClass(), DataSyncListener.class, true, true).forEach(m -> {
					DataSyncListener listener = MethodUtils.getAnnotation(m, DataSyncListener.class, false, true);
					if (Objects.nonNull(listener)) {
						String[] tables = listener.tables();
						for (String table : tables) {
							DataSyncMethod method = DataSyncMethod.builder()
									.database(database)
									.table(table)
									.skip(listener.skip())
									.method(m)
									.subscriber(v)
									.subscriberName(v.getClass().getSimpleName())
									.build();
							SUBSCRIBERS_MAP.computeIfAbsent(table, key -> new HashSet<>()).add(method);
						}
					}
				});
			// @formatter:on
			}
		});
	}
	
	private boolean skip(String key, DataSyncMethod method) {
		boolean skip = false;
		synchronized (log) {
			skip = Cacher.containsKey(key);
			if (!skip) {
				Cacher.put(key, method);
			}
		}
		return skip;
	}
	
}
