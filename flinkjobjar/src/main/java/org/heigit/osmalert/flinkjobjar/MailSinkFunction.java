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
	private final String boundingBox;
	private final int time;

	public MailSinkFunction(String host, int port, String username, String password, String emailAddress, String boundingBox, int time) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.emailAddress = emailAddress;
		this.boundingBox = boundingBox;
		this.time = time;
	}

	@Override
	public void invoke(Integer value, Context context) {

		AverageTime averageTime = AverageTime.getInstance();
		System.out.println("##### MailSink input: " + value);

		System.out.println("##### memory:  reserved heap MB : " + getRuntime().totalMemory() / 1_000_000);
		System.out.println("##### memory: maximum memory MB : " + getRuntime().maxMemory() / 1_000_000);

		long currentTimeMillis = System.currentTimeMillis();
		long startTimeMillis = currentTimeMillis - (this.time * 60 * 1000L);

		String unusualChanges = "There was an unusual high amount of changes " + value + " higher than the average of " + averageTime.getAverageChanges() + "\n";

		String linkAdaptedForBBoxFinder = "http://bboxfinder.com/" + AdaptBoundingBoxForBBoxfinder(this.boundingBox);

		String timeRange = "Time Range: " + new Date(startTimeMillis) + " - " + new Date(currentTimeMillis) + "\n";
		String boundingBox = "Bounding Box: " + this.boundingBox + "\n";
		String emailContent = "Dear user,\n\nIn the last " + this.time + " minutes, there have been "
								  + value + " new OpenStreetMap updates.\n" + boundingBox + timeRange + "\n" + linkAdaptedForBBoxFinder + "\n"
								  // adding 5 % threshold above
								  + (value > averageTime.getAverageChanges() * AverageTime.getDerivative() ? unusualChanges : "")
								  + "\n\nThank you,\nOSM Alert System";

		averageTime.calculateAverage(value);

		this.sendMail(emailContent, this.emailAddress);
	}

	public static String AdaptBoundingBoxForBBoxfinder(String boundingBox) {
		String[] parts = boundingBox.split(",");

		String temp = parts[0];
		parts[0] = parts[1];
		parts[1] = temp;
		temp = parts[2];
		parts[2] = parts[3];
		parts[3] = temp;
		return String.join(",", parts);
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