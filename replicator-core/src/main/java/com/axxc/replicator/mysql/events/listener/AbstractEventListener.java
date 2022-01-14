package com.axxc.replicator.mysql.events.listener;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.io.Serializable;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.collections.*;
import org.apache.commons.lang3.ArrayUtils;

import com.axxc.replicator.mysql.data.*;
import com.axxc.replicator.mysql.disruptor.MySqlPublisher;
import com.github.shyiko.mysql.binlog.BinaryLogClient.EventListener;
import com.github.shyiko.mysql.binlog.event.*;

import lombok.extern.slf4j.Slf4j;

/**
 * @author: CharlesXiong
 * @date: 2021年8月30日下午2:47:34
 */

@Slf4j
public abstract class AbstractEventListener<E extends EventData> implements EventListener {
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public AbstractEventListener() {
		if (Objects.isNull(e)) {
			Class<? extends AbstractEventListener> thisClass = this.getClass();
			Type genType = thisClass.getGenericSuperclass();
			Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
			e = (Class<E>) params[0];
			log.info("{} init generic type {}", thisClass.getSimpleName(), e.getSimpleName());
		}
	}
	
	protected static Map<Long, TableMetadata> TABLE_METADATA_MAPS = new HashMap<>();
	
	protected static AtomicLong lastXider = new AtomicLong(0);
	
	protected static List<BinlogBatchData> batchs = new ArrayList<>();
	
	private Class<E> e;
	
	public abstract BinlogBatchData doListening(E data);
	
	@Override
	public void onEvent(Event event) {
		if (Objects.isNull(event)) return;
		if (Objects.isNull(event.getData())) return;
		if (!e.isAssignableFrom(event.getData().getClass())) return;
		BinlogBatchData batchData = this.doListening(event.getData());
		if (Objects.isNull(batchData)) {
			return;
		}
		this.publish(batchData);
	}
	
	// @formatter:off
	protected <T extends Serializable> BinlogBatchData convert(String type, long table, List<T[]> befores,List<T[]> afters) {
	// @formatter:on
		TableMetadata metadata = TABLE_METADATA_MAPS.get(table);
		if (Objects.isNull(metadata)) {
			log.info("TableMetadata isNull ignore, table={}", table);
			return null;
		}
		
		BinlogBatchData data = new BinlogBatchData();
		data.setDatabase(metadata.getDatabase());
		data.setTable(metadata.getTable());
		data.setType(type);
		data.setXid(lastXider.get());
		List<String> columnNames = Optional.ofNullable(metadata.getColumnNames()).orElseGet(ArrayList::new);
		int size = CollectionUtils.isNotEmpty(befores) ? befores.size()
				: CollectionUtils.isNotEmpty(afters) ? afters.size() : 0;
		for (int i = 0; i < size; i++) {
			Serializable[] before = CollectionUtils.isNotEmpty(befores) ? befores.get(i) : null;
			Serializable[] after = CollectionUtils.isNotEmpty(afters) ? afters.get(i) : null;
			Map<Object, Object> beforeHashMap = new LinkedHashMap<>();
			Map<Object, Object> afterHashMap = new LinkedHashMap<>();
			int length = ArrayUtils.isNotEmpty(before) ? before.length
					: ArrayUtils.isNotEmpty(after) ? after.length : 0;
			
			for (int j = 0; j < length; j++) {
				String columnName = String.valueOf(j);
				if (CollectionUtils.isEmpty(columnNames)) {
					// TODO 维护一份meta数据?
				} else {
					columnName = columnNames.get(j);
				}
				Serializable beforeValue = ArrayUtils.isNotEmpty(before) ? before[j] : null;
				Serializable afterValue = ArrayUtils.isNotEmpty(after) ? after[j] : null;
				if (!Objects.equals(beforeValue, afterValue)) data.getChange().add(columnName);
				if (Objects.nonNull(beforeValue)) beforeHashMap.put(columnName, beforeValue);
				if (Objects.nonNull(afterValue)) afterHashMap.put(columnName, afterValue);
			}
			if (MapUtils.isNotEmpty(beforeHashMap)) data.getBeforeDatas().add(beforeHashMap);
			if (MapUtils.isNotEmpty(afterHashMap)) data.getAfterDatas().add(afterHashMap);
		}
		
		return data;
	}
	
	private void publish(BinlogBatchData batchData) {
		if (batchData instanceof BinlogXidData) {
			long xid = batchData.getXid();
			lastXider.set(xid);
			
			// @formatter:off
			List<BinlogBatchData> targets = batchs.stream()
				.sorted(comparing(BinlogBatchData::getXid))
				.filter(d -> d.getXid() < xid)
				.peek(d -> d.setXid(xid))
				.peek(MySqlPublisher::publish)
				.collect(toList());
			// @formatter:on
			batchs.removeAll(targets);
			// log.info("xid = {},{}", xid, JSONUtils.toJSonAsString(batch));
		} else {
			batchs.add(batchData);
		}
	}
}
