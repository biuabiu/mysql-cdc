package com.axxc.repicator.config;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.client.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;

@Configuration
public class ElasticsearchRestClient {
	private static final int ADDRESS_LENGTH = 2;
	private static final String HTTP_SCHEME = "http";
	
	@Value("localhost:9200")
	String[] esAddress;
	
	@Bean
	public RestClientBuilder restClientBuilder() {
		// @formatter:off
		HttpHost[] hosts = Arrays.stream(esAddress)
				.map(this::makeHttpHost)
				.filter(Objects::nonNull)
				.toArray(HttpHost[]::new);
		// @formatter:on
		return RestClient.builder(hosts);
	}
	
	@Bean(name = "highLevelClient")
	public RestHighLevelClient highLevelClient(@Autowired RestClientBuilder restClientBuilder) {
		return new RestHighLevelClient(restClientBuilder);
	}
	
	private HttpHost makeHttpHost(String s) {
		assert StringUtils.isNotEmpty(s);
		String[] address = s.split(":");
		if (address.length == ADDRESS_LENGTH) {
			String ip = address[0];
			int port = Integer.parseInt(address[1]);
			return new HttpHost(ip, port, HTTP_SCHEME);
		} else {
			return null;
		}
	}
	
}
