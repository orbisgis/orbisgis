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

import org.gdms.Geometries;
import org.gdms.BaseTest;
import org.gdms.data.BasicWarningListener;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreation;
import org.gdms.data.DataSourceDefinition;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.file.FileSourceCreation;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.object.ObjectSourceDefinition;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.generic.GenericObjectDriver;
import org.orbisgis.utils.FileUtils;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTReader;
import org.gdms.data.types.ConstraintFactory;

public class ShapefileDriverTest extends TestCase {
	private DataSourceFactory dsf;

	private SimpleDateFormat sdf;

	private WKTReader wktReader;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dsf = new DataSourceFactory();
		dsf.setTempDir("src/test/resources/backup");
		sdf = new SimpleDateFormat("yyyy-MM-dd");
		wktReader = new WKTReader();
	}

	public void testOpenShapeWithDifferentCase() throws Exception {
                // should it fail if different case ? I say yes...
		DataSource ds = dsf.getDataSource(new File(BaseTest.internalData
				+ "multipolygon2d.Shp"));
                try {
		ds.open();
		ds.close();
                assertTrue(false);
                } catch (DriverException ex) {
                }
	}

	public void testBigShape() throws Exception {
		dsf.getSourceManager().register(
				"big",
				new FileSourceCreation(new File(BaseTest.internalData
						+ "landcover2000.shp"), null));
		DataSource ds = dsf.getDataSource("big");
		ds.open();
		ds.close();
	}

	public void testSaveEmptyGeometries() throws Exception {
		GenericObjectDriver omd = new GenericObjectDriver(new String[] {
				"the_geom", "id" }, new Type[] {
				TypeFactory.createType(Type.GEOMETRY,
                                        ConstraintFactory.createConstraint(Constraint.GEOMETRY_TYPE, GeometryConstraint.POINT)),
				TypeFactory.createType(Type.STRING) });
		dsf.getSourceManager().register("obj", new ObjectSourceDefinition(omd,"main"));
		DataSource ds = dsf.getDataSource("obj");
		GeometryFactory gf = new GeometryFactory();
		ds.open();
		ds.insertFilledRow(new Value[] {
				ValueFactory.createValue(gf
						.createGeometryCollection(new Geometry[0])),
				ValueFactory.createValue("0") });
		ds.insertFilledRow(new Value[] { null, ValueFactory.createValue("1") });
		DataSourceCreation target = new FileSourceCreation(new File(
				BaseTest.backupDir, "outputtestSaveEmptyGeometries.shp"), null);
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
		GenericObjectDriver omd = new GenericObjectDriver(new String[] { "id",
				"geom" }, new Type[] { TypeFactory.createType(Type.STRING),
				TypeFactory.createType(Type.GEOMETRY) });
		dsf.getSourceManager().register("obj", new ObjectSourceDefinition(omd,"main"));
		DataSourceCreation target = new FileSourceCreation(new File(
				BaseTest.backupDir,
				"outputtestSaveHeterogeneousGeometries.shp"), null);
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
				ConstraintFactory.createConstraint(Constraint.GEOMETRY_TYPE, GeometryConstraint.POLYGON));
		File shpFile = new File(BaseTest.backupDir,
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
                        ConstraintFactory.createConstraint(Constraint.GEOMETRY_TYPE, GeometryConstraint.POLYGON));
		File shpFile = new File(BaseTest.backupDir,
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
		assertTrue(ds.getString(0, "string").equalsIgnoreCase(" "));
		assertTrue(ds.getInt(0, "int") == 0);
		assertTrue(listener.warnings.isEmpty());
	}

	public void test2DReadWriteMultipolygon() throws Exception {
		Geometry geom = wktReader
				.read("MULTIPOLYGON ((( 107 113, 107 293, 368 293, 368 113, 107 113 )), (( 178 246, 178 270, 196 270, 196 246, 178 246 )))");
		test2DReadWrite(GeometryConstraint.MULTI_POLYGON, geom);
	}

	public void test2DReadWrite(int geometryType, Geometry geom)
			throws Exception {
		int nbCoords = geom.getCoordinates().length;
		DefaultMetadata m = new DefaultMetadata();
		m.addField("thelongernameintheworld", Type.STRING);
		m.addField("", Type.GEOMETRY,
                        ConstraintFactory.createConstraint(Constraint.GEOMETRY_TYPE, geometryType),
				ConstraintFactory.createConstraint(Constraint.GEOMETRY_DIMENSION, 2));
		File shpFile = new File(BaseTest.backupDir,
				"outputtest2DReadWrite.shp");
		if (shpFile.exists()) {
			assertTrue(shpFile.delete());
		}
		dsf.createDataSource(new FileSourceCreation(shpFile, m));
		DataSource ds = dsf.getDataSource(shpFile);
		ds.open();
		ds.insertEmptyRow();

		ds.setFieldValue(0, 0, ValueFactory.createValue(geom));
		ds.commit();
		ds.close();
		ds.open();
		Geometry geomRes = ds.getFieldValue(0, 0).getAsGeometry();
		Coordinate[] coordinates = geomRes
				.getCoordinates();
		ds.close();
		assertTrue(nbCoords == coordinates.length);
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

	public void test3DReadWrite(int geometryType, Geometry geom)
			throws Exception {
		DefaultMetadata m = new DefaultMetadata();
		m.addField("thelongernameintheworld", Type.STRING);
		m.addField("", Type.GEOMETRY,
				 ConstraintFactory.createConstraint(Constraint.GEOMETRY_TYPE, geometryType),
				ConstraintFactory.createConstraint(Constraint.GEOMETRY_DIMENSION, 3));
		File shpFile = new File(BaseTest.backupDir,
				"outputtest3DReadWrite.shp");
		if (shpFile.exists()) {
			assertTrue(shpFile.delete());
		}
		dsf.createDataSource(new FileSourceCreation(shpFile, m));
		DataSource ds = dsf.getDataSource(shpFile);
		ds.open();
		ds.insertEmptyRow();
		ds.setFieldValue(0, 0, ValueFactory.createValue(geom));
		ds.commit();
		ds.close();
		ds.open();
		Geometry linestring2 = ds.getFieldValue(0, 0).getAsGeometry();
		ds.close();
		assertTrue(ValueFactory.createValue(geom).equals(
				ValueFactory.createValue(linestring2)).getAsBoolean());
	}

	public void testNoConstraintWith3DGeom2SHP() throws Exception {
		GenericObjectDriver omd = new GenericObjectDriver(
				new String[] { "geom" }, new Type[] { TypeFactory
						.createType(Type.GEOMETRY) });
		omd.addValues(new Value[] { ValueFactory
				.createValue(new GeometryFactory().createPoint(new Coordinate(
						2, 2, 2))) });
		DataSource ds = dsf.getDataSource(omd,"main");

		File shpFile = new File(BaseTest.backupDir,
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
		m.addField("", Type.GEOMETRY,  ConstraintFactory.createConstraint(Constraint.GEOMETRY_TYPE, GeometryConstraint.POINT),
				ConstraintFactory.createConstraint(Constraint.GEOMETRY_DIMENSION, 3));
		File shpFile = new File(BaseTest.backupDir,
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
		m.addField("the_geom", Type.GEOMETRY, ConstraintFactory.createConstraint(Constraint.GEOMETRY_TYPE, GeometryConstraint.POINT),
				ConstraintFactory.createConstraint(Constraint.GEOMETRY_DIMENSION, 3));
		m.addField("f1", Type.BOOLEAN);
		m.addField("f2", Type.BYTE);
		m.addField("f3", Type.DATE);
		m.addField("f4", Type.DOUBLE);
		m.addField("f5", Type.FLOAT);
		m.addField("f6", Type.INT);
		m.addField("f7", Type.LONG);
		m.addField("f8", Type.SHORT);
		m.addField("f9", Type.STRING);

		File shpFile = new File(BaseTest.backupDir, "outputtestAllTypes.shp");
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

		assertTrue(listener.warnings.isEmpty());
	}

	// SEE THE GT BUG REPORT :
	// http://jira.codehaus.org/browse/GEOT-1268

	public void testReadAndWriteDBF() throws Exception {
		File file = new File(BaseTest.internalData + "alltypes.dbf");
		File backup = new File(BaseTest.internalData + "backup/alltypes.dbf");
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
		File file = new File(BaseTest.internalData + "alltypes.shp");
		File backup1 = new File(BaseTest.internalData + "backup/alltypes.shp");
		FileUtils.copy(file, backup1);
		File backup = backup1;
		file = new File(BaseTest.internalData + "alltypes.shx");
		File backup2 = new File(BaseTest.internalData + "backup/alltypes.shx");
		FileUtils.copy(file, backup2);
		file = new File(BaseTest.internalData + "alltypes.dbf");
		File backup3 = new File(BaseTest.internalData + "backup/alltypes.dbf");
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
				ConstraintFactory.createConstraint(Constraint.GEOMETRY_TYPE, GeometryConstraint.LINESTRING)));
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