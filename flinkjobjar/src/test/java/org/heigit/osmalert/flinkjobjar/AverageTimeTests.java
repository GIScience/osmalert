package org.heigit.osmalert.flinkjobjar;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class AverageTimeTests {
	private AverageTime average;

	@BeforeEach
	void initAverageTime() {
		AverageTime.destroyInstance();
		average = AverageTime.setInstance(0, 0);
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
	public void calculateAverageNullTest() {
		AverageTime.destroyInstance();
		assertFalse(average.calculateAverage(1));
		assertEquals(-1, average.getAverageChanges());
	}
}
