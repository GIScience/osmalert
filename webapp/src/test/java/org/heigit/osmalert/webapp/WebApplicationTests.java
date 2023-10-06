package org.heigit.osmalert.webapp;

import org.heigit.osmalert.webapp.domain.JobRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class WebApplicationTests {

	@Autowired
	JobRepository repository;

	@Test
	void contextLoads() {
	}

	@Test
	void dataHasBeenInitialized() {
		assertThat(repository.findAll()).hasSize(555555);
	}

}
