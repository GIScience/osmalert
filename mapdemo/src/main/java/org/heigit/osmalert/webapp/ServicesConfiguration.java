package org.heigit.osmalert.webapp;

import org.heigit.osmalert.flinkservice.*;
import org.heigit.osmalert.webapp.domain.*;
import org.heigit.osmalert.webapp.exceptions.*;
import org.heigit.osmalert.webapp.services.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;

@Configuration
public class ServicesConfiguration {

	@Bean
	RemoteJobService remoteJobService(
		JobRepository jobRepository,
		@Qualifier("osmalert") OsmalertConfiguration config
	) throws RemoteJobServiceException {
		if (config.isFlinkEnabled()) {
			FlinkRestsConfiguration flinkRestConfiguration = new FlinkRestsConfiguration(
				config.getFlinkHost(),
				config.getFlinkPort(),
				config.getFlinkMaxRetryAttempts()
			);
			try {
				FlinkClusterService flinkClusterService = new FlinkClusterService(flinkRestConfiguration);
				return new FlinkRemoteJobService(jobRepository, flinkClusterService);
			} catch (Exception e) {
				throw new RemoteJobServiceException("Failed to create FlinkClusterService", e);
			}
		} else {
			return new FakeRemoteJobService(jobRepository);
		}
	}

}
