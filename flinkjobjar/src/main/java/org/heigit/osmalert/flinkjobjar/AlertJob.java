package org.heigit.osmalert.flinkjobjar;

import org.apache.flink.streaming.api.datastream.*;
import org.apache.flink.streaming.api.environment.*;
import org.apache.flink.streaming.api.functions.sink.*;
import org.apache.flink.streaming.api.windowing.assigners.*;
import org.heigit.osmalert.flinkjobjar.model.*;
import org.locationtech.jts.geom.*;

import static org.apache.flink.api.common.eventtime.WatermarkStrategy.*;
import static org.apache.flink.streaming.api.windowing.time.Time.*;
import static org.heigit.osmalert.flinkjobjar.KafkaSourceFactory.*;

public class AlertJob {

	private AlertJob() {}

	public static void main(String[] args) throws Exception {

		String sourceName = "osmalert_flink_kafka_source";

		StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();

		SingleOutputStreamOperator<String> streamOperator = environment
																.fromSource(getKafkaSource(), noWatermarks(), sourceName)
																.uid(sourceName)
																.name(sourceName);

		JobConfiguration jobConfiguration = new JobConfiguration((args));


		/*
		 * For Polygon use this
		 * new WKTReader().read(args[2]);
		 */
		Geometry boundingBox = new GeometryFactory().toGeometry(
			new Envelope(
				jobConfiguration.getBoundingBoxValues(0),
				jobConfiguration.getBoundingBoxValues(2),
				jobConfiguration.getBoundingBoxValues(1),
				jobConfiguration.getBoundingBoxValues(3)
			));
		MailSinkFunction mailSink = new MailSinkFunction(
			System.getenv("MAILERTOGO_SMTP_HOST"),
			Integer.parseInt(System.getenv("MAILERTOGO_SMTP_PORT")),
			System.getenv("MAILERTOGO_SMTP_USER"),
			System.getenv("MAILERTOGO_SMTP_PASSWORD"),
			jobConfiguration.getEmailAddress(),
			jobConfiguration.getBoundingBoxString(),
			jobConfiguration.getTimeWindowInMinutes()
		);
		configureAndRunJob(jobConfiguration.getJobName(), streamOperator, environment, jobConfiguration.getTimeWindowInSeconds(), mailSink, boundingBox);
	}

	static void configureAndRunJob(
		String jobName, SingleOutputStreamOperator<String> streamOperator,
		StreamExecutionEnvironment environment, int windowSeconds, SinkFunction<Integer> mailSink, Geometry boundingBox
	) throws Exception {

		String sinkName = "osmalert_flink_mail_sink";

		streamOperator
			.map(AlertJob::log)
			.map(Contribution::createContribution)
			.filter(contrib -> contrib.isWithin(boundingBox))
			.map(log -> 1)
			.windowAll(TumblingProcessingTimeWindows.of(seconds(windowSeconds)))
			.reduce(Integer::sum)
			.addSink(mailSink)
			.uid(sinkName)
			.name(sinkName);

		environment.execute(jobName);
	}

	private static String log(String contribution) {
		System.out.println("contribution = " + contribution);
		return contribution;
	}
}