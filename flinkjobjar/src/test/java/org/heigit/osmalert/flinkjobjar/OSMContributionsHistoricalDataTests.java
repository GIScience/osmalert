package org.heigit.osmalert.flinkjobjar;

import java.io.*;

import org.json.*;
import org.junit.jupiter.api.*;

import static org.heigit.osmalert.flinkjobjar.OSMContributionsHistoricalData.*;

public class OSMContributionsHistoricalDataTests {

	AverageTime averageTime;

	@BeforeEach
	void init() {
		AverageTime.destroyInstance();
		averageTime = AverageTime.setInstance(0, 0);
	}

	@Test
	void getContributionsCountHistoricalAverageTest() throws IOException, InterruptedException, JSONException {

		getContributionsCountHistoricalAverage("6.9,49.8,13.4,53.8", "2023-11-01", "2023-11-06", 5, 60 * 24);

		Assertions.assertEquals(
			34497.0,
			averageTime.getAverageChanges()
		);
	}

	@Test
	void getContributionsCountInBBTest() throws IOException, InterruptedException {
		JSONObject contributionsCountObject = new JSONObject(getContributionsCountInBB("6.9,49.8,13.4,53.8", "2023-11-01", "2023-11-02", 60 * 24));
		Assertions.assertEquals(
			32317.0,
			((JSONObject) contributionsCountObject.getJSONArray("result").get(0)).getInt("value")
		);
	}

}