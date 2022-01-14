package com.axxc.replicator.launcher;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;

import com.axxc.replicator.mysql.MysqlBinlogReplicator;
import com.axxc.replicator.mysql.events.listener.*;
import com.axxc.replicator.properties.ReplicatorMysqlProperties;

public class ReplicatorLauncher implements SmartInitializingSingleton {
	
	@Autowired
	ReplicatorMysqlProperties mysqlProperties;
	
	@Override
	public void afterSingletonsInstantiated() {
		// @formatter:off
		MysqlBinlogReplicator listener = MysqlBinlogReplicator.builder()
				.hostname(mysqlProperties.getHostname())
				.port(mysqlProperties.getPort())
				.serverId(mysqlProperties.getServerId())
				.username(mysqlProperties.getUsername())
				.password(mysqlProperties.getPassword())
				.listeners(new TableMapEventDataListener())
				.listeners(new WriteRowsEventDataListener())
				.listeners(new DeleteRowsEventDataListener())
				.listeners(new UpdateRowsEventDataListener())
				.listeners(new XidEventDataListener())
				.build();
		// @formatter:on
		new Thread(() -> listener.start(), "ReplicatorLauncher").start();
	}
	
}
