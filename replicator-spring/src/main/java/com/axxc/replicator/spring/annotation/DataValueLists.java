package com.axxc.replicator.spring.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.*;

@Target({ TYPE, FIELD })
@Retention(RUNTIME)
@Documented
public @interface DataValueLists {
	DataValue[] value();
}