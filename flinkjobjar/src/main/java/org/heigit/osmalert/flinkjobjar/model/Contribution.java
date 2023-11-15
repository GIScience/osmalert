package org.heigit.osmalert.flinkjobjar.model;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.*;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.*;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.*;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.json.*;

public class Contribution {
	@JsonProperty("current")
	public String current;

	public static Contribution createContribution(String contribution) throws JsonProcessingException {
		return new ObjectMapper().readValue(contribution, Contribution.class);
	}
}