package org.heigit.osmalert.webapp;

import com.microsoft.playwright.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.*;
import org.springframework.boot.test.web.server.*;

@SpringBootTest(
	webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class WebsiteTest {
	static Playwright playwright;
	static Browser browser;

	// New instance for each test method.
	private BrowserContext context;
	private Page page;

	@LocalServerPort
	int port;

	@BeforeAll
	static void launchBrowser() {
		playwright = Playwright.create();
		/*
		 Code for using chrome or webkit
		 playwright.chromium().launch();
		 playwright.webkit().launch();
		*/
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
}