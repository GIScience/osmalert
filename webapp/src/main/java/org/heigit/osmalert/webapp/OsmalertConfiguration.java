package org.heigit.osmalert.webapp;

import org.springframework.boot.context.properties.*;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.*;

@Component("osmalert")
@EnableConfigurationProperties(OsmalertConfiguration.class)
@ConfigurationProperties(prefix = "osmalert")
public class OsmalertConfiguration {

	private String webappTitle;
	private long submissionPollingInterval;
	private boolean isFlinkEnabled;

	public String getWebappTitle() {
		return webappTitle;
	}

	public void setWebappTitle(String webappTitle) {
		this.webappTitle = webappTitle;
	}

	public long getSubmissionPollingInterval() {
		return submissionPollingInterval;
	}

	public void setSubmissionPollingInterval(long submissionPollingInterval) {
		this.submissionPollingInterval = submissionPollingInterval;
	}

	public void setFlinkEnabled(boolean flinkEnabled) {
		isFlinkEnabled = flinkEnabled;
	}

	public boolean isFlinkEnabled() {
		return isFlinkEnabled;
	}
}
