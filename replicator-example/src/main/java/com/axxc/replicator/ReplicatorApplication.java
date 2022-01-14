package com.axxc.replicator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.axxc.replicator.work.annotation.EnableDataSync;

@EnableDataSync
@SpringBootApplication
public class ReplicatorApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(ReplicatorApplication.class, args);
	}
}
