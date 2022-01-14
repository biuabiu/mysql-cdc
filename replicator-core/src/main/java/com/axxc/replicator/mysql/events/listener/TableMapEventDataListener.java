package com.axxc.replicator.mysql.events.listener;

import java.util.*;

import com.axxc.replicator.mysql.data.*;
import com.github.shyiko.mysql.binlog.event.*;

/**
 * @author: CharlesXiong
 * @date: 2021年8月30日下午2:47:34
 * @notes 表元数据事件
 */
public class TableMapEventDataListener extends AbstractEventListener<TableMapEventData> {
	
	@Override
	public BinlogBatchData doListening(TableMapEventData data) {
		// https://github.com/osheroff/mysql-binlog-connector-java/issues/54
		// MySQL 8.0+ and MariaDB 10.5+
		// 开启 binlog_row_metadata 即可拿到
		TableMapEventData tableMapEventData = (TableMapEventData) data;
		long tableId = tableMapEventData.getTableId();
		String database = tableMapEventData.getDatabase();
		String table = tableMapEventData.getTable();
		TableMapEventMetadata metadata = tableMapEventData.getEventMetadata();
		
		TableMetadata tableMetadata = new TableMetadata();
		tableMetadata.setDatabase(database);
		tableMetadata.setTable(table);
		tableMetadata.setTableId(tableId);
		if (Objects.nonNull(metadata)) {
			List<String> columnNames = metadata.getColumnNames();
			tableMetadata.setColumnNames(columnNames);
		}
		
		TABLE_METADATA_MAPS.put(tableId, tableMetadata);
		
		return null;
	}
	
}
