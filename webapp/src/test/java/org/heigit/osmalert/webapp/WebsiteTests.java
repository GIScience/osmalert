package org.heigit.osmalert.webapp;

import java.util.*;

import com.microsoft.playwright.*;
import org.heigit.osmalert.webapp.domain.*;
import org.heigit.osmalert.webapp.services.*;
import org.junit.jupiter.api.*;
import org.junit.platform.commons.util.*;
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

		Job expectedJob = createJob(null, null, "job1", "123@web.de", "123.4,12.3,170.5,67.2", "");

		verify(jobsService, times(1)).saveNewJob(expectedJob);
		verify(jobsService).getAllJobs();
	}

	@Test
	void jobsInRepositoryAreDisplayedCorrectly() {
		when(jobsService.getAllJobs()).thenReturn(List.of(
			createJob(1L, "3faef48d72ce4df6007db4dcbff0074e", "jobA", "jlA@test.com", "0,2,3,4", "2 Hours"),
			createJob(2L, "3faef48d72ce4df6007db4dcbff0074e", "jobB", "jlB@test.com", "5,6,7,8", "3 Minutes"),
			createJob(3L, "3faef48d72ce4df6007db4dcbff0074e", "jobC", "jlC@test.com", "9,10,11,12", "14 Minutes")
		));

		page.navigate("http://localhost:" + port);

		assertJobRow("1", "3faef48d72ce4df6007db4dcbff0074e", "jobA", "jlA@test.com", "0,2,3,4", "2 Hours");
		assertJobRow("2", "3faef48d72ce4df6007db4dcbff0074e", "jobB", "jlB@test.com", "5,6,7,8", "3 Minutes");
		assertJobRow("3", "3faef48d72ce4df6007db4dcbff0074e", "jobC", "jlC@test.com", "9,10,11,12", "14 Minutes");
	}

	private void assertJobRow(String id, String flinkId, String jobName, String email, String boundingBox, String timeWindow) {
		Locator jobLocator = page.locator("tbody[id='" + id + "']");
		assertThat(jobLocator).isVisible();
		assertThat(jobLocator.locator("td:has-text('" + jobName + "')")).isVisible();
		assertThat(jobLocator.locator("td:text('" + flinkId + "')")).isVisible();
		assertThat(jobLocator.locator("td:has-text('" + email + "')")).isVisible();
		assertNotNull(page.querySelector("a:has-text('" + boundingBox + "')"));
		assertThat(jobLocator.locator("td:has-text('" + timeWindow + "')")).isVisible();
	}

	private static Job createJob(Long id, String flinkId, String jobName, String email, String boundingBox, String formattedTimeWindow) {
		Job job = new Job(jobName, id);
		job.setEmail(email);
		job.setBoundingBox(boundingBox);
		job.setFlinkId(flinkId);
		job.setFormattedTimeWindow(StringUtils.isBlank(formattedTimeWindow) ? "1 Minutes" : formattedTimeWindow);
		return job;
	}

	@Test
	@Disabled("No Validation function in the jobservice to check if the email is valid or not")
	void rejectJobForInvalidOwnersEmailTest() {
		Job expectedJob = createJob(null, "3faef48d72ce4df6007db4dcbff0074e", "job1", "ownersEmailweb", "123.4,12.3,170.5,67.2", "");

		addJob("job1", "ownersEmailweb", "123.4,12.3,170.5,67.2");

		verify(jobsService, times(0)).saveNewJob(expectedJob);
	}

	@Test
	void rejectJobForInvalidBoundingBoxTest() {
		when(jobsService.validateCoordinates("12.2,12.2,13.2,12.2")).thenReturn(false);

		createJob(0L, "3faef48d72ce4df6007db4dcbff0074e", "job1", "ownersEmail@web.de", "12.2,12.2,13.2,12.2", "");

		Job expectedJob = createJob(null, "3faef48d72ce4df6007db4dcbff0074e", "job1", "ownersEmail@web.de", "12.2,12.2,13.2,12.2", "");

		verify(jobsService, times(0)).saveNewJob(expectedJob);
	}

	@Test
	void rejectJobWithAlreadyExistingName() {
		clearInvocations(jobsService);
		when(jobsService.getAllJobs()).thenReturn(List.of(createJob(1L, "3faef48d72ce4df6007db4dcbff0074e", "job2", "email@emaila2.de", "2.4,2.3,70.5,67.2", "2 Minutes")));
		Job job2 = createJob(2L, "", "job2", "email@emaila2.de", "121.4,12.3,170.5,67.2", "");

		addJob("job1", "email@emaila2.de", "2.4,2.3,70.5,67.2");

		when(jobsService.isJobRunning("job2")).thenReturn(true);

		addJob("job2", "email@emaila2.de", "2.4,2.3,70.5,67.2");

		assertJobRow("1", "3faef48d72ce4df6007db4dcbff0074e", "job2", "email@emaila2.de", "2.4,2.3,70.5,67.2", "2 Minutes");
		verify(jobsService, times(0)).saveNewJob(job2);
	}
}