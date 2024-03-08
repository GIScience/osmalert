package org.heigit.osmalert.flinkjobjar;


import org.apache.flink.api.common.functions.*;
import org.heigit.osmalert.flinkjobjar.model.*;


public class StatsAggregateFunction implements AggregateFunction<Contribution, StatsAccumulator, StatsResult> {

     public StatsAccumulator createAccumulator() {
         return new StatsAccumulator();
     }

	 //TODO: use immutable accumulators
     public StatsAccumulator merge(StatsAccumulator a, StatsAccumulator b) {
         a.count += b.count;
		 a.uniqueUsers.addAll(b.uniqueUsers);

		 return a;
     }

     public StatsAccumulator add(Contribution value, StatsAccumulator accumulator) {
		 accumulator.count++;
		 accumulator.uniqueUsers.add(value.getUserId());

         return accumulator;
     }

     public StatsResult getResult(StatsAccumulator accumulator) {
         return new StatsResult(accumulator.count, accumulator.uniqueUsers.size());
     }

 }