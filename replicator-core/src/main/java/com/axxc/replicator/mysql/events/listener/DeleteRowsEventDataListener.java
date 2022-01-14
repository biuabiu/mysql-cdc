package com.axxc.replicator.mysql.events.listener;
import com.axxc.replicator.mysql.data.BinlogBatchData;
import com.github.shyiko.mysql.binlog.event.DeleteRowsEventData;

/**
 * @author: CharlesXiong
 * @date: 2021年8月30日下午2:47:34
 * @notes 物理删除操作事件
 */
public class DeleteRowsEventDataListener extends AbstractEventListener<DeleteRowsEventData> {
	
	@Override
	public BinlogBatchData doListening(DeleteRowsEventData data) {
		return super.convert("DELETE", data.getTableId(), data.getRows(), null);
	}
	
}
