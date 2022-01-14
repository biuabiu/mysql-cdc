package com.axxc.replicator.work.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.*;

@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface DataSyncListener {
	// 监听库名
	String database();
	
	// 监听的表名
	String[] tables();
	
	// 同批次事务是否跳过
	boolean skip() default true;
}
