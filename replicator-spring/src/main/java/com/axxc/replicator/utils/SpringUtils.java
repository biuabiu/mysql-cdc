package com.axxc.replicator.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.*;
import org.springframework.context.*;
import org.springframework.core.env.Environment;

public class SpringUtils implements ApplicationContextAware {
	
	private static ApplicationContext applicationContext = null;
	
	private static Environment environment = null;
	
	@Override
	public void setApplicationContext(ApplicationContext ApplicationContext) throws BeansException {
		SpringUtils.applicationContext = ApplicationContext;
	}
	
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		return (T) applicationContext.getBean(name);
	}
	
	public static <T> T getBean(Class<T> clazz) {
		return (T) applicationContext.getBean(clazz);
	}
	
	public static <T> T registerBean(Class<T> c) {
		DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext
				.getAutowireCapableBeanFactory();
		BeanDefinition beanDefinition = new GenericBeanDefinition();
		beanDefinition.setBeanClassName(c.getName());
		beanFactory.registerBeanDefinition(c.getName(), beanDefinition);
		return getBean(c.getName());
	}
	
	public static String getProperty(String key) {
		if (environment == null) {
			environment = getBean(Environment.class);
		}
		return environment.getProperty(key);
	}
}