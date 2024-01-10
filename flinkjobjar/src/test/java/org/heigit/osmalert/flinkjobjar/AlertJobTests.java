package org.heigit.osmalert.flinkjobjar;

import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;
import static org.heigit.osmalert.flinkjobjar.AlertJob.*;

class AlertJobTests {

	private final JobParams jobParams = new JobParams(new String[]{"jobname", "email@email.de", "1.0,2.0,3.0,4.0", "80"});

	@Test
	void getJobNameFromParams() {
		assertThat(jobParams.getJobName()).isEqualTo("AlertJob_jobname");
	}

	@Test
	void getEmailAddressTest() {
		assertThat(jobParams.getEmailAddress()).isEqualTo("email@email.de");
	}

	@Test
	void getBoundingBoxString() {
		assertThat(jobParams.getBoundingBoxString()).isEqualTo("1.0,2.0,3.0,4.0");
	}

	@Test
	void getBoundingBoxValuesTest() {
		String[] input = {"1.0", "2.0", "3.0", "4.0"};
		double[] result = getBoundingBoxValues(input);
		assertThat(result[0]).isEqualTo(1.0);
		assertThat(result[1]).isEqualTo(2.0);
		assertThat(result[2]).isEqualTo(3.0);
		assertThat(result[3]).isEqualTo(4.0);
	}

	@Test
	void getBoundingBoxStringArrayTest() {
		String[] result = getBoundingBoxStringArray("1.0,2.0,3.0,4.0");
		assertThat(result[0]).isEqualTo("1.0");
		assertThat(result[1]).isEqualTo("2.0");
		assertThat(result[2]).isEqualTo("3.0");
		assertThat(result[3]).isEqualTo("4.0");
	}

	@Test
	void getTimeWindowTest() {
		assertThat(jobParams.getTimeWindowInMinutes()).isEqualTo(80);
	}

}
