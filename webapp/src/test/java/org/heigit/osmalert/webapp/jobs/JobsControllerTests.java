package org.heigit.osmalert.webapp.jobs;

import java.util.*;

import org.hamcrest.*;
import org.heigit.osmalert.webapp.domain.*;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.context.*;
import org.springframework.boot.test.mock.mockito.*;
import org.springframework.security.test.context.support.*;
import org.springframework.test.web.servlet.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class JobsControllerTests {

	@Autowired
	MockMvc mockMvc;

	@SpyBean
	JobRepository jobRepository;

	@MockBean
	RemoteJobService remoteJobService;

	@Test
	@WithMockUser
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
	@WithMockUser
	void postNewJob() throws Exception {
		ArgumentCaptor<Job> jobRequestCaptor = ArgumentCaptor.forClass(Job.class);

		mockMvc.perform(post("/jobs")
							.param("jobName", "Post New Job")
							.param("ownersEmail", "123@web.de"))
			   .andExpect(status().isOk())
			   .andExpect(model().attributeExists("jobs"))
			   .andExpect(view().name("jobs::joblist"));

		verify(jobRepository).save(jobRequestCaptor.capture());

		assertThat(jobRequestCaptor.getValue().getJobName())
			.isEqualTo("post new job");
		assertThat(jobRequestCaptor.getValue().getEmail())
			.isEqualTo("123@web.de");

		mockMvc.perform(post("/jobs")
							.param("jobName", "Post New Job")
							.param("ownersEmail", "123@web.de"))
			   .andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser
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

	@Test
	@WithMockUser
	void checkValidJobName() throws Exception {
		mockMvc.perform(post("/jobs")
							.param("jobName", "checkvalidjobname")
							.param("ownersEmail", "hello@world.com"))
			   .andExpect(status().isOk());
		mockMvc.perform(post("/jobs")
							.param("jobName", "check valid Jobname")
							.param("ownersEmail", "hello@world.com"))
			   .andExpect(status().isOk());
		mockMvc.perform(post("/jobs")
							.param("jobName", " check Valid Job  Name2 ")
							.param("ownersEmail", "hello@world.com"))
			   .andExpect(status().isOk());
	}

	@Test
	@WithMockUser
	void rejectEmptyJobName() throws Exception {

		mockMvc.perform(post("/jobs")
							.param("jobName", "")
							.param("ownersEmail", "Something@hallo.de"))
			   .andExpect(status().isBadRequest())
			   .andExpect(content().string(
				   Matchers.containsString("Invalid jobName")
			   ));
	}

	@Test
	@WithMockUser
	void rejectInvalidEmail() throws Exception {
		mockMvc.perform(post("/jobs")
							.param("jobName", "InvalidEmailJobName1")
							.param("ownersEmail", "123a"))
			   .andExpect(status().isBadRequest())
			   .andExpect(content().string(
				   Matchers.containsString("Invalid Email")
			   ));

		mockMvc.perform(post("/jobs")
							.param("jobName", "InvalidEmailJobName2")
							.param("ownersEmail", "abc@def"))
			   .andExpect(status().isBadRequest())
			   .andExpect(content().string(
				   Matchers.containsString("Invalid Email")
			   ));

		mockMvc.perform(post("/jobs")
							.param("jobName", "jobName3")
							.param("ownersEmail", "@abc"))
			   .andExpect(status().isBadRequest())
			   .andExpect(content().string(
				   Matchers.containsString("Invalid Email")
			   ));

	}

	@Test
	@WithMockUser
	void checkValidEmail() throws Exception {
		mockMvc.perform(post("/jobs")
							.param("jobName", "checkValidEmail1")
							.param("ownersEmail", "hello@world.com"))
			   .andExpect(status().isOk());
	}
}