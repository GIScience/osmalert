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
import org.mockito.*;

import static org.assertj.core.api.Assertions.*;
import static org.heigit.osmalert.flinkjobjar.AlertJob.*;

class AlertJobIntegrationTests {

	static final Configuration configuration = new Configuration();

	@Mock
	private StatisticalAnalyzer mockStatisticalAnalyzer;

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
		MockitoAnnotations.openMocks(this);
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
			"15.0,17.0,1.0,2.0",
			timewindow,
			"highway=track",
			"Test job");

		MailSinkFunction.setStatisticalAnalyzer(mockStatisticalAnalyzer);

		configureAndRunJob("job1", operator, environment, timewindow, mailSink, boundingBox, "highway=track");

		assertThat(fakeMailServer.getMessages().size())
			.isGreaterThan(0);
	}


	@RepeatedTest(2)
	void flinkStreamCountIsRight() throws Exception {
		StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment(configuration);
		Iterator<String> iterator = new SlowStringIterator();
		DataStreamSource<String> operator = environment.fromCollection(iterator, TypeInformation.of(String.class));

		MockSink mockSink = new MockSink();
		MockSink.values.clear();

		configureAndRunJob("job1", operator, environment, 3, mockSink, boundingBox, "highway=track");

		assertThat(MockSink.values).allMatch(result -> (result.count <= 4) && (result.count >= 1));
		assertThat(MockSink.values).allMatch(result -> (result.uniqueUsers == 1));
		assertThat(MockSink.values).hasSizeBetween(1, 2);
	}


	@Test
	void isContributionNotNull() {
		Contribution contributionObj = Contribution.createContribution(contribution);
		assertThat(contributionObj).isNotNull();
	}

	private static class MockSink implements SinkFunction<StatsResult> {
		public static final List<StatsResult> values = new ArrayList<>();

		@Override
		public void invoke(StatsResult value, Context context) {
			System.out.println("Mock sink stream value: " + value);
			values.add(value);
		}

	}

	static class SlowStringIterator implements Iterator<String>, Serializable {

		int count = 0;

		@Override
		public boolean hasNext() {
			return count < 7;
		}

		@Override
		public String next() {
			String file;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			if (count == 0) {
				try {
					file = Files.readString(Paths.get("src/test/resources/contribution1.json"));
					count++;
				} catch (IOException e) {
					file = "File not readable";
				}
			} else
				file = contribution + count++;
			System.out.println("NEXT called()");
			return file;
		}
	}

}