package org.heigit.osmalert.flinkjobjar;

public class JobParams {
	private final String[] args;

	public JobParams(String[] args) {
		this.args = args;
	}

	public String getJobName() {
		String jobName = "AlertJob_" + args[0];
		System.out.println("=== " + jobName + " ===");
		return jobName;
	}

	public String getEmailAddress() {
		String emailAddress = args[1];
		System.out.println("=== " + emailAddress + " ===");
		return emailAddress;
	}

	public String getBoundingBoxString() {
		return args[2];
	}

	public int getTimeWindowInMinutes() {
		return Integer.parseInt(args[3]);
	}
}
