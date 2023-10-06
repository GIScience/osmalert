package org.heigit.osmalert.webapp.domain;

import jakarta.persistence.*;

@Entity
public class Job {

	@Id
	@GeneratedValue
	private Long id;

	private String jobName;

	private String flinkId;

	private String email;

	protected Job() { }

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
}
