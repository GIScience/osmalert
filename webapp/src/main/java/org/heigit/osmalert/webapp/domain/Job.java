package org.heigit.osmalert.webapp.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class Job {

	@Id
	@GeneratedValue
	private Long id;

	@Pattern(regexp = "[^ ]*([A-Za-z0-9]+ ?)+[^ ]*", message = "Invalid jobName")
	private String jobName;

	private String flinkId;

	@Email(regexp = ".+[@].+[\\.].+", message = "Invalid Email")
	private String email;

	private String boundingBox;

	protected Job() {}

	public Job(String jobName) {
		this.jobName = jobName;
	}

	public Job(String jobName, long id) {
		this.jobName = jobName;
		this.id = id;
	}

	public String getJobName() {
		return jobName;
	}

	public Long getId() {
		return id;
	}

	public void setFlinkId(String flinkId) {
		this.flinkId = flinkId;
	}

	public String getFlinkId() {
		return flinkId;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return this.email;
	}

	public void setBoundingBox(String boundingBox) {
		this.boundingBox = boundingBox;
	}
}