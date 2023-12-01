package org.heigit.osmalert.flinkservice;

import org.apache.flink.api.common.*;
import org.apache.flink.client.program.*;
import org.apache.flink.configuration.*;
import org.apache.flink.runtime.jobgraph.*;
import org.apache.flink.runtime.minicluster.*;
import org.apache.flink.runtime.testutils.*;
import org.apache.flink.test.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.junitpioneer.jupiter.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class FlinkClusterServiceTests {

	@RegisterExtension
	static final MiniClusterExtension miniClusterExtension = new MiniClusterExtension(
		new MiniClusterResourceConfiguration.Builder()
			.setNumberSlotsPerTaskManager(2)
			.setNumberTaskManagers(1)
			.build()
	);

	final String jobName = "job_23";
	final String emailAddress = "user@example.org";
	final String boundingBox = "1.0,2.0,3.0,4.0";

	static Configuration config2 = new Configuration();

	@BeforeAll
	static void setConfig2() {
		config2.setString("MAILERTOGO_SMTP_HOST", "localhost");
		config2.setString("MAILERTOGO_SMTP_PORT", "2025");
		config2.setString("MAILERTOGO_SMTP_USER", "whatever");
		config2.setString("MAILERTOGO_SMTP_PASSWORD", "whatever");
		config2.setString("KAFKA_USER", "whatever");
		config2.setString("KAFKA_PASSWORD", "whatever");
		config2.setString("KAFKA_TOPIC", "whatever");
		config2.setString("KAFKA_BROKER", "whatever");
	}

	@Test
	@SetEnvironmentVariable(key = "KAFKA_USER", value = "whatever")
	@SetEnvironmentVariable(key = "KAFKA_PASSWORD", value = "whatever")
	@SetEnvironmentVariable(key = "KAFKA_TOPIC", value = "whatever")
	@SetEnvironmentVariable(key = "KAFKA_BROKER", value = "whatever")

	@SetEnvironmentVariable(key = "MAILERTOGO_SMTP_HOST", value = "whatever")
	@SetEnvironmentVariable(key = "MAILERTOGO_SMTP_PORT", value = "123")
	@SetEnvironmentVariable(key = "MAILERTOGO_SMTP_USER", value = "whatever")
	@SetEnvironmentVariable(key = "MAILERTOGO_SMTP_PASSWORD", value = "whatever")
	void aJobGraphCanBeCreated() throws Exception {

		// dummy config - never used
		FlinkRestsConfiguration config = new FlinkRestsConfiguration("", -1, 0);
		FlinkClusterService clusterService = new FlinkClusterService(config);

		JobGraph jobGraph = clusterService.createJobGraph("name", "emailAddress", boundingBox);
		assertThat(jobGraph).isNotNull();

	}

	@Test
	@SetEnvironmentVariable(key = "KAFKA_USER", value = "whatever")
	@SetEnvironmentVariable(key = "KAFKA_PASSWORD", value = "whatever")
	@SetEnvironmentVariable(key = "KAFKA_TOPIC", value = "whatever")
	@SetEnvironmentVariable(key = "KAFKA_BROKER", value = "whatever")

	@SetEnvironmentVariable(key = "MAILERTOGO_SMTP_HOST", value = "localhost")
	@SetEnvironmentVariable(key = "MAILERTOGO_SMTP_PORT", value = "25")
	@SetEnvironmentVariable(key = "MAILERTOGO_SMTP_USER", value = "whatever")
	@SetEnvironmentVariable(key = "MAILERTOGO_SMTP_PASSWORD", value = "whatever")
	void testClusterSubmission(@InjectMiniCluster MiniCluster miniCluster) throws Exception {

		MiniClusterClient clusterClient = getMiniClusterClient(miniCluster);
		FlinkClusterService clusterService = new FlinkClusterService(clusterClient);

		String jobId = clusterService.submitJarJobToCluster(jobName, emailAddress, boundingBox);
		System.out.println("jobId = " + jobId);

		assertEquals(32, jobId.length());
		assertTrue(clusterService.isNotFailed(jobId));
		assertEquals(jobName, clusterService.getJobName(jobId));

		//TODO: check if flaky

		assertEquals(JobStatus.INITIALIZING, clusterService.getStatus(jobId));
	}

	private static MiniClusterClient getMiniClusterClient(MiniCluster miniCluster) {
		Configuration config = miniClusterExtension.getClientConfiguration();
		return new MiniClusterClient(config, miniCluster);
	}

	@Disabled("only for local usage against a local flink cluster at 8081")
	@Test
	/*@SetEnvironmentVariable(key = "MAILERTOGO_SMTP_HOST", value = "localhost")
	@SetEnvironmentVariable(key = "MAILERTOGO_SMTP_PORT", value = "25")
	@SetEnvironmentVariable(key = "MAILERTOGO_SMTP_USER", value = "whatever")
	@SetEnvironmentVariable(key = "MAILERTOGO_SMTP_PASSWORD", value = "whatever")*/
	void submitSomeJobs() throws Exception {

		FlinkClusterService clusterService = new FlinkClusterService();

		submitJobAndCheck("finally works!!", "valid email address!!", boundingBox, clusterService);

	}

	@Disabled("only for experimentally 'deploying' to heroku")
	@Test
	void manuallyDeployToHeroku() throws Exception {


		FlinkRestsConfiguration restConfiguration = new FlinkRestsConfiguration(
			"osmalert-flink-docker-d0c317ac495f.herokuapp.com",
			80,
			2
		);

		FlinkClusterService clusterService = new FlinkClusterService(restConfiguration);

		clusterService.submitJarJobToCluster("try_job_with_mail", "user@example.org", boundingBox);

	}

	// Test can be deleted when other tests work
	@Test
	void serviceCanBeCreatedAndJobJarAccessed() throws Exception {
		FlinkRestsConfiguration restConfiguration = new FlinkRestsConfiguration(
			"osmalert-flink-docker-d0c317ac495f.herokuapp.com",
			80,
			2
		);

		FlinkClusterService clusterService = new FlinkClusterService(restConfiguration);
		assertTrue(clusterService.getFlinkJobJar().exists());
	}

	private static void submitJobAndCheck(String name, String emailAddress, String boundingBox, FlinkClusterService clusterService) {
		try {
			String id = clusterService.submitJarJobToCluster(name, emailAddress, boundingBox);
			System.out.println("jobId = " + id);
			assertTrue(clusterService.isNotFailed(id));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}