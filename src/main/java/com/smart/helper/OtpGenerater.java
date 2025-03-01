package com.smart.helper;

import org.springframework.stereotype.Component;

@Component
public class OtpGenerater {

	public String setOtp() {

		String otp = "";

		for (int i = 0; i < 4; i++) {
			int random = (int) (Math.random() * 10);
			String num = Integer.toString(random);
			otp = otp + num;
		}
		
		return otp;
	}
}
