package org.heigit.osmalert.webapp;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.boot.test.web.server.*;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class WebsiteTest {

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
		browser = playwright.firefox().launch();
	}

	@AfterAll
	static void closeBrowser() {
		playwright.close();
	}

	@BeforeEach
	void createContextAndPage() {
		context = browser.newContext();
		page = context.newPage();

		page.navigate("http://localhost:" + port + "/login");
		page.getByLabel("username").fill(username);
		page.getByLabel("password").fill(password);
		page.locator("button").click();
		page.navigate("http://localhost:" + port);
	}

	@AfterEach
	void closeContext() {
		context.close();
	}

	@Test
	public void osmalertLogInPage() {
		page.navigate("http://localhost:%d".formatted(port));
		Assertions.assertThat(page.title()).isEqualTo("Please sign in");
		page.close();
	}

	@Test
	void loginFailedTest() {
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

	@Test
	void acceptValidJobTest() {
		page.locator("//input[@id='jobName']").fill("job1");
		page.locator("//input[@id='ownersEmail']").fill("123@web.de");
		page.locator("//input[@id='boundingBox']").fill("123.4,12.3,170.5,67.2");
		page.locator("#createNewJob").click();
		page.waitForTimeout(2000);

		Locator jobNameElement = page.locator("td:has-text('job1')");
		Locator emailElement = page.locator("td:has-text('123@web.de')");

		assertThat(jobNameElement).isVisible();
		assertThat(emailElement).isVisible();
	}

	@Test
	void rejectJobForInvalidOwnersEmailTest() {
		page.locator("//input[@id='jobName']").fill("job2");
		page.locator("//input[@id='ownersEmail']").fill("1234web.de");
		page.locator("//input[@id='boundingBox']").fill("123.4,12.3,120.5,67.2");
		page.locator("#createNewJob").click();
		page.waitForTimeout(2000);

		Locator emailElement = page.locator("td:has-text('1234web.de')");

		assertThat(emailElement).isHidden();
	}

	@Test
	void rejectJobForInvalidBoundingBoxTest() {
		page.locator("//input[@id='jobName']").fill("job3");
		page.locator("//input[@id='ownersEmail']").fill("123@web.de");
		page.locator("//input[@id='boundingBox']").fill("12.2,12.2,13.2,12.2");
		page.locator("#createNewJob").click();
		page.waitForTimeout(2000);

		Locator emailElement = page.locator("td:has-text('job3')");

		assertThat(emailElement).isHidden();
	}
}