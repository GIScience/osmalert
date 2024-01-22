package org.heigit.osmalert.flinkjobjar;

public class JobConfiguration {

	private final String jobName;
	private final String emailAddress;
	private final double[] boundingBox;
	private final String boundingBoxString;
	private final int timeWindow;

	public JobConfiguration(String[] params) {
		this.jobName = setJobName(params);
		this.emailAddress = setEmailAddress(params);
		this.boundingBoxString = setBoundingBoxStringArray(params);
		this.boundingBox = setBoundingBoxValues(this.boundingBoxString);
		this.timeWindow = setTimeWindow(params);
	}

	public String setJobName(String[] args) {
		String jobName = "AlertJob_" + args[0];
		System.out.println("=== " + jobName + " ===");
		return jobName;
	}

	public String setEmailAddress(String[] args) {
		String emailAdress = args[1];
		System.out.println("=== " + emailAdress + " ===");
		return emailAdress;
	}

	public double[] setBoundingBoxValues(String input) {
		String[] boundingBoxValues = input.split(",");
		double[] doubleArray = new double[4];
		for (int i = 0; i < 4; i++)
			doubleArray[i] = Double.parseDouble(boundingBoxValues[i]);
		return doubleArray;
	}

	public String setBoundingBoxStringArray(String[] args) {
		return args[2];
	}

	public int setTimeWindow(String[] args) {
		return Integer.parseInt(args[3]);
	}

	public String getJobName() {return this.jobName;}

	public String getEmailAddress() {return this.emailAddress;}

	public double[] getBoundingBox() {return this.boundingBox;}

	public double getBoundingBoxValues(int position) {return this.boundingBox[position];}

	public String getBoundingBoxString() {return this.boundingBoxString;}

	public int getTimeWindow() {return this.timeWindow;}
}