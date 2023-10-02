package org.heigit.osmalert.flinkjobjar;

import jakarta.mail.*;
import org.simplejavamail.api.email.*;
import org.simplejavamail.api.mailer.*;
import org.simplejavamail.email.*;
import org.simplejavamail.mailer.*;


public class MailSender {

	final private Mailer mailer;

	private final String fromAddress = "admin@osmalert-web-0773365646a7.herokuapp.com";



	public MailSender(String host, int port, String username, String password) {

		this.mailer = MailerBuilder
					 .withSMTPServer(host, port, username, password)
					 .buildMailer();
	}


    void sendMail(String recipient, String payload) {

		Recipient mailRecipient = new Recipient("user", recipient, Message.RecipientType.TO);


		Email email = EmailBuilder
						  .startingBlank()
						  .from("osmalert", fromAddress)
						  .to(mailRecipient)
						  .withSubject("Osmalert Notification")
						  .withPlainText(payload)
						  .buildEmail();


		this.mailer.sendMail(email);
	}


}
