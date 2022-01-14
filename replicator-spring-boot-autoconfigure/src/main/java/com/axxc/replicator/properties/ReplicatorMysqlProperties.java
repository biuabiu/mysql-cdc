package com.axxc.replicator.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(ReplicatorMysqlProperties.PREFIX)
public class ReplicatorMysqlProperties {
	
	public static final String PREFIX = "replicator.mysql";
	
	private String hostname = "127.0.0.1";
	private int port = 3306;
	private String username;
	private String password;
	private int serverId = 3306;
	
}
