package org.heigit.osmalert.webapp.services;

import org.apache.flink.api.common.*;
import org.apache.flink.runtime.messages.*;
import org.heigit.osmalert.flinkservice.*;
import org.heigit.osmalert.webapp.domain.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class FlinkRemoteJobServiceTests {

	private FlinkRemoteJobService flinkRemoteJobService;
	private JobRepository jobRepository;
	private FlinkClusterService flinkClusterService;

	@BeforeEach
	void initialize() {
		jobRepository = Mockito.mock(JobRepository.class);
		flinkClusterService = Mockito.mock(FlinkClusterService.class);
		flinkRemoteJobService = new FlinkRemoteJobService(jobRepository, flinkClusterService);
	}

	@Test
	void getStatusOfSubmittedJob() throws Exception {
		Job job = new Job("job1", 1L);
		job.setFlinkId("flinkId1");

		when(flinkClusterService.getStatus(job.getFlinkId())).thenReturn(JobStatus.FAILED);

		assertThat(flinkRemoteJobService.getStatus(job))
			.isEqualTo(RemoteJobStatus.FAILED);
	}

	@Test
	void getStatusOfUnSubmittedJob() throws Exception {
		Job job = new Job("job1", 1L);
		assertThat(flinkRemoteJobService.getStatus(job))
			.isEqualTo(RemoteJobStatus.CREATED);
	}

	@Test
	void statusIsUnknownWhenExceptionOccurs() throws Exception {
		Job job = new Job("job1", 1L);
		job.setFlinkId("flinkId1");
		when(flinkClusterService.getStatus(anyString())).thenThrow(
			new RuntimeException("Something went wrong")
		);
		assertThat(flinkRemoteJobService.getStatus(job))
			.isEqualTo(RemoteJobStatus.UNKNOWN);
	}

	@Test
	void statusIsArchivedWhenFlinkJobNotFoundExceptionOccurs() throws Exception {
		Job job = new Job("job1", 1L);
		job.setFlinkId("1234");
		when(flinkClusterService.getStatus(anyString())).thenThrow(
			new FlinkJobNotFoundException(new JobID())
		);
		assertThat(flinkRemoteJobService.getStatus(job))
			.isEqualTo(RemoteJobStatus.ARCHIVED);
	}

	@Test
	void flinkJobStatusIsCorrectlyTranslatedToRemoteJobStatus() {
		checkStatusIsTranslatedTo(JobStatus.INITIALIZING, RemoteJobStatus.SUBMITTED);
		checkStatusIsTranslatedTo(JobStatus.CREATED, RemoteJobStatus.SUBMITTED);
		checkStatusIsTranslatedTo(JobStatus.RUNNING, RemoteJobStatus.RUNNING);
		checkStatusIsTranslatedTo(JobStatus.CANCELLING, RemoteJobStatus.RUNNING);
		checkStatusIsTranslatedTo(JobStatus.RESTARTING, RemoteJobStatus.RUNNING);
		checkStatusIsTranslatedTo(JobStatus.RECONCILING, RemoteJobStatus.RUNNING);
		checkStatusIsTranslatedTo(JobStatus.SUSPENDED, RemoteJobStatus.RUNNING);
		checkStatusIsTranslatedTo(JobStatus.FAILED, RemoteJobStatus.FAILED);
		checkStatusIsTranslatedTo(JobStatus.FAILING, RemoteJobStatus.FAILED);
		checkStatusIsTranslatedTo(JobStatus.CANCELED, RemoteJobStatus.FINISHED);
		checkStatusIsTranslatedTo(JobStatus.FINISHED, RemoteJobStatus.FINISHED);
	}

	private void checkStatusIsTranslatedTo(JobStatus flinkStatus, RemoteJobStatus remoteStatus) {
		RemoteJobStatus translatedStatus = flinkRemoteJobService.toRemoteJobStatus(flinkStatus);
		assertThat(translatedStatus)
			.describedAs("Flink status %s should be translated to %s", flinkStatus, remoteStatus)
			.isEqualTo(remoteStatus);
	}

	@Test
	void unsubmittedJobCanBeSubmitted() throws Exception {
		Job job = new Job("job1");
		job.setEmail("user@example.org");
		when(flinkClusterService.submitJarJobToCluster(job.getJobName(), job.getEmail())).
			thenReturn("new-flink-id");

		flinkRemoteJobService.submit(job);

		verify(jobRepository, times(1)).save(job);
		assertThat(job.getFlinkId()).isEqualTo("new-flink-id");
	}
}
