package com.smart.repo;

import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailSender {

	public boolean sendEmail(String to, String subject, String message) {

		boolean f = false;
		String from = "asjadkhanamu21@gmail.com";

		// get the System Properties
		Properties props = new Properties();
		System.out.println("PROPERTIES : " + props);

		// setting Important Information to properties object
	    props.put("mail.smtp.host", "smtp.gmail.com");
	    props.put("mail.smtp.port", "587");
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.starttls.enable", "true");

		// STEP 1: to get the session object
		Session session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from, "uujb vmop xgyd dnjc");
				//return new PasswordAuthentication(from, "@projectX8319!");
			}
		});

		session.setDebug(true);
		// STEP 2: compose the msg
		MimeMessage m = new MimeMessage(session);

		try {
			// from email
			m.setFrom(from);
			// adding recipient to message
			m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			// add subject
			m.setSubject(subject);
			// add text to message
			m.setText(message);
			// STEP 3: send the message using Transport class
			Transport.send(m);
			System.out.println("sending email...");
			f = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return f;
	}
}
