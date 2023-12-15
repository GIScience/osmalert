package org.heigit.osmalert.flinkjobjar.model;

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
	}

	@JsonProperty("current")
	private final Current current;

	public boolean isWithin(Geometry boundingBox) {
		if (boundingBox != null) {
			Geometry geometry = getGeometry();
			for (Coordinate coordinate : geometry.getCoordinates())
				if (boundingBox.contains(new GeometryFactory().createPoint(coordinate)))
					return true;
		}
		return false;
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