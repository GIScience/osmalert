package org.heigit.osmalert.webapp.services;

import java.util.*;

import org.heigit.osmalert.webapp.domain.*;

public class FakeRemoteJobService implements RemoteJobService {

	private final JobRepository jobRepository;
	private final Random random = new Random();

	private final Map<Long, RemoteJobStatus> submittedJobs = new HashMap<>();

	public FakeRemoteJobService(JobRepository jobRepository) {
		this.jobRepository = jobRepository;
	}

	@Override
	public void submit(Job request) {
		String fakeFlinkId = "fake-" + random.nextLong();
		request.setFlinkId(fakeFlinkId);
		jobRepository.save(request);

		submittedJobs.put(request.getId(), RemoteJobStatus.SUBMITTED);

		Runnable changeJobStatus = () -> {
			try {
				long randomDelay = 5000 + random.nextLong(10000);
				Thread.sleep(randomDelay);
				submittedJobs.put(request.getId(), RemoteJobStatus.RUNNING);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		};

		// TODO: Use proper Spring scheduling
		new Thread(changeJobStatus).start();
	}

	@Override
	public RemoteJobStatus getStatus(Job job) {
		if (submittedJobs.containsKey(job.getId())) {
			return submittedJobs.get(job.getId());
		}
		if (job.getFlinkId() != null) {
			return RemoteJobStatus.FAILED;
		} else {
			return RemoteJobStatus.CREATED;
		}
	}
}
