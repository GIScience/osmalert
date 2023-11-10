package org.heigit.osmalert.webapp;

import org.springframework.boot.context.properties.*;
import org.springframework.stereotype.*;

@Component("osmalert")
@EnableConfigurationProperties(OsmalertConfiguration.class)
@ConfigurationProperties(prefix = "osmalert")
public class OsmalertConfiguration {

	private String webappTitle;
	private long submissionPollingInterval;
	private boolean isFlinkEnabled;
	private String flinkHost;
	private int flinkPort;
	private int flinkMaxRetryAttempts;
	private String webPassword;
	private String webUsername;

	public String getWebPassword() {
		return webPassword;
	}

	public void setWebPassword(String webPassword) {
		this.webPassword = webPassword;
	}

	public String getWebUsername() {
		return webUsername;
	}

	public void setWebUsername(String webUsername) {
		this.webUsername = webUsername;
	}

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

	public void setFlinkHost(String flinkHost) {this.flinkHost = flinkHost;}

	public String getFlinkHost() {return flinkHost;}

	public void setFlinkPort(int flinkPort) {this.flinkPort = flinkPort;}

	public int getFlinkPort() {return flinkPort;}

	public void setFlinkMaxRetryAttempts(int flinkMaxRetryAttempts) {this.flinkMaxRetryAttempts = flinkMaxRetryAttempts;}

	public int getFlinkMaxRetryAttempts() {return flinkMaxRetryAttempts;}

}