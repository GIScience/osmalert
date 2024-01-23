package org.heigit.osmalert.flinkjobjar;

import org.json.*;
import org.junit.jupiter.api.*;

import java.io.*;

import static org.heigit.osmalert.flinkjobjar.OSMContributionsHistoricalData.*;

public class HistoricalAverageTests {

	@Test
	void getContributionsCountTest() throws IOException, InterruptedException, JSONException {

		//TODO: Write tests for all functions in HistoricalAverage.java
		String boundingBox = "6.9,49.8,13.4,53.8";
		getContributionsCountHistoricalAverage(boundingBox);
		// int responseCode = HistoricalAverage.getContributionsCount(boundingBox);
		// Assertions.assertEquals(responseCode, 200);
	}
}
