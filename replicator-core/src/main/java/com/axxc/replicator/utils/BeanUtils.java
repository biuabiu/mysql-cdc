package com.axxc.replicator.utils;

import lombok.SneakyThrows;

public class BeanUtils {
	
	@SneakyThrows
	public static void copy(Object source, Object target) {
		org.apache.commons.beanutils.BeanUtils.copyProperties(target, source);
	}
	
	@SneakyThrows
	public static <T> T copy(Object source, Class<T> clazz) {
		T target = clazz.newInstance();
		org.apache.commons.beanutils.BeanUtils.copyProperties(target, source);
		return target;
	}
}
