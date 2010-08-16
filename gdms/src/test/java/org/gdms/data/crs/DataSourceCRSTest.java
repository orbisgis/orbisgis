/*package org.gdms.data.crs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.gdms.BaseTest;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.SpatialDataSourceDecorator;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.geom.util.GeometryTransformer;
import com.vividsolutions.jts.io.WKTReader;

import fr.cts.CoordinateOperation;
import fr.cts.Identifier;
import fr.cts.IllegalCoordinateException;
import fr.cts.crs.GeodeticCRS;
import fr.cts.op.CoordinateOperationFactory;
import fr.cts.op.CoordinateOperationSequence;
import fr.cts.util.CRSUtil;

public class DataSourceCRSTest extends TestCase {

	private DataSourceFactory dsf;
	private ArrayList<Geometry> geometriesWithCRSCode;

	*//**
	 * A class to test CRS projection
	 *//*
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dsf = new DataSourceFactory();
		dsf.setTempDir("src/test/resources/backup");
		geometriesWithCRSCode = new ArrayList<Geometry>();
		WKTReader wktReader = new WKTReader();
		Geometry geom1 = wktReader
				.read(("POLYGON (( 306469.7641171428 2255170.293483699, 306469.7641171428 2256010.4135221257, 307242.67455249524 2256010.4135221257, 307242.67455249524 2255170.293483699, 306469.7641171428 2255170.293483699 ))"));

		geom1.setSRID(27572);

		geometriesWithCRSCode.add(geom1);

		Geometry geom3 = wktReader
				.read("LINESTRING ( 305739.2942285593 2254969.0992193245, 305789.9911274298 2254967.6507364996, 305801.5789900288 2254980.6870819237 )");
		geom3.setSRID(27572);

		geometriesWithCRSCode.add(geom3);

	}

	public void testSHPWithPRJ() throws Exception {
		String crsName = "NTF_Lambert_II_étendu";
		File file = new File(BaseTest.internalData + "landcover2000.shp");
		DataSource ds = dsf.getDataSource(file);
		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);
		sds.open();
		assertTrue(sds.getCRS().getName().equals(crsName));
		sds.close();

	}

	public void testASCIIFileCRSWithPRJ() throws Exception {

		File file = new File(BaseTest.internalData + "sample.asc");
		DataSource ds = dsf.getDataSource(file);
		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);
		sds.open();
		System.out.println(sds.getCRS().getName());
		sds.close();

	}

	public void testSHPWithoutPRJ() throws Exception {

		File file = new File(BaseTest.internalData + "hedgerow.shp");
		DataSource ds = dsf.getDataSource(file);
		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);
		sds.open();
		assertTrue(sds.getCRS().getName().equals("Unknow CRS"));
		sds.close();

	}

	public void geometryTransform() throws Exception {

		for (Geometry geom : geometriesWithCRSCode) {
			GeodeticCRS sourceCRS = (GeodeticCRS) CRSUtil
					.getCRSFromEPSG(Integer.toString(geom.getSRID()));
			GeodeticCRS targetCRS = (GeodeticCRS) CRSUtil
					.getCRSFromEPSG("4326");
			List<CoordinateOperation> ops = CoordinateOperationFactory
					.createCoordinateOperations(sourceCRS, targetCRS);
			final CoordinateOperationSequence cos = new CoordinateOperationSequence(
					new Identifier(DataSourceCRSTest.class, "" + " to " + ""),
					ops);
			GeometryTransformer gt = new GeometryTransformer() {
				protected CoordinateSequence transformCoordinates(
						CoordinateSequence cs, Geometry geom) {
					Coordinate[] cc = geom.getCoordinates();
					CoordinateSequence newcs = new CoordinateArraySequence(cc);
					for (int i = 0; i < cc.length; i++) {
						Coordinate c = cc[i];
						try {
							// if(cc.length==)
							double[] xyz = cos.transform(new double[] { c.x,
									c.y, c.z });
							newcs.setOrdinate(i, 0, xyz[0]);
							newcs.setOrdinate(i, 1, xyz[1]);
							if (xyz.length > 2)
								newcs.setOrdinate(i, 2, xyz[2]);
							else
								newcs.setOrdinate(i, 2, Double.NaN);
						} catch (IllegalCoordinateException ice) {
							ice.printStackTrace();
						}
					}
					return newcs;
				}
			};

			Geometry resultGeom = geom;

			resultGeom = gt.transform(geom);

			assertTrue(geom.getCoordinates().length == resultGeom
					.getCoordinates().length);

		}
	}

	*//**
	 * Transform a geometry in 27572 to wgs84
	 * 
	 * @throws Exception
	 *//*
	public void testCRSTransform() throws Exception {

		// In target crs
		final String targetCode = "4326";
		String tableName = "landcover";
		File file = new File(BaseTest.internalData + "landcover2000.shp");
		dsf.getSourceManager().register("landcover", file);

		File file4326 = new File(BaseTest.internalData
				+ "landcover2000_4326.shp");
		dsf.getSourceManager().register("landcover_4326", file4326);

		// dsf.executeSQL("select register('/tmp/target.shp','target')");
		dsf.executeSQL("create table target as select st_transform(the_geom, '"
				+ targetCode + "') from " + tableName + ";");

		SpatialDataSourceDecorator sds_4326 = new SpatialDataSourceDecorator(
				dsf.getDataSource("landcover_4326"));
		sds_4326.open();

		System.out.println("CRS source " + sds_4326.getCRS().toWkt());

		SpatialDataSourceDecorator transformedSDS = new SpatialDataSourceDecorator(
				dsf.getDataSource("target"));
		transformedSDS.open();

		for (int i = 0; i < transformedSDS.getRowCount(); i++) {
			Geometry geomTrans = transformedSDS.getGeometry(i);
			assertTrue(sds_4326.getGeometry(i).equals(geomTrans));
		}

		System.out.println("CRS target " + transformedSDS.getCRS().toWkt());
		transformedSDS.close();
		sds_4326.close();

	}
}
*/