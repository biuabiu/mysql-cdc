package com.axxc.replicator.spring.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.*;

import org.springframework.context.annotation.Import;

import com.axxc.replicator.spring.DataValueAnnotationBeanPostProcessor;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Import(DataValueAnnotationBeanPostProcessor.class)
public @interface EnableDataValue {
	
	String[] value() default {};
}
