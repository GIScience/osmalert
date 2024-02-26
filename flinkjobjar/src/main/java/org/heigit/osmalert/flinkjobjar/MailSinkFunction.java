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
	private final String jobName;
	private final int time;

	private final String pattern;
	private static StatisticalAnalyzer statisticalAnalyzer;
	private boolean firstEmail;

	public MailSinkFunction(
		String host, int port, String username, String password, String emailAddress, String boundingBox, int time, String pattern,
		String jobName
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
		this.jobName = jobName;
	}

	@Override
	public void invoke(Integer value, Context context) {
		if (statisticalAnalyzer == null) {
			try {
				statisticalAnalyzer = StatisticalAnalyzer.setInstance(boundingBox, time * 60, pattern);
			} catch (IOException | InterruptedException e) {
				statisticalAnalyzer = StatisticalAnalyzer.getInstance();
			}
		}
		System.out.println("##### MailSink input: " + value);

		System.out.println("##### memory:  reserved heap MB : " + getRuntime().totalMemory() / 1_000_000);
		System.out.println("##### memory: maximum memory MB : " + getRuntime().maxMemory() / 1_000_000);

		long currentTimeMillis = System.currentTimeMillis();
		long startTimeMillis = currentTimeMillis - (this.time * 60 * 1000L);

		String unusualChanges = "There were " + value + " changes, which is an unusual high amount of changes as they deviate by more than " +
									"1 standard deviation from the mean of value " + statisticalAnalyzer.getRoundedMeanChanges() +
									". The Standard Deviation value is " + statisticalAnalyzer.getRoundedStandardDeviation();


		String initial = getInitialMessage();

		String timeRange = "Time Range: " + new Date(startTimeMillis) + " - " + new Date(currentTimeMillis) + "\n";
		String boundingBox = "Bounding Box: " + this.boundingBox + "\n";
		String filter;

		if (pattern == null || pattern.isEmpty()) {
			filter = "type:node or type:way";
		} else {
			filter = pattern;
		}
		String emailContent = "Dear user,\n\nIn the last " + this.time + " minutes, there have been "
								  + value + " new OpenStreetMap updates.\n\n" + boundingBox + timeRange + "Tag Filter: \"" + filter + "\"\n\n" + getBoundingBoxLink() + "\n\n"
								  + (statisticalAnalyzer.getZScore(value) > 1.0 ? unusualChanges : "")
								  + initial
								  + "\n\nThank you,\nOSM Alert System";

		statisticalAnalyzer.calculateStandardDeviation(value);

		String jobName = this.jobName.startsWith("AlertJob_") ? this.jobName.split("AlertJob_")[1] : this.jobName;
		this.sendMail(emailContent, this.emailAddress, jobName);
	}

	private String getInitialMessage() {
		String initial = "";
		if (firstEmail) {
			firstEmail = false;
			if (statisticalAnalyzer.getMean() == 0)
				initial += ". A Problem occurred retrieving the historical Data.";
			else
				initial += ". The initial average is calculated with data from " + statisticalAnalyzer.getHistoricDataStart() + " to " + statisticalAnalyzer.getHistoricDataEnd() + " with a value of " + statisticalAnalyzer.getStandardDeviation() + ".";
		}
		return initial;
	}

	private String getBoundingBoxLink() {
		String bbox = "https://dashboard.ohsome.org/#time=" + statisticalAnalyzer.getHistoricDataStart() + "T00%3A00%3A00Z%2F" + statisticalAnalyzer.getHistoricDataEnd() + "T23%3A00Z%2FP1W&filter=" + pattern + "&bboxes=" + boundingBox;
		return bbox.replace(",", "%2c");
	}

	private MailSender getMailSender() {

		System.out.println("host = " + host);
		System.out.println("port = " + port);

		return new MailSender(host, port, username, password);
	}

	private void sendMail(String payload, String emailAddress, String jobName) {
		MailSender mailSender = getMailSender();
		mailSender.sendMail(emailAddress, payload, jobName);
		System.out.println("=== MAIL SENT! ===");
	}
}