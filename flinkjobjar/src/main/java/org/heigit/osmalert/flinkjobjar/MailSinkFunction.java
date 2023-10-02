package org.heigit.osmalert.flinkjobjar;

import org.apache.flink.streaming.api.functions.sink.*;



public class MailSinkFunction implements SinkFunction<Integer> {


	@Override
	public void invoke(Integer value, Context context) {

		System.out.println("##### MailSink input: " + value);

		this.sendMail("total message length for last 10 seconds: " + value);
	}


	private MailSender getMailSender() {

		String host = System.getenv("MAILERTOGO_SMTP_HOST");
		int port = Integer.parseInt(System.getenv("MAILERTOGO_SMTP_PORT"));
		String username = System.getenv("MAILERTOGO_SMTP_USER");
		String password = System.getenv("MAILERTOGO_SMTP_PASSWORD");

		System.out.println("host = " + host);
		System.out.println("port = " + port);

		return new MailSender(host, port, username, password);
	}


	private  void sendMail(String payload) {
		MailSender mailSender = getMailSender();
		mailSender.sendMail("osmalert@web.de", payload);
		System.out.println("=== MAIL SENT! ===");
	}




}
