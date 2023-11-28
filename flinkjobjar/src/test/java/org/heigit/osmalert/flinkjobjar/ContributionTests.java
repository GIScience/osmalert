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
	void isWithinGeometry() throws IOException, ParseException {
		//Files.readString(Paths.get());
		Contribution contribution1 = Contribution.createContribution(Files.readString(Paths.get("src/test/resources/contribution1.json")));
		Geometry boundingBox1 = new WKTReader().read("POLYGON ((13.0 0.0, 16.0 1.0, 16.0 2.0, 14.0 2.0, 13.0 0.0))");
		assertThat(contribution1.isWithin(boundingBox1)).isTrue();

		Contribution contribution2 = Contribution.createContribution(Files.readString(Paths.get("src/test/resources/contribution2.json")));
		Geometry boundingBox2 = new WKTReader().read("POLYGON ((13.0 0.0, 16.0 1.0, 16.0 2.0, 14.0 2.0, 13.0 0.0))");
		assertThat(contribution2.isWithin(boundingBox2)).isFalse();

		Contribution contribution3 = Contribution.createContribution(Files.readString(Paths.get("src/test/resources/contribution3.json")));
		Geometry boundingBox3 = new WKTReader().read("POLYGON ((-88 41, -86 41, -86 43, -88 43, -88 41))");
		assertThat(contribution3.isWithin(boundingBox3)).isTrue();
	}
}