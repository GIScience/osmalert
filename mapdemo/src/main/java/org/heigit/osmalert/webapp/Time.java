package org.heigit.osmalert.webapp;

public enum Time {
	// D("Day") {
	// 	@Override
	// 	public int calculateMinutes(int time) {
	// 		return time * 24 * 60;
	// 	}
	// },
	H("Hour") {
		@Override
		public int calculateMinutes(int time) {
			return time * 60;
		}
	},
	M("Minute") {
		@Override
		public int calculateMinutes(int time) {
			return time;
		}
	};

	Time(String i) {

	}

	public int calculateMinutes(int amount) {return 0;}
}