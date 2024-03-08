package org.heigit.osmalert.flinkjobjar;

import org.heigit.osmalert.flinkjobjar.model.*;
import org.junit.jupiter.api.Test;

import java.util.*;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;


class StatsAggregateFunctionTests {

	StatsAggregateFunction aggregateFunction = new StatsAggregateFunction();

	HashSet<Integer> set123 = new HashSet<>(asList(1, 2, 3));
	HashSet<Integer> set345 = new HashSet<>(asList(3, 4, 5));


	@Test
	void merge() {

		StatsAccumulator accumulator1 = new StatsAccumulator(2, set123);
		StatsAccumulator accumulator2 = new StatsAccumulator(7, set345);

		StatsAccumulator merged = this.aggregateFunction.merge(accumulator1, accumulator2);

		assertEquals(9, merged.count);

		//TODO: check items in set
		assertEquals(5, merged.uniqueUsers.size());
	}


	@Test
	void add() {

		Contribution contribution = new Contribution();
		contribution.getChangeset().setUserId(2);
		StatsAccumulator result = this.aggregateFunction.add(contribution, new StatsAccumulator(11, set123));

		assertEquals(12, result.count);
		assertEquals(3, result.uniqueUsers.size());

	}


	@Test
	void getResult() {

		StatsAccumulator accumulator = new StatsAccumulator(11, set123);
		StatsResult result = this.aggregateFunction.getResult(accumulator);

		assertEquals(11, result.count);
		assertEquals(3, result.uniqueUsers);

	}


}