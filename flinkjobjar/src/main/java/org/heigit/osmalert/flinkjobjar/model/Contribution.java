package org.heigit.osmalert.flinkjobjar.model;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.*;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.*;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.*;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.json.*;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.*;

import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true, allowGetters = true)
public class Contribution {

	private Contribution() {
		current = new Current();
		id = "";
	}

	@JsonProperty("current")
	private final Current current;

	@JsonProperty("id")
	private final String id;

	public boolean isWithin(Geometry boundingBox) {
		if (boundingBox != null) {
			Geometry geometry = getGeometry();
			for (Coordinate coordinate : geometry.getCoordinates())
				if (boundingBox.contains(new GeometryFactory().createPoint(coordinate)))
					return true;
		}
		return false;
	}

	public boolean filterBoundingBoxAndPattern(Geometry boundingBox, String pattern) {
		return isWithin(boundingBox) && hasPattern(pattern);
	}

	public boolean hasPattern(String pattern) {
		if (pattern == null || pattern.isEmpty()) {
			return id.contains("node") || id.contains("way");
		}
		boolean hasPattern = false;
		Map<String, String> tags = this.current.getTags();
		String[] keyAndValue = pattern.split("=", 2);
		if (keyAndValue[1].equals("*")) {
			for (Map.Entry<String, String> tag: tags.entrySet()) {
                if (tag.getKey().equals(keyAndValue[0])) {
                    hasPattern = true;
                    break;
                }
			}
		} else {
			for (Map.Entry<String, String> tag: tags.entrySet()) {
                if (tag.getKey().equals(keyAndValue[0]) && tag.getValue().equals(keyAndValue[1])) {
                    hasPattern = true;
                    break;
                }
			}
		}
		return hasPattern;
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
}