package com.axxc.replicator.mysql.data;

import java.util.*;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
public class BinlogBatchData {
	
	// 变更后数据
	private List<Map<?, ?>> afterDatas = new ArrayList<>();
	
	// 变更前数据
	private List<Map<?, ?>> beforeDatas = new ArrayList<>();
	
	// 变更字段名
	private Set<String> change = new HashSet<>();
	
	// 数据库名
	private String database;
	
	// 表名
	private String table;
	
	// 构建时间戳
	private Long timestamp = System.currentTimeMillis();
	
	// 写入类型
	private String type;
	
	// 事务id
	private long xid;
	
}