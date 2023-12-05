package org.heigit.osmalert.flinkjobjar;

import org.apache.flink.api.common.serialization.*;
import org.apache.flink.connector.kafka.source.*;
import org.apache.flink.connector.kafka.source.enumerator.initializer.*;

import static java.lang.System.*;

public class KafkaSourceFactory {

	private KafkaSourceFactory() {}

	static KafkaSource<String> getKafkaSource() {

		String user = getenv("KAFKA_USER");
		String password = getenv("KAFKA_PASSWORD");
		String topic = getenv("KAFKA_TOPIC");
		String broker = getenv("KAFKA_BROKER");

		String group = "int.osmalert.flinkjob.exp1";

		return getKafkaContributionSource(broker, topic, user, password, group);
	}

	static KafkaSource<String> getKafkaContributionSource(
		String bootstrapServer, String topic,
		String user, String password, String groupID
	) {


		return KafkaSource
				   .<String>builder()
				   .setBootstrapServers(bootstrapServer)
				   .setTopics(topic)
				   .setStartingOffsets(OffsetsInitializer.latest())
				   .setValueOnlyDeserializer(new SimpleStringSchema())

				   .setProperty("group.id", groupID)
				   .setProperty("security.protocol", "SASL_SSL")
				   .setProperty("sasl.mechanism", "PLAIN")
				   .setProperty("sasl.jaas.config", saslJaasConfig(user, password))

				   //commit offset periodically to enable progress insights on broker
				   .setProperty("enable.auto.commit", "true")
				   .build();

	}

	static String saslJaasConfig(String user, String password) {
		return "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" +
				   user +
				   "\" password=\"" +
				   password +
				   "\";";
	}

}