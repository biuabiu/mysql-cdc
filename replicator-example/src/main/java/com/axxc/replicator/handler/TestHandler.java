package com.axxc.replicator.handler;

import org.springframework.stereotype.Component;

import static com.axxc.replicator.constans.TopicConstans.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import com.axxc.replicator.mysql.data.BinlogBatchData;
import com.axxc.replicator.spring.annotation.*;
import com.axxc.replicator.utils.JSONUtils;
import com.axxc.replicator.work.annotation.DataSyncListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@DataValueLists({ @DataValue(value = TEST), @DataValue(value = TEST2) })
public class TestHandler {
	
	@DataSyncListener(database = "test", tables = { "tag" }, skip = true)
	List<TestResult> test(BinlogBatchData data) {
		log.info("TestHandler data {}", JSONUtils.toJSonAsString(data));
		TestResult testResult = new TestResult();
		testResult.setId(ThreadLocalRandom.current().nextLong());
		return Arrays.asList(testResult);
	}
	
	@DataSyncListener(database = "test2", tables = { "test2" }, skip = true)
	List<TestResult> test2(BinlogBatchData data) {
		log.info("TestHandler data {}", JSONUtils.toJSonAsString(data));
		TestResult testResult = new TestResult();
		testResult.setId(ThreadLocalRandom.current().nextLong());
		return Arrays.asList(testResult);
	}
}
