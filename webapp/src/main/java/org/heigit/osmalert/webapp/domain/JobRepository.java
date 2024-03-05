package org.heigit.osmalert.webapp.domain;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.*;

public interface JobRepository extends CrudRepository<Job, Long> {

	@Query("SELECT j FROM Job j WHERE j.flinkId is null")
	Iterable<Job> findUnsubmittedJobs();

	@Query("SELECT j from Job j where j.expirationDate = current date ")
	Iterable<Job> findJobsByExpirationDateBefore();
}
