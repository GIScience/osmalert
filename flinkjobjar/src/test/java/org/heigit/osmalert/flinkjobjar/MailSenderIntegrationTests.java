package org.heigit.osmalert.flinkjobjar;

import jakarta.mail.internet.*;
import name.bychkov.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;

import static org.junit.jupiter.api.Assertions.*;

public class MailSenderIntegrationTests {

	final MailSender mailSender = new MailSender("localhost", 2025, "username", "password");

	@RegisterExtension
	static final FakeSmtpJUnitExtension fakeMailServer = new FakeSmtpJUnitExtension()
															 .port(2025);

	@Test
	void smokeTest() throws Exception {

		this.mailSender.sendMail("user@example.org", "payload");


		assertEquals(1, fakeMailServer.getMessages().size());
		MimeMessage message = fakeMailServer.getMessages().get(0);

		assertNotNull(message);
		assertEquals("osmalert <osmalert@web.de>", message.getFrom()[0].toString());
		assertEquals("user <user@example.org>", message.getAllRecipients()[0].toString());
		assertEquals("Osmalert Notification", message.getSubject());
		assertEquals("payload\r\n", message.getContent().toString());
	}

}