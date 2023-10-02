package org.heigit.osmalert.flinkjobjar;

import jakarta.mail.internet.*;
import name.bychkov.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.api.extension.*;

import static org.junit.jupiter.api.Assertions.*;


@DisabledIfEnvironmentVariable(named = "CI", matches = "true", disabledReason = "Do not run on in GH Actions")
public class MailSenderIntegrationTests {


	MailSender mailSender = new MailSender();


	@RegisterExtension
	static FakeSmtpJUnitExtension fakeMailServer = new FakeSmtpJUnitExtension()
														   .port(25);


	@Test
	void smokeTest() throws Exception {

		this.mailSender.sendMail("user@example.org", "payload");


		assertEquals(1, fakeMailServer.getMessages().size());
		MimeMessage message = fakeMailServer.getMessages().get(0);

		assertNotNull(message);
		assertEquals("osmalert <7ead94725baf40d45113d83f7c957a5e@smtp.us-west-1.mailertogo.net>", message.getFrom()[0].toString());
		assertEquals("Osmalert Notification", message.getSubject());
		assertEquals("payload\r\n", message.getContent().toString());
	}


}
