package com.lucentblock.assignment2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	@Value("${app.env1}")
	static String env1;

	@Value("${app.env2}")
	static String env2;

	public static void main(String[] args) {
		System.out.println(env1 + ":"+ env2);
		SpringApplication.run(Application.class, args);
	}

}
