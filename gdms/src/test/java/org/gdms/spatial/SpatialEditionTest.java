package org.gdms.spatial;

import java.util.List;

import org.gdms.SourceTest;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.values.BooleanValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.spatial.FID;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class SpatialEditionTest extends SourceTest {

	private GeometryFactory gf = new GeometryFactory();

	private void testIndex(String dsName) throws Exception {
		Geometry[] geometries = super.getNewGeometriesFor(dsName);
		Envelope[] bounds = new Envelope[geometries.length];
		for (int i = 0; i < bounds.length; i++) {
			bounds[i] = geometries[i].getEnvelopeInternal();
		}
		SpatialDataSource d = new SpatialDataSourceDecorator(dsf
				.getDataSource(dsName));

		d.open();
		d.buildIndex();
		int sfi = d.getSpatialFieldIndex();
		long rc = d.getRowCount();
		Value[] row = d.getRow(0);

		for (int i = 0; i < geometries.length; i++) {
			row[sfi] = ValueFactory.createValue(geometries[i]);
			d.insertFilledRow(row);
		}

		for (int i = 0; i < geometries.length; i++) {
			assertTrue(contains(d, d.queryIndex(bounds[i]), geometries[i]));
		}

		for (int i = 0; i < geometries.length; i++) {
			d.setFieldValue(rc + i, sfi, ValueFactory
					.createValue(geometries[geometries.length - i - 1]));
		}

		for (int i = 0; i < geometries.length; i++) {
			assertTrue(contains(d, d.queryIndex(bounds[i]),
					geometries[geometries.length - i - 1]));
		}

		for (int i = 0; i < geometries.length; i++) {
			d.deleteRow(rc + i);
		}

		for (int i = 0; i < geometries.length; i++) {
			assertTrue(!contains(d, d.queryIndex(bounds[i]), geometries[i]));
		}

		d.cancel();
	}

	private boolean contains(SpatialDataSource sds, List<FID> list,
			Geometry geometry) throws DriverException {
		for (int i = 0; i < list.size(); i++) {
			if (sds.getGeometry(list.get(i)) == geometry) {
				return true;
			}
		}

		return false;
	}

	public void testIndex() throws Exception {
		String[] resources = super.getSpatialResources();
		for (String resource : resources) {
			testIndex(resource);
		}
	}

	private void testManyDeleteIndexedEdition(String dsName) throws Exception {
		SpatialDataSource d = new SpatialDataSourceDecorator(dsf
				.getDataSource(dsName));

		d.open();
		long rc = d.getRowCount();
		d.buildIndex();
		d.deleteRow(0);
		d.deleteRow(0);
		assertTrue(rc - 2 == d.getRowCount());
		d.commit();

		d.open();
		assertTrue(rc - 2 == d.getRowCount());
		d.cancel();
	}

	public void testManyDeleteIndexedEdition() throws Exception {
		String[] resources = super.getSpatialResources();
		for (String resource : resources) {
			testManyDeleteIndexedEdition(resource);
		}
	}

	private void testIndexedEdition(String dsName) throws Exception {
		SpatialDataSource d = new SpatialDataSourceDecorator(dsf
				.getDataSource(dsName));

		d.open();
		d.buildIndex();
		long originalRowCount = d.getRowCount();
		d.deleteRow(0);
		assertTrue(d.getRowCount() == originalRowCount - 1);
		d.insertEmptyRowAt(1);
		assertTrue(d.isNull(1, 0));
		d.deleteRow(1);
		d.insertFilledRowAt(1, d.getRow(0));
		assertTrue(((BooleanValue) d.getFieldValue(1, 0).equals(
				d.getFieldValue(0, 0))).getValue());
		d.deleteRow(1);
		d.commit();
		d.open();
		assertTrue(d.getRowCount() == originalRowCount - 1);
		d.cancel();
	}

	public void testIndexedEdition() throws Exception {
		String[] resources = super.getSpatialResources();
		for (String resource : resources) {
			testIndexedEdition(resource);
		}
	}

	private void testAdd(String dsName) throws Exception {
		SpatialDataSource d = new SpatialDataSourceDecorator(dsf
				.getDataSource(dsName));

		Value[][] previous;

		d.open();
		previous = new Value[(int) d.getRowCount()][d.getDataSourceMetadata()
				.getFieldCount()];
		for (int i = 0; i < previous.length; i++) {
			for (int j = 0; j < previous[i].length; j++) {
				previous[i][j] = d.getFieldValue(i, j);
			}
		}
		Geometry geom = super.getNewGeometriesFor(dsName)[0];
		Value nv2 = d.getFieldValue(0, 1);
		d.insertEmptyRow();
		d.setFieldValue(d.getRowCount() - 1, 0, ValueFactory.createValue(geom));
		d.setFieldValue(d.getRowCount() - 1, 1, nv2);
		d.commit();

		d = new SpatialDataSourceDecorator(dsf.getDataSource(dsName));
		d.open();
		for (int i = 0; i < previous.length; i++) {
			for (int j = 0; j < previous[i].length; j++) {
				assertTrue(!((BooleanValue) previous[i][j].notEquals(d
						.getFieldValue(i, j))).getValue());
			}
		}
		assertTrue(d.getRowCount() == previous.length + 1);
		assertTrue(d.getGeometry(previous.length).equals(geom));
		assertTrue(((BooleanValue) d.getFieldValue(previous.length, 1).equals(
				nv2)).getValue());
		d.cancel();
	}

	public void testAdd() throws Exception {
		String[] resources = super.getSpatialResources();
		for (String resource : resources) {
			testAdd(resource);
		}
	}

	/*
	 * public void testBigFileCreation() throws Exception { new
	 * File("src/test/resources/big.dbf").delete(); new
	 * File("src/test/resources/big.shp").delete(); new
	 * File("src/test/resources/big.shx").delete(); DefaultSpatialDriverMetadata
	 * dsdm = new DefaultSpatialDriverMetadata(); dsdm.addSpatialField("geom",
	 * SpatialDataSource.MULTILINESTRING); dsdm.addField("text", "Numeric", new
	 * String[] { DBFDriver.LENGTH, DBFDriver.PRECISION }, new String[] { "1",
	 * "0" }); dsf.createDataSource(new FileSourceCreation(new File(
	 * "src/test/resources/big.shp"), dsdm));
	 *
	 * dsf.registerDataSource("big", new SpatialFileSourceDefinition(new File(
	 * "src/test/resources/big.shp")));
	 *
	 * SpatialDataSource d = (SpatialDataSource) ds.getDataSource("big");
	 *
	 * d.beginTrans(); Coordinate[] coords = new Coordinate[3]; coords[0] = new
	 * Coordinate(0, 0); coords[1] = new Coordinate(10, 10); coords[2] = new
	 * Coordinate(10, 15); Geometry geom = gf.createMultiLineString(new
	 * LineString[] { gf .createLineString(coords) }); Value nv2 =
	 * ValueFactory.createValue(3.0); int n = 10000; for (int i = 0; i < n; i++) {
	 * d.insertEmptyRow(); d.setFieldValue(d.getRowCount() - 1, 0, ValueFactory
	 * .createValue(geom)); d.setFieldValue(d.getRowCount() - 1, 1, nv2); }
	 * d.commitTrans();
	 *
	 * d = (SpatialDataSource) dsf.getDataSource("big"); d.start();
	 * assertTrue(d.getRowCount() == n); for (int i = 0; i < n; i++) { Geometry
	 * readGeom = d.getGeometry(i); assertTrue(readGeom
	 * .equals((com.vividsolutions.jts.geom.Geometry) geom));
	 * assertTrue(((BooleanValue) d.getFieldValue(i, 1).equals(nv2))
	 * .getValue()); } d.stop(); }
	 */

	public void testIsModified() throws Exception {
		DataSource d = dsf.getDataSource(super.getAnySpatialResource());

		d.open();
		assertFalse(d.isModified());
		d.insertEmptyRow();
		assertTrue(d.isModified());
		d.cancel();

		d.open();
		assertFalse(d.isModified());
		d.insertFilledRow(d.getRow(0));
		assertTrue(d.isModified());
		d.cancel();

		d.open();
		assertFalse(d.isModified());
		d.removeField(1);
		assertTrue(d.isModified());
		d.cancel();

		d.open();
		assertFalse(d.isModified());
		d.addField("name", d.getDriverMetadata().getFieldType(0));
		assertTrue(d.isModified());
		d.cancel();

		d.open();
		assertFalse(d.isModified());
		d.setFieldName(1, "asd");
		assertTrue(d.isModified());
		d.cancel();

		d.open();
		assertFalse(d.isModified());
		d.setFieldValue(0, 0, ValueFactory.createNullValue());
		assertTrue(d.isModified());
		d.cancel();

		DataSource ads = d;
		ads.open();
		assertFalse(ads.isModified());
		ads.deleteRow(0);
		assertTrue(ads.isModified());
		ads.cancel();

		ads.open();
		assertFalse(ads.isModified());
		ads.insertEmptyRowAt(0);
		assertTrue(ads.isModified());
		ads.cancel();

		ads.open();
		assertFalse(ads.isModified());
		ads.insertFilledRowAt(0, ads.getRow(0));
		assertTrue(ads.isModified());
		ads.cancel();

	}

	private boolean fullExtentContainsAll(SpatialDataSource sds)
			throws DriverException {
		Envelope fe = sds.getFullExtent();
		for (int i = 0; i < sds.getRowCount(); i++) {
			if (!sds.isNull(i, sds.getSpatialFieldIndex())) {
				if (!fe.contains(sds.getGeometry(i).getEnvelopeInternal())) {
					return false;
				}
			}
		}

		return true;
	}

	private void testEditedSpatialDataSourceFullExtent(SpatialDataSource d)
			throws Exception {

		int sfi = d.getSpatialFieldIndex();
		Envelope originalExtent = d.getFullExtent();

		Value[] row = d.getRow(0);
		double x = originalExtent.getMinX();
		double y = originalExtent.getMinY();
		row[sfi] = ValueFactory.createValue(gf.createPoint(new Coordinate(
				x - 10, y - 10)));
		d.insertFilledRow(row);
		assertTrue(fullExtentContainsAll(d));

		d.setFieldValue(d.getRowCount() - 1, sfi, ValueFactory.createValue(gf
				.createPoint(new Coordinate(x - 11, y - 11))));
		assertTrue(fullExtentContainsAll(d));

		d.setFieldValue(d.getRowCount() - 1, sfi, ValueFactory.createValue(gf
				.createPoint(new Coordinate(x - 9, y - 9))));
		assertTrue(fullExtentContainsAll(d));

		d.deleteRow(d.getRowCount() - 1);
		assertTrue(fullExtentContainsAll(d));

		d.undo();
		assertTrue(fullExtentContainsAll(d));

		d.redo();
		assertTrue(fullExtentContainsAll(d));

	}

	public void testEditedSpatialDataSourceFullExtentFile() throws Exception {
		String[] resources = super.getSpatialResources();
		for (String resource : resources) {
			SpatialDataSource d = new SpatialDataSourceDecorator(dsf
					.getDataSource(resource, DataSourceFactory.UNDOABLE));
			d.open();
			testEditedSpatialDataSourceFullExtent(d);
			d.commit();

			d.open();
			d.buildIndex();
			testEditedSpatialDataSourceFullExtent(d);
			d.commit();
		}
	}

}
