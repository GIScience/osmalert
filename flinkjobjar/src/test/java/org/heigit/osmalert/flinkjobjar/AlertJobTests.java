package org.heigit.osmalert.flinkjobjar;

import java.io.*;

import org.heigit.osmalert.flinkjobjar.model.*;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;
import static org.heigit.osmalert.flinkjobjar.AlertJob.*;

class AlertJobTests {
	static final String contribution;

	static {
		try (BufferedReader reader = new BufferedReader(
			new FileReader("src/test/resources/contribution1.json"))) {
			StringBuilder stringBuilder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
			}
			contribution = stringBuilder.toString();
		} catch (IOException e) {
			throw new RuntimeException("Error reading file", e);
		}
	}

	@Test
	void isContributionNotNull() {
		Contribution contributionObj = Contribution.createContribution(contribution);
		assertThat(contributionObj).isNotNull();
	}

	@Test
	void getJobNameTest() {
		String[] input = {"jobname", "email", "123.123"};
		String[] emptyJobName = {null, "email@email.de"};
		assertThat(getJobName(input)).isEqualTo("AlertJob_jobname");
		try {
			getJobName(emptyJobName);
		} catch (AssertionError e) {
			assertThat(e).isExactlyInstanceOf(AssertionError.class);
		}
	}

	@Test
	void getEmailAdressTest() {
		String[] input = {"name", "email@email.de"};
		String[] emptyEmailAddress = {"name", null};
		assertThat(getEmailAddress(input)).isEqualTo(input[1]);
		try {
			getEmailAddress(emptyEmailAddress);
		} catch (AssertionError e) {
			assertThat(e).isExactlyInstanceOf(AssertionError.class);
		}
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
		try {
			String[] ret = getBoundingBoxStringArray(null);
			assertThat(ret[0]).isNull();
		} catch (AssertionError e) {
			assertThat(e).isExactlyInstanceOf(AssertionError.class);
		}
	}

	@Test
	void getTimeWindowTest() {
		String time = "60";
		assertThat(getTimeWindow(time)).isEqualTo(60);
	}

}
