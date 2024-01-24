package org.heigit.osmalert.flinkjobjar;

import java.io.*;
import java.net.*;
import java.net.http.*;

import org.json.*;

public class OSMContributionsHistoricalData {

	static void getContributionsCountHistoricalAverage(
		String boundingBox,
		String fromDate,
		String toDate,
		int timeIntervals,
		int timeIntervalInMinutes
	) throws IOException, InterruptedException {

		JSONObject contributionsCountObject = new JSONObject(getContributionsCountInBB(boundingBox, fromDate, toDate, timeIntervalInMinutes));
		calculateHistoricalAverage(contributionsCountObject.getJSONArray("result"), timeIntervals);
	}

	static void calculateHistoricalAverage(JSONArray osmContributionsCountJsonArray, int timeIntervals) {
		AverageTime averageTime = AverageTime.getInstance();
		for (int i = 0; i < osmContributionsCountJsonArray.length(); i++)
			averageTime.calculateAverage(osmContributionsCountJsonArray.getJSONObject(i).getInt("value"));
	}

	public static String getContributionsCountInBB(
		String boundingBox,
		String fromDate,
		String toDate,
		int timeIntervalInMinutes
	) throws IOException, InterruptedException {
		// TODO change filter
		String apiUrl = "https://api.ohsome.org/v1/contributions/count?bboxes=" + boundingBox + "&filter=type:node or type:way&format=json&time=" + fromDate + "%2f" + toDate + "%2FPT" + timeIntervalInMinutes + "M&timeout=300";
		apiUrl = apiUrl.replace(" ", "%20");
		HttpClient client = HttpClient.newHttpClient();

		HttpRequest request = HttpRequest.newBuilder()
										 .uri(URI.create(apiUrl))
										 .build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		return response.body();
	}
}