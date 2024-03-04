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

		getContributionsCountHistoricalAverage("8.67,49.39,8.71,49.42", "2014-01-01", "2017-01-01", 525600, "natural=*");

		Assertions.assertEquals(
			504,
			statisticalAnalyzer.getMean(),
			2
		);
	}

	@Test
	void getContributionsCountHistoricalAverageWithEmptyStringTest() throws IOException, InterruptedException, JSONException {

		getContributionsCountHistoricalAverage("8.67,49.39,8.71,49.42", "2014-01-01", "2017-01-01", 525600, "");

		Assertions.assertEquals(
			11768.7,
			statisticalAnalyzer.getMean(),
			5
		);
	}

	@Test
	void getContributionsCountInBBTest() throws IOException, InterruptedException {

		JSONObject contributionsCountObject = new JSONObject(getContributionsCountInBB("8.67,49.39,8.71,49.42", "2014-01-01", "2017-01-01", 525600, ""));
		Assertions.assertEquals(
			14970,
			((JSONObject) contributionsCountObject.getJSONArray("result").get(0)).getInt("value")
		);
	}

}