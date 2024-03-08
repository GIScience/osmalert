package org.heigit.osmalert.flinkjobjar;


public final class StatsResult {

    public int count;
    public int uniqueUsers;

	public StatsResult(int count, int uniqueUsers) {
		this.count = count;
		this.uniqueUsers = uniqueUsers;
	}

}

