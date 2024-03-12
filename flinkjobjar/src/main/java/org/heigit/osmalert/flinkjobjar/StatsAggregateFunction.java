package org.heigit.osmalert.flinkjobjar;


import org.apache.flink.api.common.functions.*;
import org.heigit.osmalert.flinkjobjar.model.*;

import java.util.*;

public class StatsAggregateFunction implements AggregateFunction<Contribution, StatsAccumulator, StatsResult> {

     public StatsAccumulator createAccumulator() {
         return new StatsAccumulator();
     }

     public StatsAccumulator merge(StatsAccumulator a, StatsAccumulator b) {

		 Set<Integer> uniqueUsers = new HashSet<>();
		 uniqueUsers.addAll(a.uniqueUsers);
		 uniqueUsers.addAll(b.uniqueUsers);

		 return new StatsAccumulator(a.count + b.count, uniqueUsers);
     }

     public StatsAccumulator add(Contribution value, StatsAccumulator accumulator) {
         Set<Integer> uniqueUsers = new HashSet<>(accumulator.uniqueUsers);
		 uniqueUsers.add(value.getChangeset().getUserId());

	 	return new StatsAccumulator(accumulator.count + 1, uniqueUsers);
	 }

     public StatsResult getResult(StatsAccumulator accumulator) {
         return new StatsResult(accumulator.count, accumulator.uniqueUsers.size());
     }

 }