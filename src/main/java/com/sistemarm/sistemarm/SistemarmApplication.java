package com.sistemarm.sistemarm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class SistemarmApplication implements WebMvcConfigurer{

	public static void main(String[] args) {
		SpringApplication.run(SistemarmApplication.class, args);
	}

}
