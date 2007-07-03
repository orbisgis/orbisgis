package org.gdms.spatial;

import java.io.File;
import java.util.Iterator;

import org.gdms.SourceTest;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.edition.PhysicalDirection;
import org.gdms.data.file.FileSourceCreation;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.data.indexes.IndexQuery;
import org.gdms.data.indexes.SpatialIndex;
import org.gdms.data.indexes.SpatialIndexQuery;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.LengthConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.values.BooleanValue;
import org.gdms.data.values.NullValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ReadAccess;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

public class SpatialEditionTest extends SourceTest {

	private GeometryFactory gf = new GeometryFactory();

	private void testIndex(String dsName) throws Exception {
		Geometry[] geometries = super.getNewGeometriesFor(dsName);
		Envelope[] bounds = new Envelope[geometries.length];
		for (int i = 0; i < bounds.length; i++) {
			bounds[i] = geometries[i].getEnvelopeInternal();
		}
		dsf.getIndexManager().buildIndex(dsName,
				super.getSpatialFieldName(dsName), SpatialIndex.SPATIAL_INDEX);
		SpatialDataSourceDecorator d = new SpatialDataSourceDecorator(dsf
				.getDataSource(dsName));

		d.open();
		int sfi = d.getSpatialFieldIndex();
		long rc = d.getRowCount();
		Value[] row = d.getRow(0);

		for (int i = 0; i < geometries.length; i++) {
			row[sfi] = ValueFactory.createValue(geometries[i]);
			d.insertFilledRow(row);
		}

		for (int i = 0; i < geometries.length; i++) {
			assertTrue(contains(d, d.queryIndex(new SpatialIndexQuery(
					bounds[i], super.getSpatialFieldName(dsName))),
					geometries[i]));
		}

		for (int i = 0; i < geometries.length; i++) {
			d.setFieldValue(rc + i, sfi, ValueFactory
					.createValue(geometries[geometries.length - i - 1]));
		}

		for (int i = 0; i < geometries.length; i++) {
			assertTrue(contains(d, d.queryIndex(new SpatialIndexQuery(
					bounds[i], super.getSpatialFieldName(dsName))),
					geometries[geometries.length - i - 1]));
		}

		for (int i = 0; i < geometries.length; i++) {
			d.deleteRow(rc + i);
		}

		for (int i = 0; i < geometries.length; i++) {
			assertTrue(!contains(d, d.queryIndex(new SpatialIndexQuery(
					bounds[i], super.getSpatialFieldName(dsName))),
					geometries[i]));
		}

		d.cancel();
	}

	private boolean contains(SpatialDataSourceDecorator sds,
			Iterator<PhysicalDirection> list, Geometry geometry)
			throws DriverException {
		while (list.hasNext()) {
			PhysicalDirection dir = list.next();
			if (super.equals(dir.getFieldValue(sds.getSpatialFieldIndex()),
					ValueFactory.createValue(geometry))) {
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
		dsf.getIndexManager().buildIndex(dsName,
				super.getSpatialFieldName(dsName), SpatialIndex.SPATIAL_INDEX);

		SpatialDataSourceDecorator d = new SpatialDataSourceDecorator(dsf
				.getDataSource(dsName));

		d.open();
		long rc = d.getRowCount();
		Envelope e = d.getFullExtent();
		d.deleteRow(0);
		d.deleteRow(0);
		SpatialIndexQuery query = new SpatialIndexQuery(e, super
				.getSpatialFieldName(dsName));
		assertTrue(rc - 2 == count(d.queryIndex(query)));
		d.commit();

		d.open();
		assertTrue(rc - 2 == d.getRowCount());
		d.cancel();
	}

	private long count(Iterator<PhysicalDirection> iter) {
		int count = 0;
		while (iter.hasNext()) {
			iter.next();
			count++;

		}
		return count;
	}

	public void testManyDeleteIndexedEdition() throws Exception {
		String[] resources = super.getSpatialResources();
		for (String resource : resources) {
			testManyDeleteIndexedEdition(resource);
		}
	}

	private void testIndexedEdition(String dsName) throws Exception {
		dsf.getIndexManager().buildIndex(dsName,
				super.getSpatialFieldName(dsName), SpatialIndex.SPATIAL_INDEX);
		SpatialDataSourceDecorator d = new SpatialDataSourceDecorator(dsf
				.getDataSource(dsName));

		d.open();
		long originalRowCount = d.getRowCount();
		Envelope e = d.getFullExtent();
		SpatialIndexQuery query = new SpatialIndexQuery(e, super
				.getSpatialFieldName(dsName));
		d.deleteRow(0);
		assertTrue(count(d.queryIndex(query)) == originalRowCount - 1);
		d.insertEmptyRowAt(1);
		assertTrue(d.isNull(1, 0));
		d.deleteRow(1);
		d.insertFilledRowAt(1, d.getRow(0));
		assertTrue(((BooleanValue) d.getFieldValue(1, 0).equals(
				d.getFieldValue(0, 0))).getValue());
		assertTrue(count(d.queryIndex(query)) == originalRowCount);
		d.deleteRow(1);
		assertTrue(count(d.queryIndex(query)) == originalRowCount - 1);
		d.commit();
		d.open();
		assertTrue(d.getRowCount() == originalRowCount - 1);
		assertTrue(count(d.queryIndex(query)) == originalRowCount - 1);
		d.cancel();
	}

	public void testIndexedEdition() throws Exception {
		String[] resources = super.getSpatialResources();
		for (String resource : resources) {
			testIndexedEdition(resource);
		}
	}

	private void testAdd(String dsName) throws Exception {
		SpatialDataSourceDecorator d = new SpatialDataSourceDecorator(dsf
				.getDataSource(dsName));

		Value[][] previous;

		d.open();
		previous = new Value[(int) d.getRowCount()][d.getMetadata()
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

	public void testBigFileCreation() throws Exception {
		new File("src/test/resources/backup/big.dbf").delete();
		File shpFile = new File("src/test/resources/backup/big.shp");
		shpFile.delete();
		new File("src/test/resources/backup/big.shx").delete();
		DefaultMetadata dsdm = new DefaultMetadata();
		dsdm.addField("geom", Type.GEOMETRY,
				new Constraint[] { new GeometryConstraint(
						GeometryConstraint.LINESTRING_2D) });
		dsdm.addField("text", Type.STRING,
				new Constraint[] { new LengthConstraint(10) });

		dsf.createDataSource(new FileSourceCreation(shpFile, dsdm));

		String dsName = "big" + System.currentTimeMillis();
		dsf.registerDataSource(dsName, new FileSourceDefinition(new File(
				"src/test/resources/backup/big.shp")));

		SpatialDataSourceDecorator d = new SpatialDataSourceDecorator(dsf
				.getDataSource(dsName));

		d.open();
		Coordinate[] coords = new Coordinate[3];
		coords[0] = new Coordinate(0, 0);
		coords[1] = new Coordinate(10, 10);
		coords[2] = new Coordinate(10, 15);
		Geometry geom = gf.createMultiLineString(new LineString[] { gf
				.createLineString(coords) });
		Value nv2 = ValueFactory.createValue("3.0");
		int n = 10000;
		for (int i = 0; i < n; i++) {
			d.insertEmptyRow();
			d.setFieldValue(d.getRowCount() - 1, 0, ValueFactory
					.createValue(geom));
			d.setFieldValue(d.getRowCount() - 1, 1, nv2);
		}
		d.commit();

		d = new SpatialDataSourceDecorator(dsf.getDataSource(dsName));
		d.open();
		assertTrue(d.getRowCount() == n);
		for (int i = 0; i < n; i++) {
			Geometry readGeom = d.getGeometry(i);
			assertTrue(readGeom
					.equals((com.vividsolutions.jts.geom.Geometry) geom));
			assertTrue(((BooleanValue) d.getFieldValue(i, 1).equals(nv2))
					.getValue());
		}
		d.cancel();
	}

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
		d.addField("name", d.getMetadata().getFieldType(0));
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

	private boolean fullExtentContainsAll(SpatialDataSourceDecorator sds)
			throws DriverException {
		Envelope fe = sds.getFullExtent();
		for (int i = 0; i < sds.getRowCount(); i++) {
			if (!sds.isNull(i, sds.getSpatialFieldIndex())) {
				if (!fe.contains(sds.getGeometry(i).getEnvelopeInternal())) {
					return false;
				}
			}
		}

		SpatialIndexQuery query = new SpatialIndexQuery(fe, super
				.getSpatialFieldName(sds.getName()));
		Iterator<PhysicalDirection> it = sds.queryIndex(query);
		if (it != null) {
			return count(it) == sds.getRowCount();
		}
		return true;
	}

	private void testEditedSpatialDataSourceFullExtent(
			SpatialDataSourceDecorator d) throws Exception {

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
			dsf.getIndexManager().buildIndex(resource,
					super.getSpatialFieldName(resource),
					SpatialIndex.SPATIAL_INDEX);
			SpatialDataSourceDecorator d = new SpatialDataSourceDecorator(dsf
					.getDataSource(resource, DataSourceFactory.UNDOABLE));
			d.open();
			testEditedSpatialDataSourceFullExtent(d);
			d.commit();
		}
	}

	public void testIndexInRetrievedDataSource() throws Exception {
		String dsName = super.getAnySpatialResource();
		dsf.getIndexManager().buildIndex(dsName,
				super.getSpatialFieldName(dsName), SpatialIndex.SPATIAL_INDEX);
		DataSource d = dsf.getDataSource(dsName);
		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(d);
		sds.open();
		SpatialIndexQuery query = new SpatialIndexQuery(sds.getFullExtent(),
				super.getSpatialFieldName(sds.getName()));

		Iterator<PhysicalDirection> it = sds.queryIndex(query);
		assertTrue(count(it) == sds.getRowCount());
	}

	public void testUpdateScope() throws Exception {
		String dsName = super.getAnySpatialResource();
		dsf.getIndexManager().buildIndex(dsName,
				super.getSpatialFieldName(dsName), SpatialIndex.SPATIAL_INDEX);
		DataSource d = dsf.getDataSource(dsName);
		d.open();
		Number[] scope = d.getScope(ReadAccess.X);
		for (int i = 0; i < d.getRowCount(); i++) {
			d.deleteRow(0);
		}
		Number[] newScope = d.getScope(ReadAccess.X);
		assertTrue((scope[0] != newScope[0]) || (scope[1] != newScope[1]));
	}

	public void testNullValuesDuringEdition() throws Exception {
		String dsName = super.getAnySpatialResource();
		dsf.getIndexManager().buildIndex(dsName,
				super.getSpatialFieldName(dsName), SpatialIndex.SPATIAL_INDEX);
		DataSource d = dsf.getDataSource(dsName);
		d.open();
		int fieldIndexByName = d.getFieldIndexByName(super
				.getSpatialFieldName(dsName));
		d.setFieldValue(0, fieldIndexByName, null);
		d.insertFilledRow(new Value[d.getFieldCount()]);
		assertTrue(d.getFieldValue(0, fieldIndexByName) instanceof NullValue);
		assertTrue(d.getFieldValue(d.getRowCount() - 1, fieldIndexByName) instanceof NullValue);
		d.cancel();
	}

	public void testCommitIndex() throws Exception {
		String dsName = super.getAnySpatialResource();
		dsf.getIndexManager().buildIndex(dsName,
				super.getSpatialFieldName(dsName), SpatialIndex.SPATIAL_INDEX);
		DataSource d = dsf.getDataSource(dsName);
		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(d);
		sds.open();
		Envelope extent = sds.getFullExtent();
		Geometry pointOutside = sds.getGeometry(0);
		pointOutside.getCoordinates()[0].setCoordinate(new Coordinate(extent
				.getMinX() - 11, extent.getMinY() - 11));
		Value[] row = d.getRow(0);
		row[sds.getSpatialFieldIndex()] = ValueFactory
				.createValue(pointOutside);
		d.insertFilledRow(row);
		sds.commit();

		d = dsf.getDataSource(dsName);
		sds = new SpatialDataSourceDecorator(d);
		sds.open();
		IndexQuery query = new SpatialIndexQuery(sds.getFullExtent(), super
				.getSpatialFieldName(dsName));
		assertTrue(count(sds.queryIndex(query)) == sds.getRowCount());
		sds.cancel();
	}
}
