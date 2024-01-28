package org.heigit.osmalert.flinkjobjar;

import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

public class JobConfigurationTests {
	static final String[] params = {"jobName", "email", "4.279,48.829,16.419,53.495", "5", "leisure=park"};
	static JobConfiguration jobConfiguration;

	@BeforeAll
	public static void initTests() {
		jobConfiguration = new JobConfiguration(params);
	}

	@Test
	void getJobNameTest() {
		assertThat(jobConfiguration.getJobName()).isEqualTo("AlertJob_" + params[0]);
	}

	@Test
	void getEmailAddressTest() {
		assertThat(jobConfiguration.getEmailAddress()).isEqualTo(params[1]);
	}

	@Test
	void getBoundingBoxValuesTest() {
		assertThat(jobConfiguration.getBoundingBoxValues(0)).isEqualTo(4.279);
		assertThat(jobConfiguration.getBoundingBoxValues(1)).isEqualTo(48.829);
		assertThat(jobConfiguration.getBoundingBoxValues(2)).isEqualTo(16.419);
		assertThat(jobConfiguration.getBoundingBoxValues(3)).isEqualTo(53.495);
	}

	@Test
	void getBoundingBoxStringTest() {
		assertThat(jobConfiguration.getBoundingBoxString()).isEqualTo(params[2]);
	}

	@Test
	void getTimeWindowTest() {
		assertThat(jobConfiguration.getTimeWindowInMinutes()).isEqualTo(Integer.parseInt(params[3]));
	}

	@Test
	void getBoundingBoxTest() {
		double[] boundingBox = {4.279, 48.829, 16.419, 53.495};
		assertThat(jobConfiguration.getBoundingBox()).isEqualTo(boundingBox);
	}

	@Test
	void getTimeWindowInSecondsTest() {
		assertThat(jobConfiguration.getTimeWindowInSeconds()).isEqualTo(Integer.parseInt(params[3]) * 60);
	}

	@Test
	void getPatternTest() { assertThat(jobConfiguration.getPattern()).isEqualTo(params[4]); }

}