package com.axxc.replicator.work.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.*;

import com.axxc.replicator.spring.annotation.EnableDataValue;

@Documented
@Retention(RUNTIME)
@Target({ TYPE })
@EnableDataValue
public @interface EnableDataSync {
	
}
