package com.axxc.replicator.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

public class JSONUtils {
	
	private JSONUtils() throws IllegalAccessException {
		throw new IllegalAccessException();
	}
	
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	@SneakyThrows
	public static <T> T readValue(byte[] bytes, Class<T> valueType) {
		return objectMapper.readValue(bytes, valueType);
	}
	
	@SneakyThrows
	public static <T> T readValue(String jsonStr, Class<T> valueType) {
		return objectMapper.readValue(jsonStr, valueType);
	}
	
	@SneakyThrows
	public static byte[] toJSonAsBytes(Object object) {
		return objectMapper.writeValueAsBytes(object);
	}
	
	@SneakyThrows
	public static String toJSonAsString(Object object) {
		return objectMapper.writeValueAsString(object);
	}
	
}
