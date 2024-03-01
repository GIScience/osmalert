package org.heigit.osmalert.flinkjobjar.model;

import java.util.*;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.*;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.*;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.*;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.json.*;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.*;

@JsonIgnoreProperties(ignoreUnknown = true, allowGetters = true)
public class Contribution {

	private Contribution() {
		current = new Current();
		id = "";
		changeset = new Changeset();
	}

	@JsonProperty("current")
	private final Current current;

	@JsonProperty("id")
	private final String id;

	@JsonProperty("changeset")
	private final Changeset changeset;

	public boolean isWithin(Geometry boundingBox) {
		if (boundingBox != null) {
			Geometry geometry = getGeometry();
			for (Coordinate coordinate : geometry.getCoordinates())
				if (boundingBox.contains(new GeometryFactory().createPoint(coordinate)))
					return true;
		}
		return false;
	}

	public boolean hasPattern(String pattern) {
		if (pattern == null || pattern.isEmpty()) {
			return id.contains("node") || id.contains("way");
		}

		Map<String, String> tags = this.current.getTags();
		String[] keyAndValue = pattern.split("=", 2);

		return tags.entrySet().stream()
				   .anyMatch(tag -> matchesPattern(tag, keyAndValue));
	}

	private boolean matchesPattern(Map.Entry<String, String> tag, String[] keyAndValue) {
		return keyAndValue[1].equals("*") ?
				   tag.getKey().equals(keyAndValue[0]) :
				   tag.getKey().equals(keyAndValue[0]) && tag.getValue().equals(keyAndValue[1]);
	}

	private Geometry getGeometry() {
		Geometry geometry = new GeometryFactory().createPoint();
		try {
			geometry = new WKTReader().read(this.current.getGeometry());
		} catch (Exception e) {}
		return geometry;
	}

	public static Contribution createContribution(String contribution) {
		assert contribution != null;
		ObjectMapper objectMapper = new JsonMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Contribution returnContribution = null;
		try {
			returnContribution = objectMapper.readValue(contribution, Contribution.class);
		} catch (JsonProcessingException ignored) {}
		return returnContribution;
	}

	public int getUserId() {
		return changeset.getUserId();
	}
}