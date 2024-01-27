package org.heigit.osmalert.webapp.domain;

import java.util.*;

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

	//lowerLeftLongitude,upperRightLongitude,lowerLeftLatitude,upperRightLatitude
	private String boundingBox;

	@Positive(message = "Invalid Time Window")
	private int timeWindow;

	private String formattedTimeWindow;

	private String patter;

	protected Job() {}

	public Job(String jobName) {
		this.jobName = jobName;
	}

	public Job(String jobName, Long id) {
		this.jobName = jobName;
		this.id = id;
		this.timeWindow = 1;
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

	public String getBoundingBox() {
		return boundingBox;
	}

	public void setTimeWindow(int timeWindow) {
		this.timeWindow = timeWindow;
	}

	public String getTimeWindowString() {
		return String.valueOf(timeWindow);
	}

	public void setFormattedTimeWindow(String formattedTimeWindow) {
		this.formattedTimeWindow = formattedTimeWindow;
	}

	public String getFormattedTimeWindow() {
		return formattedTimeWindow;
	}

	@SuppressWarnings("OverlyComplexMethod")
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Job job = (Job) o;
		return Objects.equals(id, job.id) && Objects.equals(jobName, job.jobName) && Objects.equals(flinkId, job.flinkId) && Objects.equals(email, job.email) && Objects.equals(boundingBox, job.boundingBox);
	}

	@Override
	public int hashCode() {
		return Objects.hash(jobName, email, boundingBox);
	}

	public String getPatter() {
		return patter;
	}

	public void setPatter(String patter) {
		this.patter = patter;
	}
}