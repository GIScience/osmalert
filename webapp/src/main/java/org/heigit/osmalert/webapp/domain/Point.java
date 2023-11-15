package org.heigit.osmalert.webapp.domain;

import jakarta.persistence.*;
import org.hibernate.validator.constraints.*;

@Entity
public class Point {

	@Range(min = -90, max = 90)
	private final double lat;

	@Range(min = -180, max = 180)
	private final double lon;

	@Id
	private Long id;

	public Point(double longitude, double latitude) {
		lat = latitude;
		lon = longitude;
	}

	public Point() {
		lat = 0;
		lon = 0;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}
}
