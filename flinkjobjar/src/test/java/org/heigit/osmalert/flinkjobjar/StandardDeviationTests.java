package org.heigit.osmalert.flinkjobjar;

import java.time.*;


import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class StandardDeviationTests {
	private StandardDeviation standardDeviation;

	@BeforeEach
	void initAverageTime() {
		StandardDeviation.destroyInstance();
		standardDeviation = StandardDeviation.setInstance(0, 0, 0);
	}

	@Test
	public void calculateAverageTest() {
		// 10 => 10
		// 10 * 1 = 10
		standardDeviation.calculateStandardDeviation(10);
		assertEquals(10, standardDeviation.getMean());
		// 10 8 => 9
		// 10 * 0.5 + 8 * 0.5 = 9
		standardDeviation.calculateStandardDeviation(8);
		assertEquals(9, standardDeviation.getMean());
		// 9 15 => 11
		// 9 * 0.66 + 15 * 0.33 = 6 + 5 = 11
		standardDeviation.calculateStandardDeviation(15);
		assertEquals(11, standardDeviation.getMean());
		// 11 1 => 8.5
		// 11 * 0.75 + 1 * 0.25 = 8,25 + 0,25 = 8.5
		standardDeviation.calculateStandardDeviation(1);
		assertEquals(8.5, standardDeviation.getMean());

	}

	@Test
	public void getRoundedAverageTest() {
		standardDeviation.calculateStandardDeviation(15);
		assertEquals(15, standardDeviation.getRoundedAverageChanges());
		standardDeviation.calculateStandardDeviation(2);
		assertEquals(8.5, standardDeviation.getRoundedAverageChanges());
		standardDeviation.calculateStandardDeviation(5);
		assertEquals(7.3, standardDeviation.getRoundedAverageChanges());
		standardDeviation.calculateStandardDeviation(9);
		assertEquals(7.8, standardDeviation.getRoundedAverageChanges());
	}

	@Test
	public void calculateAverageNullTest() {
		StandardDeviation.destroyInstance();
		standardDeviation.calculateStandardDeviation(1);
		assertEquals(0, standardDeviation.getStandardDeviation());
		assertEquals(-1, standardDeviation.getMean());
	}

	@Test
	void calculateDateInPastTest() {
		LocalDate currentDate = LocalDate.parse("2024-01-23");
		Assertions.assertEquals(StandardDeviation.calculateDateInPast(currentDate, 2), "2024-01-09");
		Assertions.assertEquals(StandardDeviation.calculateDateInPast(currentDate, 24), "2023-08-08");
	}

	@Test
	void calculateStandardDeviationTest1() {
		standardDeviation.calculateStandardDeviation(1);
		standardDeviation.calculateStandardDeviation(2);
		standardDeviation.calculateStandardDeviation(3);
		standardDeviation.calculateStandardDeviation(4);
		standardDeviation.calculateStandardDeviation(5);

		Assertions.assertEquals(standardDeviation.getStandardDeviation(), Math.sqrt(2.5));

	}

	@Test
	void calculateStandardDeviationTest2() {
		standardDeviation.calculateStandardDeviation(10);
		standardDeviation.calculateStandardDeviation(8);
		standardDeviation.calculateStandardDeviation(5);
		standardDeviation.calculateStandardDeviation(1);

		Assertions.assertEquals(standardDeviation.getStandardDeviation(), Math.sqrt(15.33333333333333333333333333333333));

	}

}