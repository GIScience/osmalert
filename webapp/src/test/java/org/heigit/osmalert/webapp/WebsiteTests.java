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
	@Disabled
	void rejectJobForInvalidOwnersEmailTest() {
		addJob("job2", "ownersEmailweb", "123.4,12.3,170.5,67.2");
		Locator jobNameElement = page.locator("td:has-text('job2')");
		Locator ownersEmailElement = page.locator("td:has-text('ownersEmailweb.de')");

		String errorMessage = page.locator("#error-message-500").innerText();

		assertThat(jobNameElement).isHidden();
		assertThat(ownersEmailElement).isHidden();
		assertEquals(": Invalid Email", errorMessage);
	}

	@Test
	@Disabled
	void rejectJobForInvalidBoundingBoxTest() {
		addJob("job3", "ownersEmail@web.de", "12.2,12.2,13.2,12.2");
		Locator jobNameElement = page.locator("td:has-text('job3')");
		Locator ownersEmailElement = page.locator("td:has-text('ownersEmail@web.de')");
		String errorMessage = page.locator("#error-message-500").innerText();

		assertThat(jobNameElement).isHidden();
		assertThat(ownersEmailElement).isHidden();
		assertEquals(": Invalid Coordinates", errorMessage);
	}

	@Test
	@Disabled
	void visualizeListOfSubmittedJobsTest() {
		addJob("jobv1", "email@emailv1.de", "121.4,12.3,170.5,67.2");
		addJob("jobv2", "email@emailv2.de", "132.4,12.3,170.5,67.2");
		addJob("jobv3", "email@emailv3.de", "143.4,12.3,170.5,67.2");

		Locator jobNameElementV1 = page.locator("td:has-text('jobv1')");
		Locator ownersEmailElementV1 = page.locator("td:has-text('email@emailv1.de')");

		Locator jobNameElementV2 = page.locator("td:has-text('jobv2')");
		Locator ownersEmailElementV2 = page.locator("td:has-text('email@emailv2.de')");

		Locator jobNameElementV3 = page.locator("td:has-text('jobv3')");
		Locator ownersEmailElementV3 = page.locator("td:has-text('email@emailv3.de')");

		int countStatus = page.locator("td:has-text('RUNNING')").count();

		assertThat(jobNameElementV1).isVisible();
		assertThat(ownersEmailElementV1).isVisible();

		assertThat(jobNameElementV2).isVisible();
		assertThat(ownersEmailElementV2).isVisible();

		assertThat(jobNameElementV3).isVisible();
		assertThat(ownersEmailElementV3).isVisible();
		// TODO change to actual job number and remove flaky by mocking an actual flink service
		assertTrue(countStatus >= 2);
	}

	@Test
	@Disabled
	void rejectJobWithAlreadyExistingName() {
		addJob("joba1", "email@emaila1.de", "121.4,12.3,170.5,67.2");
		addJob("joba1", "email@emaila2.de", "132.4,12.3,170.5,67.2");

		Locator jobNameElementA1 = page.locator("td:has-text('joba1')");
		Locator ownersEmailElementA1 = page.locator("td:has-text('email@emaila1.de')");
		Locator ownersEmailElementA2 = page.locator("td:has-text('email@emaila2.de')");
		String errorMessage = page.locator("#error-message-500").innerText();

		assertThat(jobNameElementA1).isVisible();
		assertThat(ownersEmailElementA1).isVisible();
		assertThat(ownersEmailElementA2).isHidden();
		assertEquals(": JobName already exists", errorMessage);
	}

}