package com.axxc.replicator.spring;

import java.lang.reflect.Field;
import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.*;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ReflectionUtils;

import com.axxc.replicator.spring.annotation.*;

import lombok.Data;

/**
 * 
 * @author: CharlesXiong
 * @date: 2021年11月25日下午2:50:16
 * @notes: 移植 <b>@Value</b>标记的field的值到标记在type中同名的变量key中
 */
public class DataValueAnnotationBeanPostProcessor
		implements EnvironmentAware, ImportBeanDefinitionRegistrar, InstantiationAwareBeanPostProcessor {
	static Set<String> PACKAGES_TO_SCAN = new LinkedHashSet<>();
	
	LinkedHashMap<Object, List<PropertyValueElement>> beanElements = new LinkedHashMap<>();
	
	Map<String, Object> cacheMaps = new HashMap<>();
	
	Environment environment;
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (Objects.isNull(bean) || this.skip(bean.getClass().getName())) {
			return bean;
		}
		DataValue annotation = bean.getClass().getAnnotation(DataValue.class);
		DataValueLists lists = bean.getClass().getAnnotation(DataValueLists.class);
		if (Objects.isNull(annotation) && Objects.isNull(lists)) {
			return bean;
		}
		if (Objects.nonNull(annotation)) {
			this.doValue(annotation);
		}
		if (Objects.nonNull(lists)) {
			DataValue[] values = lists.value();
			for (DataValue dataValue : values) {
				this.doValue(dataValue);
			}
		}
		
		return bean;
	}
	
	@Override
	public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
		boolean booleanValue = InstantiationAwareBeanPostProcessor.super.postProcessAfterInstantiation(bean, beanName);
		if (Objects.isNull(bean) || this.skip(bean.getClass().getName())) {
			return booleanValue;
		}
		this.buildElements(bean);
		return booleanValue;
	}
	
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if (Objects.isNull(bean) || this.skip(bean.getClass().getName())) {
			return bean;
		}
		
		List<PropertyValueElement> list = beanElements.get(bean);
		if (CollectionUtils.isNotEmpty(list)) {
			for (PropertyValueElement e : list) {
				Field field = e.getField();
				ReflectionUtils.makeAccessible(field);
				Object fieldValue = ReflectionUtils.getField(field, bean);
				AnnotationAttributes ann = e.getAnn();
				String[] values = ann.getStringArray("value");
				boolean required = ann.getBoolean("required");
				for (Object value : values) {
					String key = value.toString().replace("${", "").replace("}", "");
					if (required && Objects.equals(value.toString(), fieldValue.toString())) {
						throw new IllegalStateException("Required key '" + key + "' not found");
					}
					cacheMaps.put(key, fieldValue);
				}
			}
		}
		
		return InstantiationAwareBeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
	}
	
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		// EnableTestValue
		Map<String, Object> map = importingClassMetadata.getAnnotationAttributes(EnableDataValue.class.getName());
		AnnotationAttributes attributes = AnnotationAttributes.fromMap(map);
		String[] basePackages = attributes.getStringArray("value");
		PACKAGES_TO_SCAN.addAll(Arrays.asList(basePackages));
		if (PACKAGES_TO_SCAN.isEmpty()) {
			PACKAGES_TO_SCAN.add(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
		}
		// 当前process注册到容器中
		AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(this.getClass())
				.getBeanDefinition();
		registry.registerBeanDefinition(StringUtils.uncapitalize(this.getClass().getName()), beanDefinition);
	}
	
	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
	
	private void doValue(DataValue annotation) {
		// 运行时给类注解赋值
		String key = annotation.value().replace("${", "").replace("}", "");
		Object property = this.cacheMaps.get(key);
		boolean hasDefault = StringUtils.contains(key, ":");
		if (Objects.isNull(property)) {
			if (annotation.required() && !hasDefault) {
				property = this.environment.getRequiredProperty(key);
			}
			property = this.environment.getProperty(key);
		}
		if (Objects.isNull(property) && hasDefault) {
			// 处理默认值
			String[] split = StringUtils.split(key, ":");
			if (split.length == 2) {
				property = split[1];
			}
		}
		if (Objects.isNull(property)) {
			return;
		} else {
			cacheMaps.put(key, property);
		}
		
		// AnnotationInvocationHandler
		Field findField = ReflectionUtils.findField(annotation.getClass(), "h");
		findField.setAccessible(true);
		Object field = ReflectionUtils.getField(findField, annotation);
		Field memberValues = ReflectionUtils.findField(field.getClass(), "memberValues");
		memberValues.setAccessible(true);
		@SuppressWarnings("unchecked")
		Map<Object, Object> hashMap = (Map<Object, Object>) ReflectionUtils.getField(memberValues, field);
		hashMap.put("value", property);
	}
	
	private void buildElements(Object bean) {
		final LinkedList<PropertyValueElement> currElements = new LinkedList<>();
		Class<?> targetClass = bean.getClass();
		do {
			LinkedList<PropertyValueElement> list = new LinkedList<>();
			ReflectionUtils.doWithLocalFields(targetClass, field -> {
				AnnotationAttributes ann = AnnotatedElementUtils.getMergedAnnotationAttributes(field, DataValue.class);
				if (ann != null) {
					list.add(new PropertyValueElement(field, ann));
				}
			});
			currElements.addAll(0, list);
			targetClass = targetClass.getSuperclass();
		} while (targetClass != null && targetClass != Object.class);
		beanElements.put(bean, currElements);
		
	}
	
	private boolean skip(String qualifiedName) {
		for (String packageStr : PACKAGES_TO_SCAN) {
			if (StringUtils.startsWithIgnoreCase(qualifiedName, packageStr)) {
				return false;
			}
		}
		return true;
	}
	
	@Data
	private class PropertyValueElement {
		public PropertyValueElement(Field field, AnnotationAttributes ann) {
			super();
			this.field = field;
			this.ann = ann;
		}
		
		private AnnotationAttributes ann;
		
		private Field field;
	}
	
}
