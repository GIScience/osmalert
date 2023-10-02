package org.heigit.osmalert.webapp;

import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebApplicationConfiguration implements WebMvcConfigurer {

	/**
	 * Forward empty path to /jobs
	 */
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("forward:/jobs");
		registry.addViewController("").setViewName("forward:/jobs");
	}
}