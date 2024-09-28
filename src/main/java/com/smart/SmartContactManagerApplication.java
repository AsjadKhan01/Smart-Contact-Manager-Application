package com.smart;

import java.security.SecureRandom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SmartContactManagerApplication {
 
	public static void main(String[] args) {
		SpringApplication.run(SmartContactManagerApplication.class, args);
		System.out.println("SmartContactManagerApplication is running...");
		
		  SecureRandom random = new SecureRandom();
	        StringBuilder otp = new StringBuilder(6);
	        for (int i = 0; i < 6; i++) {
	            int digit = random.nextInt(10); // Generate a digit between 0 and 9
	            otp.append(digit);
	        }
	        System.out.println("Generated OTP: " + otp.toString());
	}
}
