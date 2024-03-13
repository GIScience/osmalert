package org.heigit.osmalert.flinkjobjar;

import java.util.*;

import org.heigit.osmalert.flinkjobjar.model.*;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;


class StatsAggregateFunctionTests {

	StatsAggregateFunction aggregateFunction = new StatsAggregateFunction();

	Set<Integer> set123 = Set.of(1, 2, 3);
	Set<Integer> set345 = Set.of(3, 4, 5);


	@Test
	void merge() {

		StatsAccumulator accumulator1 = new StatsAccumulator(2, set123);
		StatsAccumulator accumulator2 = new StatsAccumulator(7, set345);

		StatsAccumulator merged = this.aggregateFunction.merge(accumulator1, accumulator2);

		assertEquals(9, merged.count);

		assertEquals(5, merged.uniqueUsers.size());
		assertThat(merged.uniqueUsers).containsExactly(1, 2, 3, 4, 5);
	}


	@Test
	void addWithExistingUserId() {
		Contribution contribution = new Contribution();
		contribution.getChangeset().setUserId(2);
		StatsAccumulator result = this.aggregateFunction.add(contribution, new StatsAccumulator(11, set123));

		assertEquals(12, result.count);
		assertEquals(3, result.uniqueUsers.size());
		assertThat(result.uniqueUsers).containsExactly(1, 2, 3);
	}

	@Test
	void addWithNewUserId() {
		Contribution contribution = new Contribution();
		contribution.getChangeset().setUserId(7);
		StatsAccumulator result = this.aggregateFunction.add(contribution, new StatsAccumulator(11, set123));

		assertEquals(12, result.count);
		assertEquals(4, result.uniqueUsers.size());
		assertThat(result.uniqueUsers).containsExactly(1, 2, 3, 7);
	}


	@Test
	void getResult() {

		StatsAccumulator accumulator = new StatsAccumulator(11, set123);
		StatsResult result = this.aggregateFunction.getResult(accumulator);

		assertEquals(11, result.count);
		assertEquals(3, result.uniqueUsers);

	}


}