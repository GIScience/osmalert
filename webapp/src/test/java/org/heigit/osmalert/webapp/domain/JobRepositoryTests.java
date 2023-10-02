package org.heigit.osmalert.webapp.domain;

import java.util.*;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.autoconfigure.orm.jpa.*;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class JobRepositoryTests {

	@Autowired
	JobRepository repository;

	@Test
	void addJobRequest() {
		Job job = new Job("my job");
		Job saved = repository.save(job);

		Optional<Job> retrieved = repository.findById(saved.getId());

		assertThat(retrieved).isNotEmpty();
		assertThat(retrieved.get().getJobName()).isEqualTo("my job");
	}

	@Test
	void findUnsubmittedJobs() {
		List<Job> jobs = List.of(
			createSubmittedJob("job1", 1L),
			createSubmittedJob("job2", 2L),
			new Job("job3", 3L),
			new Job("job4", 4L)
		);
		repository.saveAll(jobs);

		Iterable<Job> unsubmittedJobs = repository.findUnsubmittedJobs();

		assertThat(unsubmittedJobs).hasSize(2);
		assertThat(unsubmittedJobs).extracting(Job::getJobName)
								   .containsExactlyInAnyOrder("job3", "job4");
	}

	private static Job createSubmittedJob(String jobName, long jobId) {
		Job newJob = new Job(jobName, jobId);
		newJob.setFlinkId("flink-" + jobId);
		return newJob;
	}
}
