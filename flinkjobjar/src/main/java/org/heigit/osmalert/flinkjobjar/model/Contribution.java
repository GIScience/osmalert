package org.heigit.osmalert.flinkjobjar.model;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.*;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.*;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.*;
import org.heigit.osmalert.flinkjobjar.*;
import org.locationtech.jts.geom.*;

@JsonIgnoreProperties(ignoreUnknown = true, allowGetters = true, allowSetters = false)
public class Contribution {

	@JsonProperty("current")
	private Current current;

	public boolean isWithin(BoundingBox boundingBox) {
		return boundingBox.isPointInBoundingBox(new Coordinate(current.getX(), current.getY()));
	}

	public static Contribution createContribution(String contribution) throws JsonProcessingException {
		assert contribution != null;
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(contribution, Contribution.class);
	}

}