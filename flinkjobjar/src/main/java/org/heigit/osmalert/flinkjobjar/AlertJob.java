package org.heigit.osmalert.flinkjobjar;

import org.apache.flink.streaming.api.datastream.*;
import org.apache.flink.streaming.api.environment.*;
import org.apache.flink.streaming.api.windowing.assigners.*;

import static org.apache.flink.api.common.eventtime.WatermarkStrategy.*;
import static org.apache.flink.streaming.api.windowing.time.Time.*;
import static org.heigit.osmalert.flinkjobjar.KafkaSourceFactory.*;

public class AlertJob {

	static String host = System.getenv("MAILERTOGO_SMTP_HOST").toString();
	static int port = Integer.parseInt(System.getenv("MAILERTOGO_SMTP_PORT"));
	static String username = System.getenv("MAILERTOGO_SMTP_USER").toString();
	static String password = System.getenv("MAILERTOGO_SMTP_PASSWORD").toString();

	public static void main(String[] args) throws Exception {

		String sourceName = "osmalert_flink_kafka_source";

		StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
		SingleOutputStreamOperator<String> streamOperator = environment
																.fromSource(getKafkaSource(), noWatermarks(), sourceName)
																.uid(sourceName)
																.name(sourceName);

		String jobName = getJobName(args);
		String emailAddress = getEmailAddress(args);
		MailSinkFunction mailSink = new MailSinkFunction(host, port, username, password, emailAddress);
		configureAndRunJob(jobName, streamOperator, environment, 60, mailSink);
	}

	static void configureAndRunJob(
		String jobName, SingleOutputStreamOperator<String> streamOperator,
		StreamExecutionEnvironment environment, int windowSeconds, MailSinkFunction mailSink
	) throws Exception {


		String sinkName = "osmalert_flink_mail_sink";

		streamOperator
			.map(AlertJob::log)
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

	private static String getJobName(String[] args) {
		assert args[0] != null;
		String jobName = "AlertJob_" + args[0];
		System.out.println("=== " + jobName + " ===");
		return jobName;
	}

	private static String getEmailAddress(String[] args) {
		assert args[1] != null;
		String emailAdress = args[1];
		System.out.println("=== " + emailAdress + " ===");
		return emailAdress;
	}

}
