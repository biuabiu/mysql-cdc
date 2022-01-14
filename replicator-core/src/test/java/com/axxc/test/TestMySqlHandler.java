package com.axxc.test;

import com.axxc.replicator.mysql.data.BinlogBatchData;
import com.axxc.replicator.mysql.disruptor.MySqlHandler;
import com.axxc.replicator.utils.JSONUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestMySqlHandler implements MySqlHandler {
	
	@Override
	public void onEvent(BinlogBatchData event, long sequence, boolean endOfBatch) throws Exception {
		log.debug("handler data : {}", JSONUtils.toJSonAsString(event));
	}
	
}
