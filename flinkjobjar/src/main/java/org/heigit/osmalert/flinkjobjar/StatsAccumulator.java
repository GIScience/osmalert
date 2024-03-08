package org.heigit.osmalert.flinkjobjar;

import java.util.HashSet;
import java.util.Set;


public class StatsAccumulator {

	int count = 0;
	Set<Integer> uniqueUsers = new HashSet<>();

	StatsAccumulator(int count, Set<Integer> uniqueUsers) {
		this.count = count;
		this.uniqueUsers = uniqueUsers;
	}

	public StatsAccumulator() {
	}

}
