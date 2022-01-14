package com.axxc.replicator.mysql.data;

import java.util.List;

import lombok.Data;

@Data
public class TableMetadata {
	// 数据库名
	private String database;
	
	// 表名
	private String table;
	
	// 表id
	private long tableId;
	
	// 表字段名
	// MySQL 8.0+ and MariaDB 10.5+
	// 开启 binlog_row_metadata 即可拿到
	private List<String> columnNames;
}