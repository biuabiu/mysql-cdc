package com.axxc.replicator.mysql.events.listener;

import static java.util.stream.Collectors.toList;

import java.io.Serializable;
import java.util.List;
import java.util.Map.Entry;

import com.axxc.replicator.mysql.data.BinlogBatchData;
import com.github.shyiko.mysql.binlog.event.UpdateRowsEventData;

/**
 * @author: CharlesXiong
 * @date: 2021年8月30日下午2:47:34
 * @notes 更新操作事件
 */
public class UpdateRowsEventDataListener extends AbstractEventListener<UpdateRowsEventData> {
	
	@Override
	public BinlogBatchData doListening(UpdateRowsEventData data) {
		
		List<Entry<Serializable[], Serializable[]>> rows = data.getRows();
		List<Serializable[]> afterRows = rows.stream().map(Entry::getValue).collect(toList());
		List<Serializable[]> beforeRows = rows.stream().map(Entry::getKey).collect(toList());
		
		return super.convert("UPDATE", data.getTableId(), beforeRows, afterRows);
	}
	
}
