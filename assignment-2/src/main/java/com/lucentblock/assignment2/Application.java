package com.lucentblock.assignment2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	@Value("${app.env}")
	static String env;

	public static void main(String[] args) {
		System.out.println(env);
		SpringApplication.run(Application.class, args);
	}

}
