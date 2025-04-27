package com.application.claimhereweb;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ClaimherewebApplication{

	public static void main(String[] args) {
		System.setProperty("java.awt.headless", "true");
		TimeZone.setDefault(TimeZone.getTimeZone("America/Lima"));
		SpringApplication.run(ClaimherewebApplication.class, args);
	}
}