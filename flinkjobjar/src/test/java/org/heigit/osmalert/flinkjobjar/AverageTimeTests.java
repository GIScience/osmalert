package org.heigit.osmalert.flinkjobjar;

import java.time.*;
import java.util.*;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class AverageTimeTests {
	private AverageTime average;

	@BeforeEach
	void initAverageTime() {
		AverageTime.destroyInstance();
		average = AverageTime.setInstance(0, 0, 0);
	}

	@Test
	public void calculateAverageTest() {
		// 10 => 10
		// 10 * 1 = 10
		average.calculateAverage(10);
		assertEquals(10, average.getAverageChanges());
		// 10 8 => 9
		// 10 * 0.5 + 8 * 0.5 = 9
		average.calculateAverage(8);
		assertEquals(9, average.getAverageChanges());
		// 9 15 => 11
		// 9 * 0.66 + 15 * 0.33 = 6 + 5 = 11
		average.calculateAverage(15);
		assertEquals(11, average.getAverageChanges());
		// 11 1 => 8.5
		// 11 * 0.75 + 1 * 0.25 = 8,25 + 0,25 = 8.5
		average.calculateAverage(1);
		assertEquals(8.5, average.getAverageChanges());

	}

	@Test
	public void getRoundedAverageTest() {
		average.calculateAverage(15);
		assertEquals(15, average.getRoundedAverageChanges());
		average.calculateAverage(2);
		assertEquals(8.5, average.getRoundedAverageChanges());
		average.calculateAverage(5);
		assertEquals(7.3, average.getRoundedAverageChanges());
		average.calculateAverage(9);
		assertEquals(7.8, average.getRoundedAverageChanges());
	}

	@Test
	public void calculateAverageNullTest() {
		AverageTime.destroyInstance();
		assertFalse(average.calculateAverage(1));
		assertEquals(-1, average.getAverageChanges());
	}

	@Test
	void calculateDateInPastTest() {
		LocalDate currentDate = LocalDate.parse("2024-01-23");
		Assertions.assertEquals(AverageTime.calculateDateInPast(currentDate, 2), "2024-01-09");
		Assertions.assertEquals(AverageTime.calculateDateInPast(currentDate, 24), "2023-08-08");
	}

	@Test
	void calculateStandardDeviationTest1() {
		average.calculateAverage(1);
		average.calculateAverage(2);
		average.calculateAverage(3);
		average.calculateAverage(4);
		average.calculateAverage(5);

		Assertions.assertEquals(average.getStandardDeviation(), Math.sqrt(2.5));

	}

	@Test
	void calculateStandardDeviationTest2() {
		average.calculateAverage(10);
		average.calculateAverage(8);
		average.calculateAverage(5);
		average.calculateAverage(1);

		Assertions.assertEquals(average.getStandardDeviation(), Math.sqrt(15.33333333333333333333333333333333));

	}

}