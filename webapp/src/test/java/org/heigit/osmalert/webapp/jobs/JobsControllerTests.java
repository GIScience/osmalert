package org.heigit.osmalert.webapp.jobs;

import java.util.*;

import org.hamcrest.*;
import org.heigit.osmalert.webapp.*;
import org.heigit.osmalert.webapp.domain.*;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.mock.mockito.*;
import org.springframework.test.web.servlet.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = {JobsController.class, OsmalertConfiguration.class})
class JobsControllerTests {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	JobRepository jobRepository;

	@MockBean
	RemoteJobService remoteJobService;

	@Test
	void getAllJobs() throws Exception {
		List<Job> jobs = List.of(
			new Job("job1"),
			new Job("job2")
		);
		when(jobRepository.findAll()).thenReturn(jobs);
		when(remoteJobService.getStatus(any())).thenReturn(RemoteJobStatus.RUNNING);

		mockMvc.perform(get("/jobs"))
			   .andExpect(status().isOk())
			   .andExpect(model().attribute("jobs", jobs))
			   .andExpect(view().name("jobs"))
			   .andExpect(content().string(
				   Matchers.containsString("job1")
			   ))
			   .andExpect(content().string(
				   Matchers.containsString("job2")
			   ));
	}

	@Test
	void postNewJob() throws Exception {
		ArgumentCaptor<Job> jobRequestCaptor = ArgumentCaptor.forClass(Job.class);

		mockMvc.perform(post("/jobs")
							.param("jobName", "A job name")
							.param("ownersEmail", "Something"))
			   .andExpect(status().isOk())
			   .andExpect(model().attributeExists("jobs"))
			   .andExpect(view().name("jobs::joblist"));

		verify(jobRepository).save(jobRequestCaptor.capture());

		assertThat(jobRequestCaptor.getValue().getJobName())
			.isEqualTo("A job name");
		assertThat(jobRequestCaptor.getValue().getEmail())
			.isEqualTo("Something");
	}

	@Test
	void getJobStatus() throws Exception {
		Job unsubmittedJob = new Job("one", 1L);
		Job submittedJob = new Job("two", 2L);
		submittedJob.setFlinkId("fakeID");

		when(jobRepository.findById(1L)).thenReturn(Optional.of(unsubmittedJob));
		when(jobRepository.findById(2L)).thenReturn(Optional.of(submittedJob));


		when(remoteJobService.getStatus(unsubmittedJob)).thenReturn(RemoteJobStatus.CREATED);
		when(remoteJobService.getStatus(submittedJob)).thenReturn(RemoteJobStatus.SUBMITTED);

		mockMvc.perform(get("/jobs/status").queryParam("jobId", "1"))
			   .andExpect(status().isOk())
			   .andExpect(content().string(RemoteJobStatus.CREATED.name()));

		mockMvc.perform(get("/jobs/status").queryParam("jobId", "2"))
			   .andExpect(status().isOk())
			   .andExpect(content().string(RemoteJobStatus.SUBMITTED.name()));
	}

}
