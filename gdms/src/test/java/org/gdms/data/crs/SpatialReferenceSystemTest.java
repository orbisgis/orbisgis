package org.gdms.data.crs;

import java.io.File;

import org.gdms.SourceTest;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.crs.SpatialReferenceSystem;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.strategies.SemanticException;

import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.WKTReader;

import fr.cts.crs.CoordinateReferenceSystem;

public class SpatialReferenceSystemTest extends SourceTest {

	/**
	 * A csv file with some validate data is used to test the CTS lib.
	 * 
	 * The file contains 4 columns
	 * 
	 * crs_source_code = string value --> 4326 crs_source_geom = geometry value.
	 * --> POINT(10, 12) crs_target_code = string value --> 27582
	 * crs_target_point = geometry value. --> POINT(11, 120)
	 * 
	 */
	public File crsFileValidator = new File(internalData + "crsData.csv");

	double EPSILON = 0.000001;

	public void testSRSTransform() throws Exception {
		DataSource ds = dsf.getDataSource(crsFileValidator);
		ds.open();

		WKTReader wktReader = new WKTReader();

		for (int i = 0; i < ds.getRowCount(); i++) {

			String sourceCodeCRS = ds.getFieldValue(i, 0).getAsString();
			String targetCodeCRS = ds.getFieldValue(i, 2).getAsString();

			SpatialReferenceSystem spatialReferenceSystem = new SpatialReferenceSystem(
					dsf, sourceCodeCRS, targetCodeCRS);

			Point transformedGeom = (Point) spatialReferenceSystem
					.transform(wktReader.read(ds.getFieldValue(i, 1)
							.getAsString()));

			Point targetGeom = (Point) wktReader.read(ds.getFieldValue(i, 3)
					.getAsString());
			System.out.println(spatialReferenceSystem
					.getCoordinateOperationSequence().getName()
					+ " -> Transformed geom : "
					+ transformedGeom.toText()
					+ " -> Target " + targetGeom.toText());

			System.out.println(targetGeom.getCoordinate().distance(
					transformedGeom.getCoordinate()));
		}
		ds.close();
	}

	public void testGDMSProj4CRSFactory() throws Exception {

		dsf.getSourceManager().loadSystemTables();
		GDMSProj4CRSFactory gdmsProj4CRSFactory = new GDMSProj4CRSFactory(dsf);

		// 320002120 for Lambert 93 nadgrid
		CoordinateReferenceSystem crs = gdmsProj4CRSFactory
				.getCRSFromSRID("4326");

		System.out.println(crs.getName());

	}

}
