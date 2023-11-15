package org.heigit.osmalert.flinkjobjar;

public class MessageFilter {
	private int importantMessages;
	private final BoundingBox boundingBox;
	private final MailSinkFunction mailSinkFunction;

	private MessageFilter(BoundingBox boundingBox, MailSinkFunction mailSinkFunction) {
		this.importantMessages = 0;
		this.boundingBox = boundingBox;
		this.mailSinkFunction = mailSinkFunction;
	}

}