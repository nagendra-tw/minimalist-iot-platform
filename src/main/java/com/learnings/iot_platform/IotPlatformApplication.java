package com.learnings.iot_platform;

import io.mongock.runner.springboot.EnableMongock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableMongock
public class IotPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(IotPlatformApplication.class, args);
	}

}
