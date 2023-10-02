package org.heigit.osmalert.flinkservice;

public record FlinkRestsConfiguration(
	String address,
	int port,
	int retryMaxAttempts
) {
}
