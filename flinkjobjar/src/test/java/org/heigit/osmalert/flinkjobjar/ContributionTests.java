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
	void filterBoundingBoxAndPatternEmptyContributionTest() {
		try {
			Contribution emptycontribution = Contribution.createContribution(null);
			assertThat(emptycontribution).isNull();
		} catch (AssertionError e) {
			assertThat(e).isNotNull();
		}
	}

	// @Todo rearrange Tag and BBox separately
	@Test
	void filterBoundingBoxAndPatternContribution1Test() throws IOException, ParseException {
		Contribution contribution1 = Contribution.createContribution(Files.readString(Paths.get("src/test/resources/contribution1.json")));
		Geometry boundingBox1 = new GeometryFactory().toGeometry(new Envelope(13, 16, 1, 2));
		assertThat(contribution1.hasTag("access=forestry")).isTrue();
		assertThat(contribution1.isWithin(boundingBox1)).isTrue();
		assertThat(contribution1.hasTag("source=Landsat;JRC")).isTrue();
		assertThat(contribution1.isWithin(boundingBox1)).isTrue();
		assertThat(contribution1.hasTag("highway=track")).isTrue();
		assertThat(contribution1.isWithin(boundingBox1)).isTrue();
		assertThat(contribution1.hasTag("start_date=2014")).isTrue();
		assertThat(contribution1.isWithin(boundingBox1)).isTrue();
	}

	@Test
	void filterBoundingBoxAndPatternContribution2Test() throws IOException, ParseException {
		Contribution contribution2 = Contribution.createContribution(Files.readString(Paths.get("src/test/resources/contribution2.json")));
		Geometry boundingBox1 = new GeometryFactory().toGeometry(new Envelope(13, 16, 1, 2));
		assertThat(contribution2.isWithin(boundingBox1)).isFalse();
		assertThat(contribution2.hasTag("access=forestry")).isFalse();
		assertThat(contribution2.isWithin(boundingBox1)).isFalse();
		assertThat(contribution2.hasTag("source=Landsat;JRC")).isFalse();
		assertThat(contribution2.isWithin(boundingBox1)).isFalse();
		assertThat(contribution2.hasTag("highway=track")).isFalse();
		assertThat(contribution2.isWithin(boundingBox1)).isFalse();
		assertThat(contribution2.hasTag("start_date=2014")).isFalse();
	}

	@Test
	void filterBoundingBoxAndPatternContribution3Test() throws IOException, ParseException {
		Contribution contribution3 = Contribution.createContribution(Files.readString(Paths.get("src/test/resources/contribution3.json")));
		Geometry boundingBox3 = new WKTReader().read("POLYGON ((-88 41, -86 41, -86 43, -88 43, -88 41))");
		assertThat(contribution3.isWithin(boundingBox3)).isTrue();
		assertThat(contribution3.hasTag("entrance=yes")).isTrue();
		assertThat(contribution3.hasTag("access=forestry")).isFalse();
		assertThat(contribution3.isWithin(null)).isFalse();
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
		assertThat(contribution1.hasTag("access=forestry")).isTrue();
		assertThat(contribution1.hasTag("source=Landsat;JRC")).isTrue();
		assertThat(contribution1.hasTag("highway=track")).isTrue();
		assertThat(contribution1.hasTag("start_date=2014")).isTrue();

		Contribution contribution2 = Contribution.createContribution(Files.readString(Paths.get("src/test/resources/contribution2.json")));
		assertThat(contribution2.hasTag("access=forestry")).isFalse();

		Contribution contribution3 = Contribution.createContribution(Files.readString(Paths.get("src/test/resources/contribution3.json")));
		assertThat(contribution3.hasTag("access=forestry")).isFalse();
		assertThat(contribution3.hasTag("entrance=yes")).isTrue();

		assertThat(contribution3.hasTag(null)).isTrue();
		assertThat(contribution3.hasTag("")).isTrue();
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

	@Test
	void numberOfContributors() throws IOException {
		Contribution contribution1 = Contribution.createContribution(Files.readString(Paths.get("src/test/resources/contribution1.json")));
		Contribution contribution2 = Contribution.createContribution(Files.readString(Paths.get("src/test/resources/contribution2.json")));
		Contribution contribution3 = Contribution.createContribution(Files.readString(Paths.get("src/test/resources/contribution3.json")));

		Assertions.assertEquals(4399830, contribution1.getUserId());
		Assertions.assertEquals(10233983, contribution2.getUserId());
		Assertions.assertEquals(20473249, contribution3.getUserId());

	}
}