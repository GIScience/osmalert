package org.heigit.osmalert.flinkjobjar;

import java.io.*;
import java.time.*;
import java.time.format.*;


import static org.heigit.osmalert.flinkjobjar.OSMContributionsHistoricalData.*;

public class AverageTime {
	private double averageChanges;
	private double averageWeight;
	private double sumOfQuads;

	private double standardDeviation;

	private static AverageTime self;
	private static final double derivative = 1.05;
	private static final int weekStart = 4;
	private static final int weekEnd = 2;
	private String historicDataStart;
	private String historicDataEnd;
	// week * days (7) * hours (24) * minutes (60) * seconds (60)
	private static final int numberChanges = (weekStart - weekEnd) * 7 * 24 * 60 * 60;

	private AverageTime(double defaultChanges, double numberAverageChanges, double sumOfQuads) {
		this.averageChanges = Math.max(defaultChanges, 0);
		this.averageWeight = Math.max(numberAverageChanges, 0);
		this.sumOfQuads = Math.max(sumOfQuads, 0);
	}

	public static AverageTime getInstance() {
		if (self == null) {
			setInstance(0, 0, 0);
		}
		return self;
	}

	public static AverageTime setInstance(
		String boundingBox,
		int timeWindowSeconds,
		String pattern
	) throws IOException, InterruptedException {
		if (boundingBox == null || timeWindowSeconds == 0)
			throw new IOException();
		self = setInstance(0, 0, 0);
		self.historicDataStart = calculateDateInPast(LocalDate.now(), weekStart);
		self.historicDataEnd = calculateDateInPast(LocalDate.now(), weekEnd);
		getContributionsCountHistoricalAverage(
			boundingBox,
			self.historicDataStart,
			self.historicDataEnd,
			numberChanges / timeWindowSeconds,
			timeWindowSeconds / 60,
			pattern
		);
		return self;
	}

	public static AverageTime setInstance(double averageChanges, double numberOfChanges, double sumOfQuads) {
		self = new AverageTime(averageChanges, numberOfChanges, sumOfQuads);
		return self;
	}

	public static void destroyInstance() {
		self = null;
	}

	public boolean calculateAverage(int number) {
		boolean calcSucceeded = false;
		if (self != null) {
			averageWeight += 1;
			double oldAverage = averageChanges;
			averageChanges = averageChanges + (number - averageChanges) / (averageWeight);
			sumOfQuads = sumOfQuads + (number - averageChanges) * (number - oldAverage);
			standardDeviation = Math.sqrt(sumOfQuads / (averageWeight - 1));
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

	public double getRoundedAverageChanges() {
		return (double) Math.round(averageChanges * 10) / 10;
	}

	public double getStandardDeviation() {
		return standardDeviation;
	}

	public static double getDerivative() {
		return derivative;
	}

	public static String calculateDateInPast(LocalDate currentDate, int weeksToSubtract) {
		LocalDate minusWeeks = currentDate.minusWeeks(weeksToSubtract);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		return minusWeeks.format(formatter);
	}

	public String getHistoricDataStart() {
		return this.historicDataStart;
	}

	public String getHistoricDataEnd() {
		return this.historicDataEnd;
	}

}