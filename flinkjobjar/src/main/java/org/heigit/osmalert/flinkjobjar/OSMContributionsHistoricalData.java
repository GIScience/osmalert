package org.heigit.osmalert.flinkjobjar;

import java.io.*;
import java.net.*;
import java.net.http.*;

import org.json.*;

public class OSMContributionsHistoricalData {

	static double getContributionsCountHistoricalAverage(
		String boundingBox,
		String fromDate,
		String toDate,
		int timeIntervals,
		int timeIntervalInMinutes
	) throws IOException, InterruptedException {

		JSONObject contributionsCountObject = new JSONObject(getContributionsCountInBB(boundingBox, fromDate, toDate, timeIntervalInMinutes));
		return calculateHistoricalAverage(contributionsCountObject.getJSONArray("result"), timeIntervals);
	}

	static double calculateHistoricalAverage(JSONArray osmContributionsCountJsonArray, int timeIntervals) {
		double pastContributionsCountSum = 0;
		for (int i = 0; i < osmContributionsCountJsonArray.length(); i++)
			pastContributionsCountSum += osmContributionsCountJsonArray.getJSONObject(i).getInt("value");
		System.out.println("Contribution=" + pastContributionsCountSum + "\nTimeInterval=" + timeIntervals);
		return pastContributionsCountSum / timeIntervals;
	}

	public static String getContributionsCountInBB(
		String boundingBox,
		String fromDate,
		String toDate,
		int timeIntervalInMinutes
	) throws IOException, InterruptedException {
		// TODO change filter
		String apiUrl = "https://api.ohsome.org/v1/contributions/count?bboxes=" + boundingBox + "&filter=type:node or type:way&format=json&time=" + fromDate + "%2f" + toDate + "%2FPT" + timeIntervalInMinutes + "M&timeout=300";
		HttpClient client = HttpClient.newHttpClient();

		HttpRequest request = HttpRequest.newBuilder()
										 .uri(URI.create(apiUrl))
										 .build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		return response.body();
	}
}