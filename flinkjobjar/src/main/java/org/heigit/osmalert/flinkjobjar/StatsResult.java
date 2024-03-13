package org.heigit.osmalert.flinkjobjar;


public final class StatsResult {

    public final int count;
    public final int uniqueUsers;

	public StatsResult(int count, int uniqueUsers) {
		this.count = count;
		this.uniqueUsers = uniqueUsers;
	}

}

