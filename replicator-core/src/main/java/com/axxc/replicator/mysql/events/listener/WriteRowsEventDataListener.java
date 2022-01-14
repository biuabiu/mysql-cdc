package com.axxc.replicator.mysql.events.listener;

import com.axxc.replicator.mysql.data.BinlogBatchData;
import com.github.shyiko.mysql.binlog.event.WriteRowsEventData;

/**
 * @author: CharlesXiong
 * @date: 2021年8月30日下午2:47:34
 * @notes 写入操作事件
 */
public class WriteRowsEventDataListener extends AbstractEventListener<WriteRowsEventData> {
	
	@Override
	public BinlogBatchData doListening(WriteRowsEventData data) {
		return super.convert("INSERT", data.getTableId(), null, data.getRows());
	}
	
}
