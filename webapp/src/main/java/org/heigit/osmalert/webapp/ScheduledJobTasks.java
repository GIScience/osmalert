package org.heigit.osmalert.webapp;

import org.heigit.osmalert.webapp.domain.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

@Component
public class ScheduledJobTasks {

	private final RemoteJobService remoteJobService;
	private final JobRepository jobRepository;

	public ScheduledJobTasks(
		RemoteJobService remoteJobService,
		JobRepository jobRepository
	) {
		this.remoteJobService = remoteJobService;
		this.jobRepository = jobRepository;
	}

	@Scheduled(fixedRateString = "${osmalert.submission-polling-interval}")
	public void submitNewJobs() {
		Iterable<Job> unsubmittedJobs = jobRepository.findUnsubmittedJobs();
		for (Job job : unsubmittedJobs) {
			remoteJobService.submit(job);
		}
	}

	@Scheduled(cron = "0 * * * * *") // Executes at 12:00 AM UTC every day
	public void cancelFlinkJob() {
		Iterable<Job> jobsWithExpiryDate = jobRepository.findJobsByExpirationDateBefore();

        for (Job job : jobsWithExpiryDate) {
            remoteJobService.finishJob(job);
        }
	}

}