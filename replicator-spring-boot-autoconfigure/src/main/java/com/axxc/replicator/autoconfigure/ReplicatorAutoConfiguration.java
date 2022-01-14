package com.axxc.replicator.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.*;

import com.axxc.repicator.async.AsyncConfig;
import com.axxc.repicator.listener.DataSyncResultListener;
import com.axxc.replicator.event.dispatch.EventDispatcher;
import com.axxc.replicator.handler.InternalMysqlHandler;
import com.axxc.replicator.launcher.ReplicatorLauncher;
import com.axxc.replicator.properties.ReplicatorMysqlProperties;
import com.axxc.replicator.utils.SpringUtils;

@EnableAsync
@Configuration
@Import(SpringUtils.class)
@EnableConfigurationProperties(ReplicatorMysqlProperties.class)
public class ReplicatorAutoConfiguration {
	
	@Bean
	@ConditionalOnMissingBean
	public EventDispatcher eventDispatcher() {
		return new EventDispatcher();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public InternalMysqlHandler internalMysqlHandler() {
		return new InternalMysqlHandler();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public ReplicatorLauncher replicatorLauncher() {
		return new ReplicatorLauncher();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public AsyncConfigurer asyncConfigurer() {
		return new AsyncConfig();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public DataSyncResultListener dataSyncResultListener() {
		return new DataSyncResultListener();
	}
	
}
