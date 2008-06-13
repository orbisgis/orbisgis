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
package org.gdms.spatial;

import org.gdms.SourceTest;

/**
 * 
 */
public class PostGISTest extends SourceTest {
	//
	// private static GeometryFactory gf = new GeometryFactory();
	//
	// /**
	// * DOCUMENT ME!
	// *
	// * @throws Exception
	// * DOCUMENT ME!
	// */
	// public static void testOpen(DataSourceFactory ds, String pgds)
	// throws Exception {
	// DataSource d = ds.getDataSource(pgds);
	//
	// d.start();
	// d.stop();
	// }
	//
	// public void testOpen() throws Exception {
	// testOpen(ds, "pgWithGDBMS");
	// }
	//
	// /**
	// * DOCUMENT ME!
	// *
	// * @throws Exception
	// * DOCUMENT ME!
	// */
	// public static void testGetFieldValue(DataSourceFactory ds, String pgds)
	// throws Exception {
	// SpatialDataSource d = (SpatialDataSource) ds.getDataSource(pgds);
	//
	// d.start();
	//
	// int gfi = d.getSpatialFieldIndex();
	//
	// d.getFieldValue(0, gfi);
	// d.stop();
	// }
	//
	// public void testGetFieldValue() throws Exception {
	// testGetFieldValue(ds, "pgWithGDBMS");
	// }
	//
	// /**
	// * DOCUMENT ME!
	// *
	// * @throws Exception
	// * DOCUMENT ME!
	// */
	// public static void testGetRowCount(DataSourceFactory ds, String pgds)
	// throws Exception {
	// DataSource d = ds.getDataSource(pgds);
	//
	// d.start();
	//
	// long myCount = d.getRowCount();
	// assertTrue(myCount >= 22);
	//
	// d.stop();
	// }
	//
	// public void testGetRowCount() throws Exception {
	// testGetRowCount(ds, "pgWithGDBMS");
	// }
	//
	// /**
	// * DOCUMENT ME!
	// *
	// * @throws Exception
	// * DOCUMENT ME!
	// */
	// public static void testgetFieldType(DataSourceFactory ds, String pgds)
	// throws Exception {
	// DataSource d = ds.getDataSource(pgds);
	//
	// d.start();
	//
	// int myFieldType = d.getDataSourceMetadata().getFieldType(0);
	// assertTrue(myFieldType == Value.INT);
	// myFieldType = d.getDataSourceMetadata().getFieldType(1);
	// assertTrue(myFieldType == PTTypes.GEOMETRY);
	//
	// d.stop();
	// }
	//
	// public void testgetFieldType() throws Exception {
	// testgetFieldType(ds, "pgWithGDBMS");
	// }
	//
	// /**
	// * DOCUMENT ME!
	// *
	// * @throws Exception
	// * DOCUMENT ME!
	// */
	// public static void testFieldCount(DataSourceFactory ds, String pgds)
	// throws Exception {
	// DataSource d = ds.getDataSource(pgds);
	//
	// d.start();
	//
	// int fieldCount = d.getDataSourceMetadata().getFieldCount();
	// assertTrue(fieldCount == 2);
	//
	// d.stop();
	// }
	//
	// public void testFieldCount() throws Exception {
	// testFieldCount(ds, "pgWithGDBMS");
	// }
	//
	// public static void testFullExtent(DataSourceFactory ds, String pgds)
	// throws DriverException, NoSuchTableException,
	// DataSourceCreationException {
	// SpatialDataSource d = (SpatialDataSource) ds.getDataSource(pgds);
	//
	// d.start();
	// Envelope ext = d.getFullExtent();
	// for (int i = 0; i < d.getRowCount(); i++) {
	// assertTrue(ext.contains(d.getGeometry(i).getEnvelopeInternal()));
	// }
	// d.stop();
	// }
	//
	// public void testFullExtent() throws Exception {
	// testFullExtent(ds, "pgWithGDBMS");
	// }
	//
	// public static void testWrite(DataSourceFactory ds, String pgds)
	// throws Exception {
	// DataSource d = ds.getDataSource(pgds);
	//
	// d.beginTrans();
	// String[] fieldNames = d.getFieldNames();
	// DataSource max = ds.executeSQL("select max(" + fieldNames[0] +") from " +
	// d.getName());
	// max.start();
	// int maxValue = max.getInt(0, 0);
	// max.stop();
	// long countBefore = d.getRowCount();
	// d.insertFilledRow(new Value[] { ValueFactory.createValue(maxValue+1),
	// ValueFactory.createValue(maxValue+1),
	// ValueFactory.createValue(gf.createPoint(new Coordinate(0, 0))) });
	// d.commitTrans();
	//
	// d.start();
	// assertTrue(d.getRowCount() - 1 == countBefore);
	// d.stop();
	// }
	//
	// public void testWrite() throws Exception {
	// testWrite(ds, "points0");
	// }
	//
	// public void testCreateTable() throws Exception {
	// String cs =
	// "jdbc:postgresql://127.0.0.1:5432/orbiscad?user=postgres&password=";
	//
	// Connection c = java.sql.DriverManager.getConnection(cs);
	// Statement s = c.createStatement();
	// try {
	// s
	// .execute("DROP TABLE nueva;delete from geometry_columns where
	// f_table_name = 'nueva';");
	// } catch (SQLException e) {
	// }
	// s.close();
	// c.close();
	//
	// DBSpatialSource source = new DBSpatialSource("127.0.0.1", 5432,
	// "orbiscad", "postgres", "", "nueva", "geom", "jdbc:postgresql");
	// DefaultSpatialDriverMetadata dsdm = new DefaultSpatialDriverMetadata();
	// dsdm.addSpatialField("geom", SpatialDataSource.ANY);
	// dsdm.addField("field", "INTEGER");
	// dsdm.setPrimaryKey(new String[] { "field" });
	// DBSourceCreation dbsc = new DBSourceCreation(source, dsdm);
	// ds.createDataSource(dbsc);
	// DataSource d = ds.getSpatialDataSource(source);
	// d.start();
	// d.stop();
	// }
	//
	// public void testCreateTableFailedNoPK() throws Exception {
	// String cs =
	// "jdbc:postgresql://127.0.0.1:5432/orbiscad?user=postgres&password=";
	//
	// Connection c = java.sql.DriverManager.getConnection(cs);
	// Statement s = c.createStatement();
	// try {
	// s
	// .execute("DROP TABLE nueva;delete from geometry_columns where
	// f_table_name = 'nueva';");
	// } catch (SQLException e) {
	// }
	// s.close();
	// c.close();
	//
	// DBSpatialSource source = new DBSpatialSource("127.0.0.1", 5432,
	// "orbiscad", "postgres", "", "nueva", "geom", "jdbc:postgresql");
	// DefaultSpatialDriverMetadata dsdm = new DefaultSpatialDriverMetadata();
	// dsdm.addSpatialField("geom", SpatialDataSource.ANY);
	// dsdm.addField("field", "INTEGER");
	// DBSourceCreation dbsc = new DBSourceCreation(source, dsdm);
	// try {
	// ds.createDataSource(dbsc);
	// assertTrue(false);
	// } catch (DriverException e) {
	// assertTrue(true);
	// }
	// }
	//
	// public void testCreateAndEdit() throws Exception {
	// String cs =
	// "jdbc:postgresql://127.0.0.1:5432/orbiscad?user=postgres&password=";
	//
	// Connection c = java.sql.DriverManager.getConnection(cs);
	// Statement s = c.createStatement();
	// try {
	// s
	// .execute("DROP TABLE nueva;delete from geometry_columns where
	// f_table_name = 'nueva';");
	// } catch (SQLException e) {
	// }
	// s.close();
	// c.close();
	//
	// DBSpatialSource source = new DBSpatialSource("127.0.0.1", 5432,
	// "orbiscad", "postgres", "", "nueva", "geom", "jdbc:postgresql");
	// DefaultSpatialDriverMetadata dsdm = new DefaultSpatialDriverMetadata();
	// dsdm.addSpatialField("geom", SpatialDataSource.POINT);
	// dsdm.addField("field", "CHAR");
	// dsdm.setPrimaryKey(new String[] { "field" });
	// DBSourceCreation dbsc = new DBSourceCreation(source, dsdm);
	// ds.createDataSource(dbsc);
	// SpatialDataSource d = ds.getSpatialDataSource(source);
	// d.beginTrans();
	// assertTrue(d.getSpatialFieldIndex() == 1);
	// d.insertEmptyRow();
	// d.setFieldValue(0, 1, ValueFactory.createValue(gf.createPoint(new
	// Coordinate(0, 0))));
	// d.setFieldValue(0, 0, ValueFactory.createValue("a"));
	// d.commitTrans();
	// }
	//
	// public void testCreateAndEditAllFieldTypes() throws Exception {
	// String cs =
	// "jdbc:postgresql://127.0.0.1:5432/orbiscad?user=postgres&password=";
	//
	// Connection c = java.sql.DriverManager.getConnection(cs);
	// Statement s = c.createStatement();
	// try {
	// s
	// .execute("DROP TABLE nueva;delete from geometry_columns where
	// f_table_name = 'nueva';");
	// } catch (SQLException e) {
	// }
	// s.close();
	// c.close();
	//
	// DBSpatialSource source = new DBSpatialSource("127.0.0.1", 5432,
	// "orbiscad", "postgres", "", "nueva", "geom", "jdbc:postgresql");
	// DefaultSpatialDriverMetadata dsdm = new DefaultSpatialDriverMetadata();
	// dsdm.addSpatialField("geom", SpatialDataSource.POINT);
	// String[] types = new PostGISDriver().getAvailableTypes();
	// for (int i = 0; i < types.length; i++) {
	// dsdm.addField("f" + i, types[i]);
	// }
	// /*
	// * CHAR, VARCHAR, LONGVARCHAR, BIGINT, BOOLEAN, BIT, DATE, DECIMAL,
	// * NUMERIC, FLOAT, DOUBLE, INTEGER, REAL, SMALLINT, TINYINT, BINARY,
	// * VARBINARY, LONGVARBINARY, TIMESTAMP, TIME
	// */
	// dsdm.setPrimaryKey(new String[] { "f1" });
	// DBSourceCreation dbsc = new DBSourceCreation(source, dsdm);
	// ds.createDataSource(dbsc);
	// SpatialDataSource d = ds.getSpatialDataSource(source);
	// d.beginTrans();
	// assertTrue(d.getSpatialFieldIndex() == 16);
	// d.insertFilledRow(new Value[] { ValueFactory.createValue("a"), // CHAR
	// ValueFactory.createValue("b"), // VARCHAR
	// ValueFactory.createValue(123123L), // BIGINT
	// ValueFactory.createValue(true), // BOOLEAN
	// ValueFactory.createValue(true), // BIT
	// ValueFactory.createValue(new Date()), // DATE
	// ValueFactory.createValue(1.4), // DECIMAL
	// ValueFactory.createValue(1.4), // NUMERIC
	// ValueFactory.createValue(1.4), // FLOAT
	// ValueFactory.createValue(1.4), // DOUBLE PRECISION
	// ValueFactory.createValue(1), // INTEGER
	// ValueFactory.createValue(1.4f), // REAL
	// ValueFactory.createValue((short) 4), // SMALLINT
	// ValueFactory.createValue(new byte[] { (byte) 3, (byte) 250 }), // BYTEA
	// ValueFactory.createValue(new Timestamp(4L)), // TIMESTAMP
	// ValueFactory.createValue(new Time(3L)), // TIME
	// ValueFactory.createValue(gf.createPoint(new Coordinate(0, 0))) });
	// d.commitTrans();
	//
	// d.start();
	// assertTrue(((BooleanValue) d.getFieldValue(0, 4)).getValue());
	// d.stop();
	// }
	//
}
