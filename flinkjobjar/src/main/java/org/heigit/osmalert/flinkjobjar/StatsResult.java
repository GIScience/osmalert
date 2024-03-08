package org.heigit.osmalert.flinkjobjar;


public final class StatsResult {

    public int count;
    public int uniqueUser = 0;

	public StatsResult(int count) {
		this.count = count;
	}

}

