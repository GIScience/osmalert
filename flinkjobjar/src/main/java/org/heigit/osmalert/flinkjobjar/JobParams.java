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
}
