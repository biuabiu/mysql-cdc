package com.axxc.replicator.handler;

import lombok.Data;

@Data
public class TestResult {
	
	private Long id;
	
	private String _id;
	
	private long timestamp = System.currentTimeMillis();
}
