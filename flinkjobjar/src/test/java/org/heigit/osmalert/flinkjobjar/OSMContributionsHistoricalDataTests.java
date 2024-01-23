package org.heigit.osmalert.flinkjobjar;

import org.json.*;
import org.junit.jupiter.api.*;

import java.io.*;

import static org.heigit.osmalert.flinkjobjar.OSMContributionsHistoricalData.*;

public class OSMContributionsHistoricalDataTests {

	@Test
	void getContributionsCountHistoricalAverageTest() throws IOException, InterruptedException, JSONException {

		Assertions.assertEquals(
			getContributionsCountHistoricalAverage("6.9,49.8,13.4,53.8", "2023-11-01", "2023-11-06"),
			760.6
		);

		Assertions.assertEquals(
			getContributionsCountHistoricalAverage("123.2,13.2,133.2,15.2", "2023-12-01", "2023-12-10"),
			22.666666666666668
		);
	}

	@Test
	void getContributionsCountInBBTest() throws IOException, InterruptedException {
		JSONObject contributionsCountObject = new JSONObject(getContributionsCountInBB("6.9,49.8,13.4,53.8", "2023-11-01", "2023-11-02"));
		Assertions.assertEquals(
			((JSONObject)contributionsCountObject.getJSONArray("result").get(0)).getInt("value"),
			759
		);
	}

}
