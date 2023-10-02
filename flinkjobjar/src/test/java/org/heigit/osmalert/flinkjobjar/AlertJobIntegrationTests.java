package org.heigit.osmalert.flinkjobjar;

import java.io.*;
import java.util.*;

import name.bychkov.junit5.*;
import org.apache.flink.api.common.typeinfo.*;
import org.apache.flink.runtime.testutils.*;
import org.apache.flink.streaming.api.datastream.*;
import org.apache.flink.streaming.api.environment.*;
import org.apache.flink.test.junit5.*;
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


	static String contribution = "{\n" +
					 "  \"replicationSequence\": 1,\n" +
					 "  \"id\": \"changeset-toplevel\",\n";



	// @Disabled("check on github")
	@Test
	@SetEnvironmentVariable(key = "MAILERTOGO_SMTP_HOST", value = "localhost")
	@SetEnvironmentVariable(key = "MAILERTOGO_SMTP_PORT", value = "2025")
	@SetEnvironmentVariable(key = "MAILERTOGO_SMTP_USER", value = "whatever")
	@SetEnvironmentVariable(key = "MAILERTOGO_SMTP_PASSWORD", value = "whatever")
	void flinkJobCanBeRunAndMailIsSent() throws Exception {

		StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
		Iterator<String> iterator = new SlowStringIterator();
		DataStreamSource<String> operator = environment.fromCollection(iterator, TypeInformation.of(String.class));

		configureAndRunJob("job1", operator, environment);

		assertThat(fakeMailServer.getMessages().size())
			.isGreaterThan(0);

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