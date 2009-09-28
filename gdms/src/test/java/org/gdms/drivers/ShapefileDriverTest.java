/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.drivers;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

import org.gdms.BaseTest;
import org.gdms.Geometries;
import org.gdms.SourceTest;
import org.gdms.data.BasicWarningListener;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreation;
import org.gdms.data.DataSourceDefinition;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.file.FileSourceCreation;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.object.ObjectSourceDefinition;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.DimensionConstraint;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.orbisgis.utils.FileUtils;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

public class ShapefileDriverTest extends TestCase {
	private DataSourceFactory dsf;

	private SimpleDateFormat sdf;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dsf = new DataSourceFactory();
		dsf.setTempDir("src/test/resources/backup");
		sdf = new SimpleDateFormat("yyyy-MM-dd");
	}

	public void testOpenShapeWithDifferentCase() throws Exception {
		DataSource ds = dsf.getDataSource(new File(SourceTest.internalData
				+ "multipolygon2d.Shp"));
		ds.open();
		ds.close();
	}

	public void testBigShape() throws Exception {
		dsf.getSourceManager().register(
				"big",
				new FileSourceDefinition(new File(SourceTest.internalData
						+ "landcover2000.shp")));
		DataSource ds = dsf.getDataSource("big");
		ds.open();
		ds.close();
	}

	public void testSaveSQL() throws Exception {
		dsf.getSourceManager().register(
				"shape",
				new FileSourceDefinition(new File(SourceTest.internalData
						+ "landcover2000.shp")));

		DataSource sql = dsf.getDataSourceFromSQL(
				"select Buffer(the_geom, 20) from shape",
				DataSourceFactory.DEFAULT);
		DataSourceDefinition target = new FileSourceDefinition(new File(
				SourceTest.backupDir, "outputtestSaveSQL.shp"));
		dsf.getSourceManager().register("buffer", target);
		dsf.saveContents("buffer", sql);

		DataSource ds = dsf.getDataSource("buffer");
		ds.open();
		sql.open();
		assertTrue(ds.getRowCount() == sql.getRowCount());
		sql.close();
		ds.close();
	}

	public void testSaveEmptyGeometries() throws Exception {
		ObjectMemoryDriver omd = new ObjectMemoryDriver(new String[] {
				"the_geom", "id" }, new Type[] {
				TypeFactory.createType(Type.GEOMETRY,
						new Constraint[] { new GeometryConstraint(
								GeometryConstraint.POINT) }),
				TypeFactory.createType(Type.STRING) });
		dsf.getSourceManager().register("obj", new ObjectSourceDefinition(omd));
		DataSource ds = dsf.getDataSource("obj");
		GeometryFactory gf = new GeometryFactory();
		ds.open();
		ds.insertFilledRow(new Value[] {
				ValueFactory.createValue(gf
						.createGeometryCollection(new Geometry[0])),
				ValueFactory.createValue("0") });
		ds.insertFilledRow(new Value[] { null, ValueFactory.createValue("1") });
		DataSourceDefinition target = new FileSourceDefinition(new File(
				SourceTest.backupDir, "outputtestSaveEmptyGeometries.shp"));
		dsf.getSourceManager().register("buffer", target);
		dsf.saveContents("buffer", ds);
		String contents = ds.getAsString();
		ds.close();

		DataSource otherDs = dsf.getDataSource("buffer");
		otherDs.open();
		assertTrue(2 == otherDs.getRowCount());
		assertTrue(otherDs.isNull(0, 0));
		assertTrue(otherDs.isNull(1, 0));
		assertTrue(otherDs.getAsString().equals(contents));
		otherDs.close();
	}

	public void testSaveHeterogeneousGeometries() throws Exception {
		ObjectMemoryDriver omd = new ObjectMemoryDriver(new String[] { "id",
				"geom" }, new Type[] { TypeFactory.createType(Type.STRING),
				TypeFactory.createType(Type.GEOMETRY) });
		dsf.getSourceManager().register("obj", new ObjectSourceDefinition(omd));
		DataSourceDefinition target = new FileSourceDefinition(new File(
				SourceTest.backupDir,
				"outputtestSaveHeterogeneousGeometries.shp"));
		DataSource ds = dsf.getDataSource("obj");
		ds.open();
		ds.insertFilledRow(new Value[] { ValueFactory.createValue("1"),
				ValueFactory.createValue(Geometries.getPolygon()), });
		ds.insertFilledRow(new Value[] { ValueFactory.createValue("0"),
				ValueFactory.createValue(Geometries.getPoint()), });
		try {
			dsf.getSourceManager().register("buffer", target);
			dsf.saveContents("buffer", ds);
			assertTrue(false);
		} catch (DriverException e) {
		}
		ds.close();
		ds.open();
		ds.insertFilledRow(new Value[] { ValueFactory.createValue("0"),
				ValueFactory.createValue(Geometries.getPoint()), });
		ds.insertFilledRow(new Value[] { ValueFactory.createValue("1"),
				ValueFactory.createValue(Geometries.getPolygon()), });
		try {
			dsf.saveContents("buffer", ds);
			assertTrue(false);
		} catch (DriverException e) {
		}
		ds.close();
	}

	public void testFieldNameTooLong() throws Exception {
		BasicWarningListener listener = new BasicWarningListener();
		dsf.setWarninglistener(listener);

		DefaultMetadata m = new DefaultMetadata();
		m.addField("thelongernameintheworld", Type.STRING);
		m.addField("", Type.GEOMETRY,
				new Constraint[] { new GeometryConstraint(
						GeometryConstraint.POLYGON) });
		File shpFile = new File(SourceTest.backupDir,
				"outputtestFieldNameTooLong.shp");
		if (shpFile.exists()) {
			assertTrue(shpFile.delete());
		}
		dsf.createDataSource(new FileSourceCreation(shpFile, m));
		assertTrue(listener.warnings.size() == 1);
	}

	public void testNullStringValue() throws Exception {
		BasicWarningListener listener = new BasicWarningListener();
		dsf.setWarninglistener(listener);

		DefaultMetadata m = new DefaultMetadata();
		m.addField("string", Type.STRING);
		m.addField("int", Type.INT);
		m.addField("", Type.GEOMETRY,
				new Constraint[] { new GeometryConstraint(
						GeometryConstraint.POLYGON) });
		File shpFile = new File(SourceTest.backupDir,
				"outputtestNullStringValue.shp");
		if (shpFile.exists()) {
			assertTrue(shpFile.delete());
		}
		dsf.createDataSource(new FileSourceCreation(shpFile, m));
		DataSource ds = dsf.getDataSource(shpFile);
		ds.open();
		ds.insertEmptyRow();
		ds.setString(0, "string", null);
		ds.setFieldValue(0, ds.getFieldIndexByName("int"), null);
		ds.commit();
		ds.close();
		ds.open();
		assertTrue(ds.getString(0, "string").equals(" "));
		assertTrue(ds.getInt(0, "int") == 0);
		assertTrue(listener.warnings.size() == 0);
	}

	public void test3DReadWritePoint() throws Exception {
		test3DReadWrite(GeometryConstraint.POINT, Geometries.getPoint3D());
	}

	public void test3DReadWriteLineString() throws Exception {
		test3DReadWrite(GeometryConstraint.MULTI_LINESTRING, Geometries
				.getMultilineString3D());
	}

	public void test3DReadWritePolygon() throws Exception {
		GeometryFactory gf = new GeometryFactory();
		LinearRing lr = gf.createLinearRing(new Coordinate[] {
				new Coordinate(0, 0, 20), new Coordinate(0, 10, 20),
				new Coordinate(10, 10, 20), new Coordinate(10, 0, 20),
				new Coordinate(0, 0, 20) });
		Polygon pol = gf.createPolygon(lr, null);
		MultiPolygon multiPol = gf.createMultiPolygon(new Polygon[] { pol });
		test3DReadWrite(GeometryConstraint.MULTI_POLYGON, multiPol);
	}

	public void test3DReadWriteMultipoint() throws Exception {
		test3DReadWrite(GeometryConstraint.MULTI_POINT, Geometries
				.getMultiPoint3D());
	}

	public void test3DReadWrite(int geometryType, Geometry linestring)
			throws Exception {
		DefaultMetadata m = new DefaultMetadata();
		m.addField("thelongernameintheworld", Type.STRING);
		m.addField("", Type.GEOMETRY, new Constraint[] {
				new GeometryConstraint(geometryType),
				new DimensionConstraint(3) });
		File shpFile = new File(SourceTest.backupDir,
				"outputtest3DReadWrite.shp");
		if (shpFile.exists()) {
			assertTrue(shpFile.delete());
		}
		dsf.createDataSource(new FileSourceCreation(shpFile, m));
		DataSource ds = dsf.getDataSource(shpFile);
		ds.open();
		ds.insertEmptyRow();
		ds.setFieldValue(0, 0, ValueFactory.createValue(linestring));
		ds.commit();
		ds.close();
		ds.open();
		Geometry linestring2 = ds.getFieldValue(0, 0).getAsGeometry();
		ds.close();
		assertTrue(ValueFactory.createValue(linestring).equals(
				ValueFactory.createValue(linestring2)).getAsBoolean());
	}

	public void testNoConstraintWith3DGeom2SHP() throws Exception {
		ObjectMemoryDriver omd = new ObjectMemoryDriver(
				new String[] { "geom" }, new Type[] { TypeFactory
						.createType(Type.GEOMETRY) });
		omd.addValues(new Value[] { ValueFactory
				.createValue(new GeometryFactory().createPoint(new Coordinate(
						2, 2, 2))) });
		DataSource ds = dsf.getDataSource(omd);

		File shpFile = new File(SourceTest.backupDir,
				"testNoConstraintWith3DGeom2SHP.shp");
		dsf.getSourceManager().register("shp", shpFile);
		dsf.saveContents("shp", ds);
		ds = dsf.getDataSource("shp");
		ds.open();
		Coordinate coord = ds.getFieldValue(0, 0).getAsGeometry()
				.getCoordinate();
		ds.close();
		assertTrue(coord.z == 2);
	}

	public void testWrongTypeForDBF() throws Exception {
		DefaultMetadata m = new DefaultMetadata();
		m.addField("id", Type.TIMESTAMP);
		m.addField("", Type.GEOMETRY, new Constraint[] {
				new GeometryConstraint(GeometryConstraint.POINT),
				new DimensionConstraint(3) });
		File shpFile = new File(SourceTest.backupDir,
				"outputtestWrongTypeForDBF.shp");
		if (shpFile.exists()) {
			assertTrue(shpFile.delete());
		}
		try {
			dsf.createDataSource(new FileSourceCreation(shpFile, m));
			assertTrue(false);
		} catch (DriverException e) {
		}
	}

	public void testAllTypes() throws Exception {
		BasicWarningListener listener = new BasicWarningListener();
		dsf.setWarninglistener(listener);

		DefaultMetadata m = new DefaultMetadata();
		m.addField("the_geom", Type.GEOMETRY, new Constraint[] {
				new GeometryConstraint(GeometryConstraint.POINT),
				new DimensionConstraint(3) });
		m.addField("f1", Type.BOOLEAN);
		m.addField("f2", Type.BYTE);
		m.addField("f3", Type.DATE);
		m.addField("f4", Type.DOUBLE);
		m.addField("f5", Type.FLOAT);
		m.addField("f6", Type.INT);
		m.addField("f7", Type.LONG);
		m.addField("f8", Type.SHORT);
		m.addField("f9", Type.STRING);

		File shpFile = new File(SourceTest.backupDir, "outputtestAllTypes.shp");
		if (shpFile.exists()) {
			assertTrue(shpFile.delete());
		}
		dsf.createDataSource(new FileSourceCreation(shpFile, m));
		DataSource ds = dsf.getDataSource(shpFile);
		ds.open();
		assertTrue(m.getFieldType(0).getTypeCode() == Type.GEOMETRY);
		assertTrue(m.getFieldType(1).getTypeCode() == Type.BOOLEAN);
		assertTrue(m.getFieldType(2).getTypeCode() == Type.BYTE);
		assertTrue(m.getFieldType(3).getTypeCode() == Type.DATE);
		assertTrue(m.getFieldType(4).getTypeCode() == Type.DOUBLE);
		assertTrue(m.getFieldType(5).getTypeCode() == Type.FLOAT);
		assertTrue(m.getFieldType(6).getTypeCode() == Type.INT);
		assertTrue(m.getFieldType(7).getTypeCode() == Type.LONG);
		assertTrue(m.getFieldType(8).getTypeCode() == Type.SHORT);
		assertTrue(m.getFieldType(9).getTypeCode() == Type.STRING);
		ds.commit();
		ds.close();

		assertTrue(listener.warnings.size() == 0);
	}

	// SEE THE GT BUG REPORT :
	// http://jira.codehaus.org/browse/GEOT-1268

	public void testReadAndWriteDBF() throws Exception {
		File file = new File(SourceTest.internalData + "alltypes.dbf");
		File backup = new File(SourceTest.internalData + "backup/alltypes.dbf");
		FileUtils.copy(file, backup);
		DataSource ds = dsf.getDataSource(backup);
		for (int i = 0; i < 2; i++) {
			ds.open();
			ds.insertFilledRow(new Value[] { ValueFactory.createValue(1),
					ValueFactory.createValue(2.4d),
					ValueFactory.createValue(2556),
					ValueFactory.createValue("sadkjsr"),
					ValueFactory.createValue(sdf.parse("1980-7-23")),
					ValueFactory.createValue(true) });
			ds.commit();
			ds.close();
		}
		ds.open();
		String content = ds.getAsString();
		ds.commit();
		ds.close();
		ds.open();
		assertTrue(content.equals(ds.getAsString()));
		ds.commit();
		ds.close();
	}

	public void testReadAndWriteSHP() throws Exception {
		File file = new File(SourceTest.internalData + "alltypes.shp");
		File backup1 = new File(SourceTest.internalData + "backup/alltypes.shp");
		FileUtils.copy(file, backup1);
		File backup = backup1;
		file = new File(SourceTest.internalData + "alltypes.shx");
		File backup2 = new File(SourceTest.internalData + "backup/alltypes.shx");
		FileUtils.copy(file, backup2);
		file = new File(SourceTest.internalData + "alltypes.dbf");
		File backup3 = new File(SourceTest.internalData + "backup/alltypes.dbf");
		FileUtils.copy(file, backup3);
		DataSource ds = dsf.getDataSource(backup);
		GeometryFactory gf = new GeometryFactory();
		for (int i = 0; i < 2; i++) {
			ds.open();
			ds.insertFilledRow(new Value[] {
					ValueFactory.createValue(gf.createPoint(new Coordinate(10,
							10))), ValueFactory.createValue(1),
					ValueFactory.createValue(3.4d),
					ValueFactory.createValue(2556),
					ValueFactory.createValue("sadkjsr"),
					ValueFactory.createValue(sdf.parse("1980-7-23")),
					ValueFactory.createValue(true) });
			ds.commit();
			ds.close();
		}
		ds.open();
		String content = ds.getAsString();
		ds.commit();
		ds.close();
		ds.open();
		assertTrue(content.equals(ds.getAsString()));
		ds.commit();
		ds.close();
	}

	public void testSHPGeometryWKB() throws Exception {
		File file = new File(BaseTest.internalData + "hedgerow.shp");
		DataSource ds = dsf.getDataSource(file);
		ds.open();
		Value geom = ds.getFieldValue(0, 0);
		byte[] wkb = geom.getBytes();
		Value read = ValueFactory.createValue(Type.GEOMETRY, wkb);
		ds.close();
		assertTrue(read.equals(geom).getAsBoolean());
	}

	public void testNullDates() throws Exception {
		DefaultMetadata m = new DefaultMetadata();
		m.addField("geom", TypeFactory.createType(Type.GEOMETRY,
				new GeometryConstraint(GeometryConstraint.LINESTRING)));
		m.addField("date", Type.DATE);
		DataSourceCreation dsc = new FileSourceCreation(new File(dsf
				.getTempFile()
				+ ".shp"), m);
		dsf.getSourceManager().register("sample", dsf.createDataSource(dsc));
		DataSource ds = dsf.getDataSource("sample");
		ds.open();
		ds.insertFilledRow(new Value[] { ValueFactory.createNullValue(),
				ValueFactory.createNullValue() });
		ds.commit();
		ds.close();

		ds.open();
		Date date = ds.getDate(0, 0);
		ds.close();
		assertTrue(date == null);
	}
}