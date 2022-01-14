package com.axxc.replicator.mysql;

import java.util.List;
import java.util.concurrent.atomic.*;

import org.apache.commons.collections.CollectionUtils;

import com.axxc.replicator.lifecycle.Lifecycle;
import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.BinaryLogClient.EventListener;

import lombok.*;

@Builder
public class MysqlBinlogReplicator implements Lifecycle {
	
	private final String hostname;
	private final int port;
	private final String username;
	private final String password;
	private final int serverId;
	@Singular("listeners")
	private final List<EventListener> listeners;
	
	//
	private final AtomicBoolean runner = new AtomicBoolean(false);
	private final AtomicReference<BinaryLogClient> clientReference = new AtomicReference<>(null);
	
	@Override
	public boolean isRunning() {
		return runner.get();
	}
	
	@Override
	@SneakyThrows
	public void start() {
		if (!runner.compareAndSet(false, true)) return;
		BinaryLogClient client = new BinaryLogClient(hostname, port, username, password);
		client.setServerId(this.serverId);
		if (CollectionUtils.isNotEmpty(this.listeners)) {
			this.listeners.forEach(client::registerEventListener);
		}
		client.connect();
		
		clientReference.set(client);
	}
	
	@Override
	@SneakyThrows
	public void stop() {
		if (runner.compareAndSet(true, false) && clientReference.get().isConnected()) {
			clientReference.get().disconnect();
		}
	}
	
}