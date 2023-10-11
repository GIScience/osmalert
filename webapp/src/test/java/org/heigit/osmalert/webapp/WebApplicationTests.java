package org.heigit.osmalert.webapp;

import org.heigit.osmalert.webapp.domain.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class WebApplicationTests {

	@Autowired
	JobRepository repository;

	@Autowired
	@Qualifier("osmalert")
	OsmalertConfiguration config;

	@Test
	void configurationCanBeRead() {
		assertThat(config).isNotNull();
		assertThat(config.isFlinkEnabled()).isFalse();
		assertThat(config.getFlinkHost()).isEqualTo("localhost");
		assertThat(config.getFlinkPort()).isEqualTo(8081);
		assertThat(config.getFlinkMaxRetryAttempts()).isEqualTo(1);
	}

	@Test
	void contextLoads() {
	}

	@Test
	void dataHasBeenInitialized() {
		assertThat(repository.findAll()).hasSize(2);
	}

}
