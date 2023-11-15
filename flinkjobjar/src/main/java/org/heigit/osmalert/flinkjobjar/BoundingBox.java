package org.heigit.osmalert.flinkjobjar;

import org.locationtech.jts.geom.*;

public class BoundingBox {
	private final Envelope envelope;

	public BoundingBox(double x1, double x2, double y1, double y2) {
		this.envelope = new Envelope(x1, x2, y1, y2);
	}

	public BoundingBox(double[] points) {
		this.envelope = new Envelope(points[0], points[1], points[2], points[3]);
	}

	public boolean isPointInBoundingBox(Coordinate point) {
		return envelope.contains(point);
	}
}