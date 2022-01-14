package com.axxc.test;

import com.axxc.replicator.mysql.MysqlBinlogReplicator;
import com.axxc.replicator.mysql.events.listener.*;

public class TestHaizolMysqlBinlogListener {
	
	public static void main(String[] args) {
		// @formatter:off
		MysqlBinlogReplicator listener = MysqlBinlogReplicator.builder()
				.hostname("db01.haizol.cc")
				.port(3306)
				.serverId(5)
				.username("root")
				.password("9c7e9cb256fd59fddd61")
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