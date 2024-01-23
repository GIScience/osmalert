package org.heigit.osmalert.flinkjobjar;

import java.io.*;
import java.time.*;
import java.time.format.*;

import org.json.*;

import static org.heigit.osmalert.flinkjobjar.OSMContributionsHistoricalData.*;

public class AverageTime {
	private double averageChanges;
	private double averageWeight;
	private static AverageTime self;
	private static final double derivate = 1.05;

	private AverageTime(double defaultChanges, int numberAverageChanges) {
		this.averageChanges = Math.max(defaultChanges, 0);
		this.averageWeight = Math.max(numberAverageChanges, 0);
	}

	public static AverageTime getInstance(String boundingBox) throws IOException, InterruptedException, JSONException {
		if (self == null) {
			self = new AverageTime(
				getContributionsCountHistoricalAverage(
					boundingBox,
					calculateDateInPast(LocalDate.now(), 24),
					calculateDateInPast(LocalDate.now(), 2)
				),
				0
			);
		}
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

	public static double getDerivate() {
		return derivate;
	}

	public static String calculateDateInPast(LocalDate currentDate, int weeksToSubtract) {
		LocalDate minusWeeks = currentDate.minusWeeks(weeksToSubtract);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		return minusWeeks.format(formatter);
	}

}