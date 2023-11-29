package org.heigit.osmalert.flinkjobjar;

import java.util.*;

import org.apache.flink.streaming.api.functions.sink.*;

import static java.lang.Runtime.*;

public class MailSinkFunction implements SinkFunction<Integer> {

	private final String host;
	private final int port;
	private final String username;
	private final String password;
	private final String emailAddress;
	private String boundingBox = "";

	public MailSinkFunction(String host, int port, String username, String password, String emailAddress) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.emailAddress = emailAddress;
	}

	@Override
	public void invoke(Integer value, Context context) {

		System.out.println("##### MailSink input: " + value);

		System.out.println("##### memory:  reserved heap MB : " + getRuntime().totalMemory() / 1_000_000);
		System.out.println("##### memory: maximum memory MB : " + getRuntime().maxMemory() / 1_000_000);

		long currentTimeMillis = System.currentTimeMillis();
		long startTimeMillis = currentTimeMillis - (60 * 1000);

		String timeRange = "Time Range: " + new Date(startTimeMillis) + " - " + new Date(currentTimeMillis);
		String emailContent = "Dear user,\n\nIn the last 60 seconds, there have been "
								  + value + " new OpenStreetMap updates in this bounding box " + boundingBox + ".\n" + timeRange
								  + "\n\nThank you,\nOSM Alert System";

		this.sendMail(emailContent, this.emailAddress);
	}

	public void setBoundingBox(String boundingBox) {
		this.boundingBox = boundingBox;
	}

	private MailSender getMailSender() {

		System.out.println("host = " + host);
		System.out.println("port = " + port);

		return new MailSender(host, port, username, password);
	}

	private void sendMail(String payload, String emailAddress) {
		MailSender mailSender = getMailSender();
		mailSender.sendMail(emailAddress, payload);
		System.out.println("=== MAIL SENT! ===");
	}

}