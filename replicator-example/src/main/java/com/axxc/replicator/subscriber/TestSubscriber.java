package com.axxc.replicator.subscriber;

import static com.axxc.replicator.constans.TopicConstans.TEST;

import org.springframework.stereotype.Component;

import com.axxc.replicator.mysql.data.BinlogBatchData;
import com.axxc.replicator.spring.annotation.DataValue;
import com.axxc.replicator.utils.JSONUtils;
import com.axxc.replicator.work.AbstractEventDataSyncWorker;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TestSubscriber extends AbstractEventDataSyncWorker {
	
	@DataValue(TEST)
	private String database;
	
	@Override
	public String topic() {
		return database;
	}
	
	@Override
	public void consume(BinlogBatchData data) {
		log.info("TestSubscriber consume data : {}", JSONUtils.toJSonAsString(data));
		super.consume(data);
	}
	
}
