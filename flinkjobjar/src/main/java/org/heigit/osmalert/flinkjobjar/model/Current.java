package org.heigit.osmalert.flinkjobjar.model;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.*;

import java.util.*;

@org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
public class Current {

	@JsonProperty("geometry")
	private String geometry;

	@JsonProperty("tags")
	private Map<String, String> tags;

	public String getGeometry() {
		return geometry;
	}

	public Map<String, String> getTags() { return tags; }

}