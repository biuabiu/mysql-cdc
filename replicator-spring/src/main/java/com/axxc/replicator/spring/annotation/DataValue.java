package com.axxc.replicator.spring.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AliasFor;

@Documented
@Retention(RUNTIME)
@Target({ TYPE, FIELD })
@Value("")
@Repeatable(DataValueLists.class)
public @interface DataValue {
	
	@AliasFor(annotation = Value.class, value = "value")
	String value();
	
	boolean required() default true;
	
}
