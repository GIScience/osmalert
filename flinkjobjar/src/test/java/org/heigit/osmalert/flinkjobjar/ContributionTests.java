package org.heigit.osmalert.flinkjobjar;

import java.io.*;
import java.nio.file.*;

import org.heigit.osmalert.flinkjobjar.model.*;
import org.junit.jupiter.api.*;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.*;

import static org.assertj.core.api.Assertions.*;

public class ContributionTests {

	@Test
	void filterBoundingBoxAndPatternTest() throws IOException, ParseException {
		try {
			Contribution emptycontribution = Contribution.createContribution(null);
			assertThat(emptycontribution).isNull();
		} catch (AssertionError e) {
			assertThat(e).isNotNull();
		}

		Contribution contribution1 = Contribution.createContribution(Files.readString(Paths.get("src/test/resources/contribution1.json")));
		Geometry boundingBox1 = new GeometryFactory().toGeometry(new Envelope(13, 16, 1, 2));
		assertThat(contribution1.filterBoundingBoxAndPattern(boundingBox1, "access=forestry")).isTrue();
		assertThat(contribution1.filterBoundingBoxAndPattern(boundingBox1, "source=Landsat;JRC")).isTrue();
		assertThat(contribution1.filterBoundingBoxAndPattern(boundingBox1, "highway=track")).isTrue();
		assertThat(contribution1.filterBoundingBoxAndPattern(boundingBox1, "start_date=2014")).isTrue();

		Contribution contribution2 = Contribution.createContribution(Files.readString(Paths.get("src/test/resources/contribution2.json")));
		assertThat(contribution2.filterBoundingBoxAndPattern(boundingBox1, "access=forestry")).isFalse();
		assertThat(contribution2.filterBoundingBoxAndPattern(boundingBox1, "source=Landsat;JRC")).isFalse();
		assertThat(contribution2.filterBoundingBoxAndPattern(boundingBox1, "highway=track")).isFalse();
		assertThat(contribution2.filterBoundingBoxAndPattern(boundingBox1, "start_date=2014")).isFalse();

		Contribution contribution3 = Contribution.createContribution(Files.readString(Paths.get("src/test/resources/contribution3.json")));
		Geometry boundingBox3 = new WKTReader().read("POLYGON ((-88 41, -86 41, -86 43, -88 43, -88 41))");
		assertThat(contribution3.filterBoundingBoxAndPattern(boundingBox3, "entrance=yes")).isTrue();
		assertThat(contribution3.filterBoundingBoxAndPattern(boundingBox3, "access=forestry")).isFalse();

		assertThat(contribution3.filterBoundingBoxAndPattern(null, null)).isFalse();

	}

	@Test
	void hasPatternTest() throws IOException {
		try {
			Contribution emptycontribution = Contribution.createContribution(null);
			assertThat(emptycontribution).isNull();
		} catch (AssertionError e) {
			assertThat(e).isNotNull();
		}

		Contribution contribution1 = Contribution.createContribution(Files.readString(Paths.get("src/test/resources/contribution1.json")));
		assertThat(contribution1.hasPattern("access=forestry")).isTrue();
		assertThat(contribution1.hasPattern("source=Landsat;JRC")).isTrue();
		assertThat(contribution1.hasPattern("highway=track")).isTrue();
		assertThat(contribution1.hasPattern("start_date=2014")).isTrue();

		Contribution contribution2 = Contribution.createContribution(Files.readString(Paths.get("src/test/resources/contribution2.json")));
		assertThat(contribution2.hasPattern("access=forestry")).isFalse();

		Contribution contribution3 = Contribution.createContribution(Files.readString(Paths.get("src/test/resources/contribution3.json")));
		assertThat(contribution3.hasPattern("access=forestry")).isFalse();
		assertThat(contribution3.hasPattern("entrance=yes")).isTrue();

		assertThat(contribution3.hasPattern(null)).isTrue();
		assertThat(contribution3.hasPattern("")).isTrue();
	}

	@Test
	void isWithinGeometry() throws IOException, ParseException {
		try {
			Contribution emptycontribution = Contribution.createContribution(null);
			assertThat(emptycontribution).isNull();
		} catch (AssertionError e) {
			assertThat(e).isNotNull();
		}

		Contribution contribution1 = Contribution.createContribution(Files.readString(Paths.get("src/test/resources/contribution1.json")));
		Geometry boundingBox1 = new GeometryFactory().toGeometry(new Envelope(13, 16, 1, 2));
		assertThat(contribution1.isWithin(boundingBox1)).isTrue();

		Contribution contribution2 = Contribution.createContribution(Files.readString(Paths.get("src/test/resources/contribution2.json")));
		assertThat(contribution2.isWithin(boundingBox1)).isFalse();

		Contribution contribution3 = Contribution.createContribution(Files.readString(Paths.get("src/test/resources/contribution3.json")));
		Geometry boundingBox3 = new WKTReader().read("POLYGON ((-88 41, -86 41, -86 43, -88 43, -88 41))");
		assertThat(contribution3.isWithin(boundingBox3)).isTrue();
		assertThat(contribution3.isWithin(boundingBox1)).isFalse();

		assertThat(contribution3.isWithin(null)).isFalse();
	}
}