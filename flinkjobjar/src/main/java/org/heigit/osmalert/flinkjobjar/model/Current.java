package org.heigit.osmalert.flinkjobjar.model;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.*;

@org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
public class Current {
	@JsonProperty("geometry")
	private String geometry;

	public String getGeometry() {
		return geometry;
	}

	public Double getX() {
		return 0.0;
	}

	public Double getY() {
		return 0.0;
	}
}