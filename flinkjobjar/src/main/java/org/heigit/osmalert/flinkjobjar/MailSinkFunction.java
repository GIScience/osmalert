package org.heigit.osmalert.flinkjobjar;

import org.apache.flink.streaming.api.functions.sink.*;



public class MailSinkFunction implements SinkFunction<Integer> {

	private final String host;
	private final int port;
	private final String username;
	private final String password;


	public MailSinkFunction(String host, int port, String username, String password) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
	}


	@Override
	public void invoke(Integer value, Context context) {

		System.out.println("##### MailSink input: " + value);

		this.sendMail("total message length for last 60 seconds: " + value);
	}


	private MailSender getMailSender() {

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
