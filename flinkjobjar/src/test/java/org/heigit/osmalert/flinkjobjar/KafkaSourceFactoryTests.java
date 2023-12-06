package org.heigit.osmalert.flinkjobjar;

import org.apache.flink.connector.kafka.source.*;
import org.junit.jupiter.api.*;
import org.junitpioneer.jupiter.*;

import static org.assertj.core.api.Assertions.*;
import static org.heigit.osmalert.flinkjobjar.KafkaSourceFactory.*;

public class KafkaSourceFactoryTests {

	@Nested
	@SetEnvironmentVariable(value = "localhost", key = "KAFKA_USER")
	@SetEnvironmentVariable(value = "2025", key = "KAFKA_PASSWORD")
	@SetEnvironmentVariable(value = "adsf", key = "KAFKA_TOPIC")
	@SetEnvironmentVariable(value = "asd", key = "KAFKA_BROKER")
	class EnvironmentalVariables {
		@Test
		void getKafkaSourceTest() {
			KafkaSource<String> testobject = getKafkaSource();
			assertThat(testobject).isNotNull();
		}
	}

	@Test
	void getKafkaSourceWithoutParameterTest() {
		try {
			KafkaSource<String> testobject = getKafkaSource();
			assertThat(testobject).isNull();
		} catch (Exception e) {
			assertThat(e).isExactlyInstanceOf(NullPointerException.class);
		}
	}

	@Test
	void saslJaasConfigTest() {
		String username = "user";
		String password = "password";
		assertThat(saslJaasConfig(username, password)).isEqualTo("org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" +
																	 username +
																	 "\" password=\"" +
																	 password +
																	 "\";");
	}
}