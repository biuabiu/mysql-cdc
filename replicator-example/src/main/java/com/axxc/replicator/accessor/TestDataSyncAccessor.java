package com.axxc.replicator.accessor;

import org.springframework.stereotype.Component;

import com.axxc.replicator.mysql.data.BinlogBatchData;
import com.axxc.replicator.work.sync.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TestDataSyncAccessor implements DataSyncAccessor {
	
	@Override
	public void invokeAfter(DataSyncMethod current, BinlogBatchData data, Exception e) {
		log.info("TestDataSyncAccessor invokeAfter");
		
	}
	
	@Override
	public void invokeBefore(DataSyncMethod current, BinlogBatchData data) {
		log.info("TestDataSyncAccessor invokeBefore");
		
	}
	
	@Override
	public void skip(DataSyncMethod current, DataSyncMethod last, BinlogBatchData data) {
		log.info("TestDataSyncAccessor skip");
	}
	
}
