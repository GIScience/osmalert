package org.heigit.osmalert.flinkjobjar;

import java.io.*;
import java.util.*;

import name.bychkov.junit5.*;
import org.apache.flink.api.common.typeinfo.*;
import org.apache.flink.runtime.testutils.*;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.*;
import org.apache.flink.streaming.api.datastream.*;
import org.apache.flink.streaming.api.environment.*;
import org.apache.flink.streaming.api.functions.sink.*;
import org.apache.flink.test.junit5.*;
import org.heigit.osmalert.flinkjobjar.model.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.junitpioneer.jupiter.*;

import static org.assertj.core.api.Assertions.*;
import static org.heigit.osmalert.flinkjobjar.AlertJob.*;

class AlertJobIntegrationTests {

	@RegisterExtension
	static MiniClusterExtension miniClusterExtension = new MiniClusterExtension(
		new MiniClusterResourceConfiguration.Builder()
			.setNumberSlotsPerTaskManager(2)
			.setNumberTaskManagers(1)
			.build()
	);

	@RegisterExtension
	static FakeSmtpJUnitExtension fakeMailServer = new FakeSmtpJUnitExtension()
													   .port(2025);

	static String contribution;
	static BoundingBox boundingBox = new BoundingBox(1, 2, 3, 4);

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
	@SetEnvironmentVariable(key = "MAILERTOGO_SMTP_HOST", value = "localhost")
	@SetEnvironmentVariable(key = "MAILERTOGO_SMTP_PORT", value = "2025")
	@SetEnvironmentVariable(key = "MAILERTOGO_SMTP_USER", value = "whatever")
	@SetEnvironmentVariable(key = "MAILERTOGO_SMTP_PASSWORD", value = "whatever")
	void flinkJobCanBeRunAndMailIsSent() throws Exception {

		//TODO: remove dependency of test on env variables and fields of AlertJob

		StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
		Iterator<String> iterator = new SlowStringIterator();
		DataStreamSource<String> operator = environment.fromCollection(iterator, TypeInformation.of(String.class));

		MailSinkFunction mailSink = new MailSinkFunction(host, port, username, password, "user@example.org");
		configureAndRunJob("job1", operator, environment, 3, mailSink, boundingBox);

		assertThat(fakeMailServer.getMessages().size())
			.isGreaterThan(0);
	}

	@Test
	@SetEnvironmentVariable(key = "MAILERTOGO_SMTP_HOST", value = "localhost")
	@SetEnvironmentVariable(key = "MAILERTOGO_SMTP_PORT", value = "2025")
	@SetEnvironmentVariable(key = "MAILERTOGO_SMTP_USER", value = "whatever")
	@SetEnvironmentVariable(key = "MAILERTOGO_SMTP_PASSWORD", value = "whatever")
	void flinkStreamCountIsRight() throws Exception {

		//TODO: remove dependency of test on env variables and fields of AlertJob

		StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
		Iterator<String> iterator = new SlowStringIterator();
		DataStreamSource<String> operator = environment.fromCollection(iterator, TypeInformation.of(String.class));

		MockSink mockSink = new MockSink();

		configureAndRunJob("job1", operator, environment, 3, mockSink, boundingBox);

		for (Integer value : MockSink.values) {
			boolean match = (value <= 3);
			Assertions.assertTrue(match);
		}
	}

	@Test
	void isContributionNotNull() throws JsonProcessingException {
		Contribution contributionObj = Contribution.createContribution(contribution);
		assertThat(contributionObj).isNotNull();
	}

	private static class MockSink implements SinkFunction<Integer> {
		public static final List<Integer> values = new ArrayList<>();

		@Override
		public void invoke(Integer value, Context context) {
			System.out.println("Mock sink stream value: " + value);
			values.add(value);
		}

	}

	static class SlowStringIterator implements Iterator<String>, Serializable {

		int count = 0;

		@Override
		public boolean hasNext() {
			return count < 6;
		}

		@Override
		public String next() {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			count++;
			System.out.println("NEXT called()");
			return contribution + count;
		}
	}

}