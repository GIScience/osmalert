package org.heigit.osmalert.flinkjobjar;

import org.apache.flink.streaming.api.datastream.*;
import org.apache.flink.streaming.api.environment.*;
import org.apache.flink.streaming.api.windowing.assigners.*;

import static org.apache.flink.api.common.eventtime.WatermarkStrategy.*;
import static org.apache.flink.streaming.api.windowing.time.Time.*;
import static org.heigit.osmalert.flinkjobjar.KafkaSourceFactory.*;


public class AlertJob {


	public static void main(String[] args) throws Exception {

		String sourceName = "osmalert_flink_kafka_source";

		StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
		SingleOutputStreamOperator<String> streamOperator = environment
																.fromSource(getKafkaSource(), noWatermarks(), sourceName)
																.uid(sourceName)
																.name(sourceName);

		String jobName = getJobName(args);
		configureAndRunJob(jobName, streamOperator, environment);
	}



	static void configureAndRunJob(String jobName, SingleOutputStreamOperator<String> streamOperator,
								   StreamExecutionEnvironment environment) throws Exception {


		String sinkName = "osmalert_flink_mail_sink";

		streamOperator
			.map(AlertJob::log)
			.map(String::length)

			.windowAll(TumblingProcessingTimeWindows.of(seconds(5)))
			// .windowAll(SlidingProcessingTimeWindows.of(seconds(5), seconds(2)))
			.reduce(Integer::sum)
			.map( i -> {System.out.println("reduced sum: " + i); return i;})

			.addSink(new MailSinkFunction())
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



}
