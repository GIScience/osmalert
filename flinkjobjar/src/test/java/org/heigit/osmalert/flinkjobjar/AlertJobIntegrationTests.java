package org.heigit.osmalert.flinkjobjar;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import name.bychkov.junit5.*;
import org.apache.flink.api.common.typeinfo.*;
import org.apache.flink.configuration.*;
import org.apache.flink.runtime.testutils.*;
import org.apache.flink.streaming.api.datastream.*;
import org.apache.flink.streaming.api.environment.*;
import org.apache.flink.streaming.api.functions.sink.*;
import org.apache.flink.test.junit5.*;
import org.heigit.osmalert.flinkjobjar.model.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.locationtech.jts.geom.*;

import static org.assertj.core.api.Assertions.*;
import static org.heigit.osmalert.flinkjobjar.AlertJob.*;

class AlertJobIntegrationTests {
	private AlertJobIntegrationTests() {}

	static final Configuration configuration = new Configuration();

	@BeforeAll
	static void setConfiguration() {
		configuration.setString("MAILERTOGO_SMTP_HOST", "localhost");
		configuration.setString("MAILERTOGO_SMTP_PORT", "2025");
		configuration.setString("MAILERTOGO_SMTP_USER", "whatever");
		configuration.setString("MAILERTOGO_SMTP_PASSWORD", "whatever");
	}

	@RegisterExtension
	static MiniClusterExtension miniClusterExtension = new MiniClusterExtension(
		new MiniClusterResourceConfiguration.Builder()
			.setNumberSlotsPerTaskManager(2)
			.setNumberTaskManagers(1)
			.build()
	);

	@RegisterExtension
	static final FakeSmtpJUnitExtension fakeMailServer = new FakeSmtpJUnitExtension()
															 .port(2025);

	static final String contribution;
	static final Geometry boundingBox = new GeometryFactory().toGeometry(new Envelope(15.0, 17.0, 1.0, 2.0));

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
	void flinkJobCanBeRunAndMailIsSent() throws Exception {
		StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment(configuration);
		Iterator<String> iterator = new SlowStringIterator();
		DataStreamSource<String> operator = environment.fromCollection(iterator, TypeInformation.of(String.class));
		int timewindow = 1;
		Map<String, String> map = configuration.toMap();
		MailSinkFunction mailSink = new MailSinkFunction(
			map.get("MAILERTOGO_SMTP_HOST"),
			Integer.parseInt(map.get("MAILERTOGO_SMTP_PORT")),
			map.get("MAILERTOGO_SMTP_USER"),
			map.get("MAILERTOGO_SMTP_PASSWORD"),
			"user@example.org",
			"-90,-180,90,180",
			timewindow
		);
		configureAndRunJob("job1", operator, environment, timewindow, mailSink, boundingBox);

		assertThat(fakeMailServer.getMessages().size())
			.isGreaterThan(0);
	}

	@Test
	void flinkStreamCountIsRight() throws Exception {
		StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment(configuration);
		Iterator<String> iterator = new SlowStringIterator();
		DataStreamSource<String> operator = environment.fromCollection(iterator, TypeInformation.of(String.class));

		MockSink mockSink = new MockSink();

		configureAndRunJob("job1", operator, environment, 3, mockSink, boundingBox);

		for (Integer value : MockSink.values) {
			boolean match = (value <= 3);
			Assertions.assertTrue(match);
		}
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
			String ret;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			if (count == 0) {
				try {
					ret = Files.readString(Paths.get("src/test/resources/contribution1.json"));
					count++;
				} catch (IOException e) {
					ret = "File not readable";
				}
			} else
				ret = contribution + count++;
			System.out.println("NEXT called()");
			return ret;
		}
	}

}