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

	private final String pattern;
	private static StandardDeviation standardDeviation;
	private boolean firstEmail;

	public MailSinkFunction(
		String host, int port, String username, String password, String emailAddress, String boundingBox, int time, String pattern
	) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.emailAddress = emailAddress;
		this.boundingBox = boundingBox;
		this.time = time;
		this.firstEmail = true;
		this.pattern = pattern;
	}

	@Override
	public void invoke(Integer value, Context context) {
		if (standardDeviation == null) {
			try {
				standardDeviation = StandardDeviation.setInstance(boundingBox, time * 60, pattern);
			} catch (IOException | InterruptedException e) {
				standardDeviation = StandardDeviation.getInstance();
			}
		}
		System.out.println("##### MailSink input: " + value);

		System.out.println("##### memory:  reserved heap MB : " + getRuntime().totalMemory() / 1_000_000);
		System.out.println("##### memory: maximum memory MB : " + getRuntime().maxMemory() / 1_000_000);

		long currentTimeMillis = System.currentTimeMillis();
		long startTimeMillis = currentTimeMillis - (this.time * 60 * 1000L);

		String unusualChanges = "There were " + value + " changes, which is an unusual high amount of changes compared to the average of "
									+ standardDeviation.getRoundedMeanChanges();


		String inital = getInitialMessage();

		String timeRange = "Time Range: " + new Date(startTimeMillis) + " - " + new Date(currentTimeMillis) + "\n";
		String boundingBox = "Bounding Box: " + this.boundingBox + "\n";
		String emailContent = "Dear user,\n\nIn the last " + this.time + " minutes, there have been "
								  + value + " new OpenStreetMap updates.\n" + boundingBox + timeRange + "\n" + getBoundingBoxLink() + "\n"
								  // adding 5 % threshold above
								  + (value > standardDeviation.getMean() * StandardDeviation.getDerivative() ? unusualChanges : "")
								  + inital
								  + "\n\nThank you,\nOSM Alert System";

		standardDeviation.calculateStandardDeviation(value);

		this.sendMail(emailContent, this.emailAddress);
	}

	private String getInitialMessage() {
		String initial = "";
		if (firstEmail) {
			firstEmail = false;
			if (standardDeviation.getMean() == 0)
				initial += "\nA Problem occurred retrieving the historical Data.";
			else
				initial += "\nThe initial average is calculated with data from " + standardDeviation.getHistoricDataStart() + " to " + standardDeviation.getHistoricDataEnd() + " with a value of " + standardDeviation.getRoundedMeanChanges() + ".";
		}
		return initial;
	}

	private String getBoundingBoxLink() {
		String bbox = "https://dashboard.ohsome.org/#backend=ohsomeApi&groupBy=none&time=" + standardDeviation.getHistoricDataStart() + "T00%3A00%3A00Z%2F" + standardDeviation.getHistoricDataEnd() + "T23%3A00Z%2FP1M&filter=" + pattern + "&measure=count&bboxes=" + boundingBox;
		return bbox.replace(",", "%2c");
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