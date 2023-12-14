package org.heigit.osmalert.webapp;

import java.util.*;

import com.microsoft.playwright.*;
import org.heigit.osmalert.webapp.domain.*;
import org.heigit.osmalert.webapp.services.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.boot.test.mock.mockito.*;
import org.springframework.boot.test.web.server.*;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebsiteTests {

	@MockBean
	JobsService jobsService;

	@LocalServerPort
	private int port;

	@Value("${osmalert.web-username}")
	private String username;

	@Value("${osmalert.web-password}")
	private String password;

	static Playwright playwright;
	static Browser browser;

	private BrowserContext context;
	private Page page;

	@BeforeAll
	static void launchBrowser() {
		playwright = Playwright.create();
		browser = playwright.firefox().launch(
			//new BrowserType.LaunchOptions().setHeadless(false).setSlowMo(1000)
		);
	}

	@AfterAll
	static void closeBrowser() {
		playwright.close();
	}

	@BeforeEach
	void createContextAndPage() {
		// By default, all coordinates are valid
		when(jobsService.validateCoordinates(anyString())).thenReturn(true);
		when(jobsService.calculateTimeWindow(any(), any())).thenReturn(1);
		// By default, all jobs are created
		when(jobsService.getJobStatus(anyLong())).thenReturn("CREATED");

		context = browser.newContext();
		page = context.newPage();

		page.navigate("http://localhost:" + port + "/login");
		page.getByLabel("username").fill(username);
		page.getByLabel("password").fill(password);
		page.waitForResponse(
			"**/*",
			() -> page.click("button")
		);
		page.navigate("http://localhost:" + port);
	}

	@AfterEach
	void closeContext() {
		context.close();
	}

	@Test
	void loginFailTest() {
		page.navigate("http://localhost:" + port + "/login");
		page.getByLabel("username").fill("user123");
		page.getByLabel("password").fill("1234");
		page.locator("button").click();
		assertEquals("http://localhost:" + port + "/login?error", page.url());
	}

	@Test
	void loginSuccessTest() {
		page.navigate("http://localhost:" + port + "/login");
		page.getByLabel("username").fill(username);
		page.getByLabel("password").fill(password);
		page.locator("button").click();
		assertEquals("http://localhost:" + port + "/", page.url());

	}

	private void addJob(String jobName, String ownersEmail, String boundingBox) {
		page.locator("//input[@id='jobName']").fill(jobName);
		page.locator("//input[@id='ownersEmail']").fill(ownersEmail);
		page.locator("//input[@id='boundingBox']").fill(boundingBox);
		// page.locator("//input[@id='timeWindow']");
		// page.locator("id=timeFormat").getByLabel("Time Format");
		page.waitForResponse(
			"**/*",
			() -> page.click("#createNewJob")
		);
	}

	@Test
	void submittingJobWillSaveNewJob() {
		clearInvocations(jobsService);

		page.locator("//input[@id='jobName']").fill("job1");
		page.locator("//input[@id='ownersEmail']").fill("123@web.de");
		page.locator("//input[@id='boundingBox']").fill("123.4,12.3,170.5,67.2");

		// Required for in-page replacements (e.g. through htmx) when no sleep or slo-mo is used
		page.waitForResponse(
			"**/*",
			() -> page.click("#createNewJob")
		);

		Job expectedJob = createJob(null, "job1", "123@web.de", "123.4,12.3,170.5,67.2");

		verify(jobsService, times(1)).saveNewJob(expectedJob);
		verify(jobsService).saveNewJob(expectedJob);
		verify(jobsService).getAllJobs();
	}

	@Test
	void jobsInRepositoryAreDisplayedCorrectly() {
		when(jobsService.getAllJobs()).thenReturn(
			List.of(
				createJob(1L, "job1", "jl1@test.com", "1,2,3,4"),
				createJob(2L, "job2", "jl2@test.com", "5,6,7,8"),
				createJob(3L, "job3", "jl3@test.com", "9,10,11,12")
			)
		);

		page.navigate("http://localhost:" + port);

		assertJobRow("1", "job1", "jl1@test.com", "1,2,3,4");
		assertJobRow("2", "job2", "jl2@test.com", "5,6,7,8");
		assertJobRow("3", "job3", "jl3@test.com", "9,10,11,12");
	}

	private void assertJobRow(String id, String jobName, String email, String boundingBox) {
		Locator jobLocator = page.locator("tbody[id='" + id + "']");
		assertThat(jobLocator).isVisible();
		assertThat(jobLocator.locator("td:has-text('" + jobName + "')")).isVisible();
		assertThat(jobLocator.locator("td:has-text('" + email + "')")).isVisible();
		assertThat(jobLocator.locator("td:has-text('" + boundingBox + "')")).isVisible();
	}

	private static Job createJob(Long id, String jobName, String email, String boundingBox) {
		Job job = new Job(jobName, id);
		job.setEmail(email);
		job.setBoundingBox(boundingBox);
		return job;
	}

	@Test
	@Disabled("No Validation function in the jobservice to check if the email is valid or not")
	void rejectJobForInvalidOwnersEmailTest() {
		Job expectedJob = createJob(null, "job1", "ownersEmailweb", "123.4,12.3,170.5,67.2");

		addJob("job1", "ownersEmailweb", "123.4,12.3,170.5,67.2");

		verify(jobsService, times(0)).saveNewJob(expectedJob);
	}

	@Test
	void rejectJobForInvalidBoundingBoxTest() {
		when(jobsService.validateCoordinates("12.2,12.2,13.2,12.2")).thenReturn(false);

		createJob(0L, "job1", "ownersEmail@web.de", "12.2,12.2,13.2,12.2");

		Job expectedJob = createJob(null, "job1", "ownersEmail@web.de", "12.2,12.2,13.2,12.2");

		verify(jobsService, times(0)).saveNewJob(expectedJob);
	}

	@Test
	void rejectJobWithAlreadyExistingName() {
		clearInvocations(jobsService);
		when(jobsService.getAllJobs()).thenReturn(
			List.of(
				createJob(1L, "job1", "email@emaila1.de", "121.4,12.3,170.5,67.2")
			)
		);
		Job job1 = createJob(null, "job1", "email@emaila1.de", "121.4,12.3,170.5,67.2");
		Job job2 = createJob(2L, "job1", "email@emaila1.de", "121.4,12.3,170.5,67.2");

		addJob("job1", "email@emaila1.de", "121.4,12.3,170.5,67.2");

		when(jobsService.isJobRunning("joba1")).thenReturn(true);

		addJob("job1", "email@emaila2.de", "121.4,12.3,170.5,67.2");

		assertJobRow("1", "job1", "email@emaila1.de", "121.4,12.3,170.5,67.2");
		verify(jobsService, times(1)).saveNewJob(job1);
		verify(jobsService, times(0)).saveNewJob(job2);
	}
}