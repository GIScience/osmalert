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

		JobParams jobParams = new JobParams(args);

		String jobName = jobParams.getJobName();
		String emailAddress = jobParams.getEmailAddress();
		int timeMinutes = jobParams.getTimeWindow();
		String boundingBoxString = jobParams.getBoundingBoxString();

		double[] boundingBoxValues = getBoundingBoxValues(getBoundingBoxStringArray(boundingBoxString));

		/*
		 * For Polygon use this
		 * new WKTReader().read(args[2]);
		 */
		Geometry boundingBox = new GeometryFactory().toGeometry(new Envelope(boundingBoxValues[0], boundingBoxValues[2], boundingBoxValues[1], boundingBoxValues[3]));

		MailSinkFunction mailSink = new MailSinkFunction(
			System.getenv("MAILERTOGO_SMTP_HOST"),
			Integer.parseInt(System.getenv("MAILERTOGO_SMTP_PORT")),
			System.getenv("MAILERTOGO_SMTP_USER"),
			System.getenv("MAILERTOGO_SMTP_PASSWORD"),
			emailAddress,
			boundingBoxString,
			timeMinutes
		);
		configureAndRunJob(streamOperator, environment, mailSink, jobName, timeMinutes * 60, boundingBox);
	}

	static void configureAndRunJob(
		SingleOutputStreamOperator<String> streamOperator, StreamExecutionEnvironment environment, SinkFunction<Integer> mailSink,
		String jobName, int windowSeconds, Geometry boundingBox
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

	public static double[] getBoundingBoxValues(String[] args) {
		double[] doubleArray = new double[4];
		for (int i = 0; i < 4; i++)
			doubleArray[i] = Double.parseDouble(args[i]);
		return doubleArray;
	}

	public static String[] getBoundingBoxStringArray(String boundingBoxString) {
		assert boundingBoxString != null;
		return boundingBoxString.split(",");
	}

}