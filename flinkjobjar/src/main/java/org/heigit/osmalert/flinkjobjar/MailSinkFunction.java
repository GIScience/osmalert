package org.heigit.osmalert.flinkjobjar;

import java.io.*;
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
	private static AverageTime averageTime;
	private boolean firstEmail;

	public MailSinkFunction(
		String host, int port, String username, String password, String emailAddress, String boundingBox, int time
	) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.emailAddress = emailAddress;
		this.boundingBox = boundingBox;
		this.time = time;
		this.firstEmail = true;
	}

	@Override
	public void invoke(Integer value, Context context) {
		if (averageTime == null) {
			try {
				averageTime = AverageTime.setInstance(boundingBox, time * 60);
			} catch (IOException | InterruptedException e) {
				averageTime = AverageTime.getInstance();
			}
		}
		System.out.println("##### MailSink input: " + value);

		System.out.println("##### memory:  reserved heap MB : " + getRuntime().totalMemory() / 1_000_000);
		System.out.println("##### memory: maximum memory MB : " + getRuntime().maxMemory() / 1_000_000);

		long currentTimeMillis = System.currentTimeMillis();
		long startTimeMillis = currentTimeMillis - (this.time * 60 * 1000L);

		String unusualChanges = "There were " + value + " changes, which is an unusual high amount of changes compared to the average of "
									+ averageTime.getRoundedAverageChanges(1);


		String inital = getInitialMessage();
		String linkAdaptedForBBoxFinder = "http://bboxfinder.com/#" + AdaptBoundingBoxForBBoxfinder(this.boundingBox);

		String timeRange = "Time Range: " + new Date(startTimeMillis) + " - " + new Date(currentTimeMillis) + "\n";
		String boundingBox = "Bounding Box: " + this.boundingBox + "\n";
		String emailContent = "Dear user,\n\nIn the last " + this.time + " minutes, there have been "
								  + value + " new OpenStreetMap updates.\n" + boundingBox + timeRange + "\n" + linkAdaptedForBBoxFinder + "\n"
								  // adding 5 % threshold above
								  + (value > averageTime.getAverageChanges() * AverageTime.getDerivative() ? unusualChanges : "")
								  + inital
								  + "\n\nThank you,\nOSM Alert System";

		averageTime.calculateAverage(value);

		this.sendMail(emailContent, this.emailAddress);
	}

	private String getInitialMessage() {
		String initial = "";
		if (firstEmail) {
			firstEmail = false;
			if (averageTime.getAverageChanges() == 0)
				initial += "\nA Problem occurred retrieving the historical Data.";
			else
				initial += "\nThe initial average is calculated with data from " + averageTime.getHistoricDataStart() + " to " + averageTime.getHistoricDataEnd() + " with a value of " + averageTime.getRoundedAverageChanges(1) + ".";
		}
		return initial;
	}

	public static String AdaptBoundingBoxForBBoxfinder(String boundingBox) {
		String[] parts = boundingBox.split(",");
		return String.join(",", parts[1], parts[0], parts[3], parts[2]);
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