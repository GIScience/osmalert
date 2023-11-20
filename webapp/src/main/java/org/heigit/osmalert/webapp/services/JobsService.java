package org.heigit.osmalert.webapp.services;

import java.util.*;
import java.util.stream.*;

import org.apache.commons.lang3.*;
import org.heigit.osmalert.webapp.domain.*;
import org.heigit.osmalert.webapp.exceptions.*;
import org.springframework.stereotype.*;

@Service
public class JobsService {

	private final JobRepository jobRepository;
	private final RemoteJobService remoteJobService;

	public JobsService(JobRepository jobRepository, RemoteJobService remoteJobService) {
		this.jobRepository = jobRepository;
		this.remoteJobService = remoteJobService;
	}

	public List<Job> getAllJobs() {
		Iterable<Job> all = jobRepository.findAll();
		return StreamSupport.stream(all.spliterator(), false)
							.toList();
	}

	public boolean isJobRunning(String jobname) {
		boolean isRunning = false;
		for (Job job : jobRepository.findAll()) {
			if (!isJobFailedFinished(job) && job.getJobName().equals(jobname)) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}

	public boolean isJobFailedFinished(Job job) {
		return remoteJobService.getStatus(job) == RemoteJobStatus.FAILED || remoteJobService.getStatus(job) == RemoteJobStatus.FINISHED;
	}

	public static String normalizeJobName(String jobName) {
		return StringUtils.normalizeSpace(jobName.toLowerCase());
	}

	public boolean validateCoordinates(String lowerLon, String upperLon, String lowerLat, String upperLat) {
		if (validateOneCoordinate(lowerLon, 180)) {
			if (validateOneCoordinate(upperLon, 180)) {
				if (validateOneCoordinate(lowerLat, 90)) {
					return validateOneCoordinate(upperLat, 90);
				}
			}
		}
		return false;
	}

	private boolean validateOneCoordinate(String coord, int max) {
		try {
			double coordinate = Double.parseDouble(coord);
			return coordinate >= -max && coordinate <= max;
		} catch (NumberFormatException | NullPointerException e) {
			throw new InvalidCoordinatesException("Invalid Coordinates");
		}
	}

	public String getJobStatus(long id) {
		Optional<Job> optionalJob = jobRepository.findById(id);
		return optionalJob.map(job -> remoteJobService.getStatus(job).name())
						  .orElseThrow(() -> new NoJobIdException(Long.toString(id)));
	}

	public void saveNewJob(Job newJob) {
		jobRepository.save(newJob);
	}

}
