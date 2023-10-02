package org.heigit.osmalert.webapp;

import org.heigit.osmalert.flinkservice.*;
import org.heigit.osmalert.webapp.domain.*;
import org.heigit.osmalert.webapp.services.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;

@Configuration
public class ServicesConfiguration {

	@Bean
	RemoteJobService remoteJobService(
		JobRepository jobRepository,
		@Qualifier("osmalert") OsmalertConfiguration config
	) throws Exception {
		if (config.isFlinkEnabled()) {
			FlinkRestsConfiguration flinkRestConfiguration = new FlinkRestsConfiguration(
				"localhost",
				8081,
				3
			);
			// TODO: Handle potential exceptions
			FlinkClusterService flinkClusterService = new FlinkClusterService(flinkRestConfiguration);
			return new FlinkRemoteJobService(jobRepository, flinkClusterService);
		} else {
			return new FakeRemoteJobService(jobRepository);
		}
	}

}
