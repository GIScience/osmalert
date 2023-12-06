package org.heigit.osmalert.webapp.services;

import org.heigit.osmalert.webapp.domain.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JobsServiceTests {

	private JobRepository jobRepository;
	private JobsService jobsService;
	private RemoteJobService remoteJobService;

	@BeforeEach
	void initialize() {
		jobRepository = Mockito.mock(JobRepository.class);
		remoteJobService = Mockito.mock(RemoteJobService.class);
		jobsService = new JobsService(jobRepository, remoteJobService);
	}

	@Test
	void checkGetAllJobs() {
		List<Job> jobs = List.of(
			new Job("job1"),
			new Job("job2")
		);
		Mockito.when(jobRepository.findAll()).thenReturn(jobs);
		List<Job> result = jobsService.getAllJobs();
		assertThat(jobs).isEqualTo(result);
	}

	@Test
	void checkNormalizeJobName() {
		assertThat("jobname").isEqualTo(JobsService.normalizeString("    jobName"));
		assertThat("jobname").isEqualTo(JobsService.normalizeString("    jobName          "));
		assertThat("job name").isEqualTo(JobsService.normalizeString("job    Name"));
		assertThat("jobname").isEqualTo(JobsService.normalizeString("JOBNAME"));
		assertThat("job name ggg").isEqualTo(JobsService.normalizeString("    job   Name       ggg   "));
		assertThat("").isEqualTo(JobsService.normalizeString("      "));
	}

	@Test
	void checkNormalizeBBoxCoordinates() {
		assertThat("123,13,124,15").isEqualTo(JobsService.normalizeString("123,13,124,15  "));
		assertThat("123,13,124,15").isEqualTo(JobsService.normalizeString("   123,13,124,15"));
		assertThat("123,13,124,15").isEqualTo(JobsService.normalizeString("123,13,124,15\n"));
	}


	@Test
	void checkSaveNewJob() {
		Job job1 = new Job("job1");
		jobsService.saveNewJob(job1);
		Mockito.verify(jobRepository).save(job1);
	}

	@Test
	void checkCheckRunningJobs() {
		Job job1 = new Job("job1");
		Mockito.when(jobRepository.findAll()).thenReturn(List.of(job1));
		Mockito.when(remoteJobService.getStatus(job1)).thenReturn(RemoteJobStatus.RUNNING);
		boolean result = jobsService.isJobRunning("job1");
		assertThat(result).isEqualTo(true);
	}

	@Test
	void checkIsJobFailedFinished() {
		Job job1 = new Job("job1");
		Mockito.when(remoteJobService.getStatus(job1)).thenReturn(RemoteJobStatus.FAILED);
		assertThat(jobsService.isJobFailedFinished(job1)).isEqualTo(true);

		Job job2 = new Job("job2");
		Mockito.when(remoteJobService.getStatus(job2)).thenReturn(RemoteJobStatus.FINISHED);
		assertThat(jobsService.isJobFailedFinished(job2)).isEqualTo(true);
	}

	@Test
	void checkGetJobStatus() {
		Job unsubmittedJob = new Job("one", 1L);
		Job submittedJob = new Job("two", 2L);
		submittedJob.setFlinkId("fakeID");

		when(jobRepository.findById(1L)).thenReturn(Optional.of(unsubmittedJob));
		when(jobRepository.findById(2L)).thenReturn(Optional.of(submittedJob));
		when(jobRepository.findById(3L)).thenReturn(null);

		when(remoteJobService.getStatus(unsubmittedJob)).thenReturn(RemoteJobStatus.CREATED);
		when(remoteJobService.getStatus(submittedJob)).thenReturn(RemoteJobStatus.SUBMITTED);

		assertThat(jobsService.getJobStatus(1L)).isEqualTo("CREATED");
		assertThat(jobsService.getJobStatus(2L)).isEqualTo("SUBMITTED");
		assertThatExceptionOfType(RuntimeException.class)
			.isThrownBy(() -> jobsService.getJobStatus(3L));

	}

}
