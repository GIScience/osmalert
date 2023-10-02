package org.heigit.osmalert.flinkservice;

import java.io.*;

import org.apache.flink.api.common.*;
import org.apache.flink.client.deployment.*;
import org.apache.flink.client.program.*;
import org.apache.flink.client.program.rest.*;
import org.apache.flink.configuration.*;
import org.apache.flink.runtime.client.*;
import org.apache.flink.runtime.jobgraph.*;

import static org.apache.flink.api.common.JobID.*;
import static org.apache.flink.api.common.JobStatus.*;

public class FlinkClusterService {

	// Must be consistent with the module name creating the jar
	final private static String FLINKJOBJAR_RESOURCE_NAME = "flinkjobjar-all.jar";

	final private static FlinkRestsConfiguration config = new FlinkRestsConfiguration(
		"localhost",
		8081,
		3
	);

	private static Configuration asFlinkConfiguration(FlinkRestsConfiguration restConfiguration) {
		Configuration config = new Configuration();
		config.setString(JobManagerOptions.ADDRESS, restConfiguration.address());
		config.setInteger(RestOptions.PORT, restConfiguration.port());
		config.setInteger(RestOptions.RETRY_MAX_ATTEMPTS, restConfiguration.retryMaxAttempts());

		return config;
	}

	final private ClusterClient<?> flinkClient;

	final private File jarFile = getJarFile(FLINKJOBJAR_RESOURCE_NAME);

	public FlinkClusterService() throws Exception {
		this(config);
	}

	public FlinkClusterService(FlinkRestsConfiguration restConfiguration) throws Exception {
		this(new RestClusterClient<>(asFlinkConfiguration(restConfiguration), StandaloneClusterId.getInstance()));
	}

	public FlinkClusterService(ClusterClient<?> flinkClient) {
		this.flinkClient = flinkClient;
	}

	public String submitJarJobToCluster(String jobName) throws Exception {

		System.out.println("jobName = " + jobName);

		PackagedProgram program = PackagedProgram.newBuilder()
												 .setJarFile(getFlinkJobJar())
												 .setArguments(jobName)
												 .build();

		JobGraph jobGraph = PackagedProgramUtils.createJobGraph(program, asFlinkConfiguration(config), 1, false);

		JobID jobId = flinkClient.submitJob(jobGraph).get();
		return jobId.toString();
	}

	public boolean isNotFailed(String jobId) throws Exception {

		return getStatus(jobId) != FAILED;
	}

	public JobStatus getStatus(String jobId) throws Exception {
		JobID id = fromHexString(jobId);
		return flinkClient.getJobStatus(id).get();
	}

	public String getJobName(String jobId) throws Exception {

		return flinkClient
				   .listJobs()
				   .get()
				   .stream()
				   .filter(i -> i.getJobId().toString().equals(jobId))
				   .map(JobStatusMessage::getJobName)
				   .map(n -> n.replaceFirst("AlertJob_", ""))
				   .findFirst()
				   .orElse(null);
	}

	// Public for temporary testing purposes only
	public File getFlinkJobJar() {
		return jarFile;
	}

	private File getJarFile(String resourceName) {
		ClassLoader classLoader = getClass().getClassLoader();
		return new File(classLoader.getResource(resourceName).getFile());
	}

}