package org.heigit.osmalert.flinkjobjar;

import java.io.*;

import org.json.*;
import org.junit.jupiter.api.*;

import static org.heigit.osmalert.flinkjobjar.OSMContributionsHistoricalData.*;

public class OSMContributionsHistoricalDataTests {

	StatisticalAnalyzer statisticalAnalyzer;

	@BeforeEach
	void init() {
		StatisticalAnalyzer.destroyInstance();
		statisticalAnalyzer = StatisticalAnalyzer.setInstance(0, 0, 0);
	}

	@Test
	void getContributionsCountHistoricalAverageTest() throws IOException, InterruptedException, JSONException {

		getContributionsCountHistoricalAverage("6.9,49.8,13.4,53.8", "2023-11-01", "2023-11-06", 60 * 24, "natural=tree");

		Assertions.assertEquals(
			633.8,
			statisticalAnalyzer.getMean()
		);
	}

	@Test
	void getContributionsCountHistoricalAverageWithNullPatternTest() throws IOException, InterruptedException, JSONException {

		getContributionsCountHistoricalAverage("6.9,49.8,13.4,53.8", "2023-11-01", "2023-11-06", 60 * 24, null);

		Assertions.assertEquals(
			34497.8,
			statisticalAnalyzer.getMean()
		);
	}

	@Test
	void getContributionsCountHistoricalAverageWithEmptyStringTest() throws IOException, InterruptedException, JSONException {

		getContributionsCountHistoricalAverage("6.9,49.8,13.4,53.8", "2023-11-01", "2023-11-06", 60 * 24, "");

		Assertions.assertEquals(
			34497.8,
			statisticalAnalyzer.getMean()
		);
	}

	@Test
	void getContributionsCountInBBTest() throws IOException, InterruptedException {

		JSONObject contributionsCountObject = new JSONObject(getContributionsCountInBB("6.9,49.8,13.4,53.8", "2023-11-01", "2023-11-02", 60 * 24, "natural=tree"));
		Assertions.assertEquals(
			680,
			((JSONObject) contributionsCountObject.getJSONArray("result").get(0)).getInt("value")
		);
	}

}