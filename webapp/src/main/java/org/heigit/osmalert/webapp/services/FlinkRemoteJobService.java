package org.heigit.osmalert.webapp.services;

import org.apache.flink.api.common.*;
import org.heigit.osmalert.flinkservice.*;
import org.heigit.osmalert.webapp.domain.*;
import org.heigit.osmalert.webapp.exceptions.*;

public class FlinkRemoteJobService implements RemoteJobService {

	private final JobRepository jobRepository;
	private final FlinkClusterService flinkClusterService;

	public FlinkRemoteJobService(JobRepository jobRepository, FlinkClusterService flinkClusterService) {
		this.jobRepository = jobRepository;
		this.flinkClusterService = flinkClusterService;
	}

	@Override
	public void submit(Job job) {
		try {
			// TODO Change the bounding box to correct values
			String flinkId = flinkClusterService.submitJarJobToCluster(job.getJobName(), job.getEmail(), job.getBoundingBox(), job.getTimeWindowString(), job.getPattern());
			job.setFlinkId(flinkId);
			jobRepository.save(job);
		} catch (Exception e) {
			// TODO: handle exception properly
			throw new SubmitJobException("Failed to submit the job to flink cluster");
		}
	}

	@Override
	public RemoteJobStatus getStatus(Job job) {
		if (job.getFlinkId() == null) {
			return RemoteJobStatus.CREATED;
		}
		try {
			JobStatus status = flinkClusterService.getStatus(job.getFlinkId());
			return toRemoteJobStatus(status);
		} catch (Exception e) {
			return getStatusExceptionHandling(e);
		}
	}

	private RemoteJobStatus getStatusExceptionHandling(Exception e) {
		String exceptionMsg = e.getMessage();
		if (exceptionMsg.contains("Could not find Flink job") || exceptionMsg.contains("FlinkJobNotFoundException")) {
			return RemoteJobStatus.ARCHIVED;
		}
		return RemoteJobStatus.UNKNOWN;
	}

	@SuppressWarnings("OverlyComplexMethod")
	public RemoteJobStatus toRemoteJobStatus(JobStatus status) {
		return switch (status) {
			case INITIALIZING, CREATED -> RemoteJobStatus.SUBMITTED;
			case RUNNING, RECONCILING, RESTARTING, CANCELLING, SUSPENDED -> RemoteJobStatus.RUNNING;
			case FAILING, FAILED -> RemoteJobStatus.FAILED;
			case FINISHED, CANCELED -> RemoteJobStatus.FINISHED;
		};
	}
}