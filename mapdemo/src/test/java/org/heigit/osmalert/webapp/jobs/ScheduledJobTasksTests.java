package org.heigit.osmalert.webapp.jobs;

import java.util.*;

import org.heigit.osmalert.webapp.*;
import org.heigit.osmalert.webapp.domain.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduledJobTasksTests {

	@InjectMocks
	ScheduledJobTasks scheduledJobTasks;

	@Mock
	RemoteJobService remoteJobService;

	@Mock
	JobRepository jobRepository;

	@Test
	void submitNewJobs() {
		List<Job> jobs = List.of(
			new Job("job3", 3L),
			new Job("job4", 4L)
		);
		when(jobRepository.findUnsubmittedJobs()).thenReturn(jobs);

		scheduledJobTasks.submitNewJobs();

		verify(remoteJobService).submit(jobs.get(0));
		verify(remoteJobService).submit(jobs.get(1));
	}
}
