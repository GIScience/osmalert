package org.heigit.osmalert.webapp.toggles;

import java.util.*;

import org.springframework.boot.context.properties.*;
import org.springframework.stereotype.*;

/**
 * This configuration class is used to enable/disable features in the application
 * in order to facilitate trunk-based development.
 * <p>
 * Features can be enabled by adding them to the list of features in application properties.
 */
@Component("toggles")
@EnableConfigurationProperties(TogglesConfiguration.class)
@ConfigurationProperties(prefix = "toggles")
public class TogglesConfiguration {

	private List<String> features = new ArrayList<>();

	public List<String> getFeatures() {
		return features;
	}

	public void setFeatures(List<String> features) {
		this.features = features;
	}

	public boolean isEnabled(String feature) {
		return features.contains(feature);
	}

	public boolean isDisabled(String feature) {
		return !isEnabled(feature);
	}

	public void ifEnabled(String feature, Runnable runnable) {
		if (isEnabled(feature)) {
			runnable.run();
		}
	}

	public void unlessEnabled(String feature, Runnable runnable) {
		if (!isEnabled(feature)) {
			runnable.run();
		}
	}

	public void ifEnabledElse(String feature, Runnable runnable, Runnable elseRunnable) {
		if (isEnabled(feature)) {
			runnable.run();
		} else {
			elseRunnable.run();
		}
	}
}