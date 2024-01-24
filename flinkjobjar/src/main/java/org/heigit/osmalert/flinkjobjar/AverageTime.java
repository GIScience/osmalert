package org.heigit.osmalert.flinkjobjar;

import java.io.*;
import java.time.*;
import java.time.format.*;

import static org.heigit.osmalert.flinkjobjar.OSMContributionsHistoricalData.*;

public class AverageTime {
	private double averageChanges;
	private double averageWeight;
	private static AverageTime self;
	private static final double derivative = 1.05;

	private AverageTime(double defaultChanges, int numberAverageChanges) {
		this.averageChanges = Math.max(defaultChanges, 0);
		this.averageWeight = Math.max(numberAverageChanges, 0);
	}

	public static AverageTime getInstance() {
		if (self == null) {
			self = new AverageTime(0, 0);
		}
		return self;
	}

	private static final int weekStart = 24;
	private static final int weekEnd = 2;
	// week * days (7) * hours (24) * minutes (60) * seconds (60)
	private static final int numberChanges = (weekStart - weekEnd) * 7 * 24 * 60 * 60;

	public static AverageTime setInstance(String boundingBox, int timeWindowSeconds) throws IOException, InterruptedException {
		self = setInstance(
			getContributionsCountHistoricalAverage(
				boundingBox,
				calculateDateInPast(LocalDate.now(), weekStart),
				calculateDateInPast(LocalDate.now(), weekEnd)
			),
			0
		);
		return self;
	}

	public static AverageTime setInstance(double averageChanges, int numberOfChanges) {
		self = new AverageTime(averageChanges, numberOfChanges);
		return self;
	}

	public static void destroyInstance() {
		self = null;
	}

	public boolean calculateAverage(int number) {
		boolean calcSucceeded = false;
		if (self != null) {
			averageWeight += 1;
			averageChanges = ((averageChanges * (averageWeight - 1) / (averageWeight)) + (number / averageWeight));
			calcSucceeded = true;
		}
		return calcSucceeded;
	}

	public double getAverageChanges() {
		double returnAverageChanges = -1;
		if (self != null)
			returnAverageChanges = this.averageChanges;
		return returnAverageChanges;
	}

	public static double getDerivative() {
		return derivative;
	}

	public static String calculateDateInPast(LocalDate currentDate, int weeksToSubtract) {
		LocalDate minusWeeks = currentDate.minusWeeks(weeksToSubtract);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		return minusWeeks.format(formatter);
	}

}