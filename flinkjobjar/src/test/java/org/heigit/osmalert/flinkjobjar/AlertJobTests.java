package org.heigit.osmalert.flinkjobjar;

import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;
import static org.heigit.osmalert.flinkjobjar.AlertJob.*;

class AlertJobTests {

	@Test
	void getJobNameTest() {
		String[] input = {"jobname", "email", "123.123", "80"};
		String[] emptyJobName = {null, "email@email.de"};
		assertThat(getJobName(input)).isEqualTo("AlertJob_jobname");
	}

	@Test
	void getEmailAddressTest() {
		String[] input = {"name", "email@email.de", "1.0,2.0,3.0,4.0", "79"};
		String[] emptyEmailAddress = {"name", null};
		assertThat(getEmailAddress(input)).isEqualTo(input[1]);
	}

	@Test
	void getBoundingBoxString() {
		String[] input = {"name", "email", "1.0,2.0,3.0,4.0", "79"};
		assertThat(AlertJob.getBoundingBoxString(input)).isEqualTo("1.0,2.0,3.0,4.0");
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
	void getTimeWindowTest_NEW() {
		String[] args = {"email", "name", "1,2,3,4", "60"};
		assertThat(getTimeWindow(args)).isEqualTo(60);
	}

}
