package org.heigit.osmalert.flinkjobjar;

import java.io.*;
import java.time.*;
import java.time.format.*;

import static org.heigit.osmalert.flinkjobjar.OSMContributionsHistoricalData.*;


public class StatisticalAnalyzer {

	private double mean;
	private double noOfDataPoints;
	private double sumOfSquaredDifferences;

	private double standardDeviation;

	private static StatisticalAnalyzer self;
	private static final int weekStart = 4;
	private static final int weekEnd = 2;
	private String historicDataStart;
	private String historicDataEnd;


	private StatisticalAnalyzer(double defaultChanges, double numberAverageChanges, double sumOfSquaredDifferences) {
		this.mean = Math.max(defaultChanges, 0);
		this.noOfDataPoints = Math.max(numberAverageChanges, 0);
		this.sumOfSquaredDifferences = Math.max(sumOfSquaredDifferences, 0);
	}

	public static StatisticalAnalyzer getInstance() {
		if (self == null) {
			setInstance(0, 0, 0);
		}
		return self;
	}

	public static StatisticalAnalyzer setInstance(
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
			timeWindowSeconds / 60,
			pattern
		);
		return self;
	}

	public static StatisticalAnalyzer setInstance(double averageChanges, double numberOfChanges, double sumOfQuads) {
		self = new StatisticalAnalyzer(averageChanges, numberOfChanges, sumOfQuads);
		return self;
	}

	public static void destroyInstance() {
		self = null;
	}

	public void calculateStandardDeviation(int number) {
		if (self != null) {
			noOfDataPoints += 1;
			double oldMean = mean;
			mean = mean + (number - mean) / (noOfDataPoints);
			sumOfSquaredDifferences = sumOfSquaredDifferences + (number - mean) * (number - oldMean);
			standardDeviation = Math.sqrt(sumOfSquaredDifferences / (noOfDataPoints - 1));
		}
	}

	public double getMean() {
		double returnAverageChanges = -1;
		if (self != null)
			returnAverageChanges = this.mean;
		return returnAverageChanges;
	}

	public double getRoundedMeanChanges() {
		return (double) Math.round(mean * 10) / 10;
	}

	public double getRoundedStandardDeviation() {
		return (double) Math.round(standardDeviation * 10) / 10;
	}

	public double getStandardDeviation() {
		return standardDeviation;
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

	public double getZScore(Integer value) {
		return (value - this.getMean()) / this.getStandardDeviation();
	}


}