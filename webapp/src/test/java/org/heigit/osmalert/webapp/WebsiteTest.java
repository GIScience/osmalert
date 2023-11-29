package org.heigit.osmalert.webapp;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

public class WebsiteTest {
	static Playwright playwright;
	static Browser browser;

	// New instance for each test method.
	private BrowserContext context;
	private Page page;

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
	public void t() {
		page.close();
	}
}