package org.heigit.osmalert.flinkjobjar;

class AverageTime {
	private static int averageChanges;
	private static double averageWeight;

	public static void initAverageChanges(int defaultAverageChanges, int numberOfAverageChangesHappened) {
		averageChanges = Math.max(defaultAverageChanges, 0);
		averageWeight = Math.max(numberOfAverageChangesHappened, 0);
	}

	public static void calculateAverage(int number) {
		// 10 8 => 9
		// 10 * 0.5 + 8 * 0.5 = 9
		// 9 15 => 12
		// 9 * 0.66 + 15 * 0.33 = 6 + 5 = 11
		// 11 1 => 8.5
		// 11 * 0.75 + 1 * 0.25 = 8,25 + 0,25 = 8.5
		// 10 8 15 1 => 8,5
		averageWeight += 1;
		averageChanges = (int) ((averageChanges / (1 - averageWeight)) + (number / averageWeight));
	}

	public static int getAverageChanges() {
		return averageChanges;
	}
}