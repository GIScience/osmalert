package org.heigit.osmalert.webapp.domain;

public interface RemoteJobService {

	void submit(Job job);

	RemoteJobStatus getStatus(Job job);
}
