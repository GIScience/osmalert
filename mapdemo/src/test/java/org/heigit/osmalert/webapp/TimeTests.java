package org.heigit.osmalert.webapp;

import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

public class TimeTests {

	static Time hour;
	static Time Minute;
	static Time time;

	@BeforeAll
	public static void createObjects() {
		hour = Time.valueOf("H");
		Minute = Time.valueOf("M");
	}

	@Test
	void HourTest() {
		assertThat(hour.calculateMinutes(3)).isEqualTo(180);
	}

	@Test
	void MinuteTest() {
		assertThat(Minute.calculateMinutes(3)).isEqualTo(3);
	}
}