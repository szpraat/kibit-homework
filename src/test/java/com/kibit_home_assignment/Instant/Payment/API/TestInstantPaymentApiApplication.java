package com.kibit_home_assignment.Instant.Payment.API;

import org.springframework.boot.SpringApplication;

public class TestInstantPaymentApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(InstantPaymentApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
