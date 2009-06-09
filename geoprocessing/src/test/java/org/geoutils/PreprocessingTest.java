package org.geoutils;

import java.io.File;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

import junit.framework.TestCase;

public class PreprocessingTest extends TestCase {

	public static DataSourceFactory dsf = new DataSourceFactory();

	public void testUpdateZWithAValue() throws DriverLoadException,
			DataSourceCreationException, DriverException,
			NonEditableDataSourceException {
		String path = "data/polygonfortopology.shp";
		DataSource ds = dsf.getDataSource(new File(path));

		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);

		sds.open();

		double value = 20.d;

		for (int i = 0; i < sds.getRowCount(); i++) {

			Geometry geom = sds.getGeometry(i);
			Coordinate[] coords = geom.getCoordinates();
			Geometry geomResult = GeomUtil.updateZ(geom, value);
			Coordinate[] coordsR = geomResult.getCoordinates();
			for (int j = 0; j < coords.length; j++) {

				assertTrue(coordsR[j].z == value);

			}
		}

		sds.close();

	}

}
