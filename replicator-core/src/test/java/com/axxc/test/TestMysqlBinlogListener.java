package com.axxc.test;

import com.axxc.replicator.mysql.MysqlBinlogReplicator;
import com.axxc.replicator.mysql.events.listener.*;

public class TestMysqlBinlogListener {
	
	public static void main(String[] args) {
		// @formatter:off
		MysqlBinlogReplicator listener = MysqlBinlogReplicator.builder()
				.hostname("127.0.0.1")
				.port(3306)
				.serverId(5)
				.username("mysql80_read")
				.password("Haizol123456")
				.listeners(new TableMapEventDataListener())
				.listeners(new WriteRowsEventDataListener())
				.listeners(new DeleteRowsEventDataListener())
				.listeners(new UpdateRowsEventDataListener())
				.listeners(new XidEventDataListener())
				.build();
		// @formatter:on
		listener.start();
		
	}
	
}