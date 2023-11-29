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

	public boolean isJobRunning(String jobName) {
		boolean isRunning = false;
		for (Job job : jobRepository.findAll()) {
			if (!isJobFailedFinished(job) && job.getJobName().equals(jobName)) {
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

	public boolean validateCoordinates(String boundingBox) {
		try {
			String[] coordinates = boundingBox.split(",");
			double lowerLon = Double.parseDouble(coordinates[0]);
			double lowerLat = Double.parseDouble(coordinates[1]);
			double upperLon = Double.parseDouble(coordinates[2]);
			double upperLat = Double.parseDouble(coordinates[3]);

			return areCoordinatesInValidRange(lowerLon, lowerLat, upperLon, upperLat)
					   && doesCoordinatesFormABox(lowerLon, lowerLat, upperLon, upperLat)
					   && isLowerCoordinateSmallerThanUpperCoordinate(lowerLon, lowerLat, upperLon, upperLat);

		} catch (NumberFormatException | NullPointerException e) {
			return false;
		}
	}

	private boolean isLowerCoordinateSmallerThanUpperCoordinate(double lowerLon,
																double lowerLat,
																double upperLon,
																double upperLat) {
		return (lowerLon < upperLon) && (lowerLat < upperLat);
	}

	private boolean areCoordinatesInValidRange(
		double lowerLon, double lowerLat,
		double upperLon, double upperLat
	) {
		return isCoordinateInValidRange(lowerLon, 180)
				   && isCoordinateInValidRange(upperLon, 180)
				   && isCoordinateInValidRange(lowerLat, 90)
				   && isCoordinateInValidRange(upperLat, 90);
	}

	private boolean isCoordinateInValidRange(double coordinate, int maxValue) {
		return coordinate >= -maxValue && coordinate <= maxValue;
	}

	private boolean doesCoordinatesFormABox(double lowerLon, double lowerLat, double upperLon, double upperLat) {
		return (lowerLat != upperLat) && (lowerLon != upperLon);
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