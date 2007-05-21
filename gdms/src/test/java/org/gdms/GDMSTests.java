package org.gdms;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.gdms.data.DataSourceTest;
import org.gdms.data.EditionListenerTest;
import org.gdms.data.GettersTest;
import org.gdms.data.command.CommandStackTests;
import org.gdms.data.db.DataBaseTests;
import org.gdms.data.edition.DriverMetadataTest;
import org.gdms.data.edition.EditionTests;
import org.gdms.data.edition.FailedEditionTest;
import org.gdms.data.edition.MetadataTest;
import org.gdms.data.edition.UndoRedoTests;
import org.gdms.data.values.ValuesTest;
import org.gdms.drivers.DriversTest;
import org.gdms.drivers.ShapefileDriverTest;
import org.gdms.newFunctionalities.NoEmptyDataSetTest;
import org.gdms.oldFunctionalities.Tests;
import org.gdms.spatial.DataSourceCreationTest;
import org.gdms.spatial.FIDTest;
import org.gdms.spatial.PostGISTest;
import org.gdms.spatial.SpatialDriverMetadataTest;
import org.gdms.spatial.SpatialEditionTest;
import org.gdms.sql.strategies.SQLTest;

/**
 * @author Fernando Gonzalez Cortes
 */
public class GDMSTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for com.hardcode.gdbms.engine.test");
		// $JUnit-BEGIN$
		suite.addTestSuite(NoEmptyDataSetTest.class);
		suite.addTestSuite(EditionTests.class);
		suite.addTestSuite(UndoRedoTests.class);
		suite.addTestSuite(EditionListenerTest.class);
		suite.addTestSuite(FailedEditionTest.class);
		suite.addTestSuite(ValuesTest.class);
		suite.addTestSuite(DataBaseTests.class);
		suite.addTestSuite(SQLTest.class);
		suite.addTestSuite(CommandStackTests.class);
		suite.addTestSuite(GettersTest.class);
		suite.addTestSuite(SpatialEditionTest.class);
		suite.addTestSuite(SpatialDriverMetadataTest.class);
		suite.addTestSuite(FIDTest.class);
		suite.addTestSuite(DriversTest.class);
		suite.addTestSuite(DataSourceTest.class);
		suite.addTestSuite(ShapefileDriverTest.class);
		// $JUnit-END$
		return suite;
	}

	public static Test suite2() {
		TestSuite suite = new TestSuite("Not yet refactored and old tests");
		suite.addTestSuite(MetadataTest.class);
		suite.addTestSuite(DriverMetadataTest.class);
		/* TODO Uncoment testBigFileCreation */

		suite.addTestSuite(DataSourceCreationTest.class);
		suite.addTestSuite(PostGISTest.class);
		suite.addTestSuite(Tests.class);
		return suite;
	}

}
