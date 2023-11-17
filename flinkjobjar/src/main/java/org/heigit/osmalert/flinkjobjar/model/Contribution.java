package org.heigit.osmalert.flinkjobjar.model;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.*;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.*;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.*;

@JsonIgnoreProperties(ignoreUnknown = true, allowGetters = true, allowSetters = false)
public class Contribution {

	@JsonProperty("current")
	private Current current;

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Current {

		@JsonProperty("geometry")
		private String geometry;
	}

	public static Contribution createContribution(String contribution) throws JsonProcessingException {
		assert contribution != null;
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(contribution, Contribution.class);
	}
}