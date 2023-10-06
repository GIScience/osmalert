package org.heigit.osmalert.webapp;

import org.heigit.osmalert.webapp.domain.Job;
import org.heigit.osmalert.webapp.domain.JobRepository;
import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;

@Component
public class InitJobRequestComponent implements InitializingBean {

	private final JobRepository repository;

	public InitJobRequestComponent(JobRepository repository) {
		this.repository = repository;
	}

	@Override
	public void afterPropertiesSet() {
		Job job1 = new Job("job 1");
		job1.setEmail("IAMAFAKE@Email.com");
		job1.setFlinkId("fake-1");
		repository.save(job1);
		Job job2 = new Job("job 2");
		job2.setEmail("IAMASECONDFAKE@Email.com");
		job2.setFlinkId("fake-2");
		repository.save(job2);
	}
}
