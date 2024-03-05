package org.heigit.osmalert.webapp.domain;

import java.time.*;
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
		job.setEmail("Something@email.de");
		job.setBoundingBox("12.3, 55.5, 120.6, 95.4");
		Job saved = repository.save(job);

		Optional<Job> retrieved = repository.findById(saved.getId());
		assertThat(retrieved.orElseThrow().getEmail()).isEqualTo("Something@email.de");
		assertThat(retrieved.get().getBoundingBox()).isEqualTo("12.3, 55.5, 120.6, 95.4");
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

	@Test
	void findJobsByExpirationDateBefore() {

		LocalDate today = LocalDate.now();
		LocalDate nextDay = today.plusDays(1);
		Date nextDayDate = java.sql.Date.valueOf(nextDay);


		List<Job> jobs = List.of(
			new Job(3L, "job3", new Date()),
			new Job(4L, "job4", new Date()),
			new Job(5L, "job5", nextDayDate)
		);
		repository.saveAll(jobs);

		Iterable<Job> jobsByExpirationDateBefore = repository.findJobsByExpirationDateBefore();
		assertThat(jobsByExpirationDateBefore).hasSize(2);
		assertThat(jobsByExpirationDateBefore).extracting(Job::getJobName)
								   .containsExactlyInAnyOrder("job3", "job4");
	}

	private static Job createSubmittedJob(String jobName, long jobId) {
		Job newJob = new Job(jobName, jobId);
		newJob.setFlinkId("flink-" + jobId);
		return newJob;
	}
}
