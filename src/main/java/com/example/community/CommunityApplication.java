package com.example.community;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CommunityApplication {
	private static final Logger log = LoggerFactory.getLogger(CommunityApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(CommunityApplication.class, args);
		log.info("TuneLog backend CI/CD deployment v1");
	}

}
