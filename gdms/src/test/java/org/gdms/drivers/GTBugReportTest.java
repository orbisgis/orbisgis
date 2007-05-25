package org.gdms.drivers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.TestCase;

import org.geotools.data.PrjFileReader;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class GTBugReportTest {
	public static void main(String[] args) throws FileNotFoundException,
			IOException, FactoryException {
		final String prjFile = "../../datas2tests/shp/smallshape2D/bv_sap.prj";
		final PrjFileReader prjFileReader = new PrjFileReader(
				new FileInputStream(prjFile).getChannel());
		final CoordinateReferenceSystem crs = prjFileReader
				.getCoodinateSystem();

		TestCase.assertTrue(CRS.equalsIgnoreMetadata(crs, CRS
				.decode("EPSG:27572")));
	}
}