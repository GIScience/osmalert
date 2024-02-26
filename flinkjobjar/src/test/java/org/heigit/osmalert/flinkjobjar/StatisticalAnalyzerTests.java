package org.heigit.osmalert.flinkjobjar;

import java.io.*;
import java.time.*;
import java.util.*;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class StatisticalAnalyzerTests {
	private StatisticalAnalyzer statisticalAnalyzer;

	@BeforeEach
	void initStandardDeviation() {
		StatisticalAnalyzer.destroyInstance();
		statisticalAnalyzer = StatisticalAnalyzer.setInstance(0, 0, 0);
	}

	@Test
	public void calculateMeanTest() {
		// 10 => 10
		// 10 * 1 = 10
		statisticalAnalyzer.calculateStandardDeviation(10);
		assertEquals(10, statisticalAnalyzer.getMean());
		// 10 8 => 9
		// 10 * 0.5 + 8 * 0.5 = 9
		statisticalAnalyzer.calculateStandardDeviation(8);
		assertEquals(9, statisticalAnalyzer.getMean());
		// 9 15 => 11
		// 9 * 0.66 + 15 * 0.33 = 6 + 5 = 11
		statisticalAnalyzer.calculateStandardDeviation(15);
		assertEquals(11, statisticalAnalyzer.getMean());
		// 11 1 => 8.5
		// 11 * 0.75 + 1 * 0.25 = 8,25 + 0,25 = 8.5
		statisticalAnalyzer.calculateStandardDeviation(1);
		assertEquals(8.5, statisticalAnalyzer.getMean());

	}

	@Test
	public void getRoundedMeanTest() {
		statisticalAnalyzer.calculateStandardDeviation(15);
		assertEquals(15, statisticalAnalyzer.getRoundedMeanChanges());
		statisticalAnalyzer.calculateStandardDeviation(2);
		assertEquals(8.5, statisticalAnalyzer.getRoundedMeanChanges());
		statisticalAnalyzer.calculateStandardDeviation(5);
		assertEquals(7.3, statisticalAnalyzer.getRoundedMeanChanges());
		statisticalAnalyzer.calculateStandardDeviation(9);
		assertEquals(7.8, statisticalAnalyzer.getRoundedMeanChanges());
	}

	@Test
	public void calculateAverageNullTest() {
		StatisticalAnalyzer.destroyInstance();
		statisticalAnalyzer.calculateStandardDeviation(1);
		assertEquals(0, statisticalAnalyzer.getStandardDeviation());
		assertEquals(-1, statisticalAnalyzer.getMean());
	}

	@Test
	void calculateDateInPastTest() {
		LocalDate currentDate = LocalDate.parse("2024-01-23");
		Assertions.assertEquals(StatisticalAnalyzer.calculateDateInPast(currentDate, 2), "2024-01-09");
		Assertions.assertEquals(StatisticalAnalyzer.calculateDateInPast(currentDate, 24), "2023-08-08");
	}

	@Test
	void calculateStandardDeviationTest() {

		List<Integer> dataPoints = Arrays.asList(1, 2, 3, 4, 5);
		for (Integer dataPoint : dataPoints)
			statisticalAnalyzer.calculateStandardDeviation(dataPoint);
		Assertions.assertEquals(statisticalAnalyzer.getStandardDeviation(), Math.sqrt(2.5));
		Assertions.assertEquals(statisticalAnalyzer.getZScore(4), 0.6324555320336759);
		Assertions.assertEquals(statisticalAnalyzer.getZScore(7), 2.5298221281347035);
		initStandardDeviation();

		dataPoints = Arrays.asList(10, 8, 5, 1);
		for (Integer dataPoint : dataPoints)
			statisticalAnalyzer.calculateStandardDeviation(dataPoint);
		Assertions.assertEquals(statisticalAnalyzer.getStandardDeviation(), Math.sqrt(15.33333333333333333333333333333333));
		Assertions.assertEquals(statisticalAnalyzer.getZScore(13), 1.787638714593372);
	}

	@Test
	public void getInstance_ReturnsNonNullInstance() {
		StatisticalAnalyzer instance = StatisticalAnalyzer.getInstance();
		assertNotNull(instance);
	}

	@Test
	public void getInstance_ReturnsSameInstanceOnMultipleCalls() {
		StatisticalAnalyzer instance1 = StatisticalAnalyzer.getInstance();
		StatisticalAnalyzer instance2 = StatisticalAnalyzer.getInstance();
		assertSame(instance1, instance2);
	}

	@Test
	public void setInstance_InitializesCorrectlyWithValidParameters() throws IOException, InterruptedException {
		StatisticalAnalyzer.setInstance("boundingBox", 60, "pattern");
		StatisticalAnalyzer instance = StatisticalAnalyzer.getInstance();
		assertNotNull(instance);
	}

	@Test
	public void setInstance_ThrowsIOExceptionWhenBoundingBoxIsNull() {
		assertThrows(IOException.class, () -> StatisticalAnalyzer.setInstance(null, 60, "pattern"));
	}

	@Test
	public void setInstance_ThrowsIOExceptionWhenTimeWindowSecondsIsZero() {
		assertThrows(IOException.class, () -> StatisticalAnalyzer.setInstance("boundingBox", 0, "pattern"));
	}

	@Test
	public void ContributorTest() {
		StatisticalAnalyzer.addContributor(8732);
		StatisticalAnalyzer.addContributor(1234);
		StatisticalAnalyzer.addContributor(3643);
		StatisticalAnalyzer.addContributor(9846);

		assertEquals(4, StatisticalAnalyzer.getContributorAmount());
		StatisticalAnalyzer.resetContributorAmount();
		assertEquals(0, StatisticalAnalyzer.getContributorAmount());
	}

}