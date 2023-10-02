package org.heigit.osmalert.flinkjobjar;

import jakarta.mail.*;
import org.simplejavamail.api.email.*;
import org.simplejavamail.api.mailer.*;
import org.simplejavamail.email.*;
import org.simplejavamail.mailer.*;


public class MailSender {

	final private Mailer mailer;


	public MailSender(String host, int port, String username, String password) {

		this.mailer = MailerBuilder
					 .withSMTPServer(host, port, username, password)
					 .buildMailer();
	}


	public MailSender() {
		this("localhost", 25, "username", "password");
	}


    void sendMail(String recipient, String payload) {

		Recipient mailRecipient = new Recipient("user", recipient, Message.RecipientType.TO);


		Email email = EmailBuilder
						  .startingBlank()
						  .from("osmalert", "7ead94725baf40d45113d83f7c957a5e@smtp.us-west-1.mailertogo.net")
						  .to(mailRecipient)
						  .withSubject("Osmalert Notification")
						  .withPlainText(payload)
						  .buildEmail();


		this.mailer.sendMail(email);
	}


}
