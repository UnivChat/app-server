package com.app.univchat;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing ///SimpleBatchConfiguration을 스프링 빈으로 등록
public class UnivchatApplication {

	public static void main(String[] args) {
		SpringApplication.run(UnivchatApplication.class, args);
	}

}
