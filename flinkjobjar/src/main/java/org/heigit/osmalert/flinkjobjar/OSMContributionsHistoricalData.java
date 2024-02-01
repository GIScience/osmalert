package org.heigit.osmalert.flinkjobjar;

import java.io.*;
import java.net.*;
import java.net.http.*;
import java.time.*;

import org.json.*;

public class OSMContributionsHistoricalData {

	static void getContributionsCountHistoricalAverage(
		String boundingBox,
		String fromDate,
		String toDate,
		int timeIntervalInMinutes,
		String pattern
	) throws IOException, InterruptedException {
		try {
			JSONObject contributionsCountObject = new JSONObject(getContributionsCountInBB(boundingBox, fromDate, toDate, timeIntervalInMinutes, pattern));
			if (contributionsCountObject.has("result")) {
				calculateHistoricalStandardDeviation(contributionsCountObject.getJSONArray("result"));
			}
		} catch (JSONException e) {
			System.out.println("String is not a JSON.");
		}
	}

	static void calculateHistoricalStandardDeviation(JSONArray osmContributionsCountJsonArray) {
		StandardDeviation standardDeviation = StandardDeviation.getInstance();
		for (int i = 0; i < osmContributionsCountJsonArray.length(); i++) {
			standardDeviation.calculateStandardDeviation(osmContributionsCountJsonArray.getJSONObject(i).getInt("value"));
		}
	}

	public static String getContributionsCountInBB(
		String boundingBox,
		String fromDate,
		String toDate,
		int timeIntervalInMinutes,
		String pattern
	) throws IOException, InterruptedException {
		// TODO change filter
		String apiUrl;
		if (pattern == null || pattern.isEmpty()) {
			apiUrl = "https://api.ohsome.org/v1/contributions/count?bboxes=" + boundingBox + "&filter=type%3Anode%20or%20type%3Away&format=json&time=" + fromDate + "%2f" + toDate + "%2FPT" + timeIntervalInMinutes + "M&timeout=300";
		} else {
			apiUrl = "https://api.ohsome.org/v1/contributions/count?bboxes=" + boundingBox + "&filter=" + pattern + "&format=json&time=" + fromDate + "%2f" + toDate + "%2FPT" + timeIntervalInMinutes + "M&timeout=300";
		}

		apiUrl = apiUrl.replace(" ", "%20");
		HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(320)).build();

		HttpRequest request = HttpRequest.newBuilder()
										 .uri(URI.create(apiUrl)).timeout(Duration.ofSeconds(310))
										 .build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		return response.body();
	}
}