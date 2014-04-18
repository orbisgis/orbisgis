/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV FR CNRS 2488
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.drivers;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.io.WKTReader;
import org.gdms.Geometries;
import org.gdms.TestBase;
import org.gdms.TestResourceHandler;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreation;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.file.FileSourceCreation;
import org.gdms.data.memory.MemorySourceDefinition;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.types.Dimension3DConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.GeometryCollectionValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.memory.MemoryDataSetDriver;
import org.gdms.driver.shapefile.IndexFile;
import org.gdms.driver.shapefile.ShapeType;
import org.gdms.driver.shapefile.ShapefileDriver;
import org.gdms.driver.shapefile.ShapefileException;
import org.gdms.driver.shapefile.ShapefileHeader;
import org.gdms.driver.shapefile.ShapefileReader;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.cts.crs.CoordinateReferenceSystem;
import org.gdms.data.types.CRSConstraint;

import static org.junit.Assert.*;
import org.orbisgis.utils.FileUtils;

public class ShapefileDriverTest extends TestBase {

        private SimpleDateFormat sdf;
        private WKTReader wktReader;

        @Before
        public void setUp() throws Exception {
                super.setUpTestsWithoutEdition();
                sdf = new SimpleDateFormat("yyyy-MM-dd");
                wktReader = new WKTReader();
        }

        @Test
        public void testOpenShapeWithDifferentCase() throws Exception {
                // should it fail if different case ? I say yes...
                DataSource ds = dsf.getDataSource(new File(TestResourceHandler.OTHERRESOURCES, "multipolygon2d.Shp"));
                try {
                        ds.open();
                        ds.close();
                        fail();
                } catch (DriverException ex) {
                }
        }

        @Test
        public void testBigShape() throws Exception {
                sm.register(
                        "big",
                        new FileSourceCreation(new File(TestResourceHandler.TESTRESOURCES, "landcover2000.shp"), null));
                DataSource ds = dsf.getDataSource("big");
                ds.open();
                ds.close();
        }

        /**
         * Load a ShapeFile that hold unclosed polygons, the driver should generate valid geometries
         */
        @Test
        public void testLoadInvalidPolygons() throws Exception {
            String sourceName = dsf.getSourceManager().nameAndRegister(getClass().getResource("invalid/invalid.shp").toURI());
            DataSource source = dsf.getDataSource(sourceName);
            source.open();
            try {
                assertTrue(source.getFieldValue(0,0).getAsGeometry().isValid());
            } finally {
                source.close();
            }
        }
        @Test
        public void testSaveEmptyGeometries() throws Exception {
                MemoryDataSetDriver omd = new MemoryDataSetDriver(
                        new String[]{"the_geom", "id"},
                        new Type[]{
                                TypeFactory.createType(Type.GEOMETRYCOLLECTION),
                                TypeFactory.createType(Type.STRING)
                        });
                sm.register("obj", new MemorySourceDefinition(omd, "main"));
                DataSource ds = dsf.getDataSource("obj");
                GeometryFactory gf = new GeometryFactory();
                ds.open();
                Value[] vals = new Value[]{
                        ValueFactory.createValue(gf.createMultiLineString(new LineString[0])),
                        ValueFactory.createValue("0")};
                ds.insertFilledRow(vals);
                ds.insertFilledRow(new Value[]{null, ValueFactory.createValue("1")});
                DataSourceCreation target = new FileSourceCreation(getTempFile(".shp"), null);
                sm.register("buffer", target);
                dsf.saveContents("buffer", ds);
                String contents = ds.getAsString();
                ds.close();

                DataSource otherDs = dsf.getDataSource("buffer");
                otherDs.open();
                assertEquals(2, otherDs.getRowCount());
                assertTrue(((GeometryCollectionValue) otherDs.getFieldValue(0, 0)).getAsGeometry().isEmpty());
                assertTrue(otherDs.isNull(1, 0));
                assertEquals(otherDs.getAsString(), contents);
                otherDs.close();
        }

        @Test
        public void testSaveHeterogeneousGeometries() throws Exception {
                MemoryDataSetDriver omd = new MemoryDataSetDriver(new String[]{"id",
                                "geom"}, new Type[]{TypeFactory.createType(Type.STRING),
                                TypeFactory.createType(Type.GEOMETRY)});
                sm.register("obj", new MemorySourceDefinition(omd, "main"));
                DataSourceCreation target = new FileSourceCreation(getTempFile(".shp"), null);
                DataSource ds = dsf.getDataSource("obj");
                ds.open();
                ds.insertFilledRow(new Value[]{ValueFactory.createValue("1"),
                                ValueFactory.createValue(Geometries.getPolygon()),});
                ds.insertFilledRow(new Value[]{ValueFactory.createValue("0"),
                                ValueFactory.createValue(Geometries.getPoint()),});
                try {
                        sm.register("buffer", target);
                        dsf.saveContents("buffer", ds);
                        fail();
                } catch (DriverException e) {
                }
                ds.close();
                ds.open();
                ds.insertFilledRow(new Value[]{ValueFactory.createValue("0"),
                            ValueFactory.createValue(Geometries.getPoint()),});
                ds.insertFilledRow(new Value[]{ValueFactory.createValue("1"),
                                ValueFactory.createValue(Geometries.getPolygon()),});
                try {
                        dsf.saveContents("buffer", ds);
                        fail();
                } catch (DriverException e) {
                }
                ds.close();
        }

        @Test
        public void testFieldNameTooLong() throws Exception {
                DefaultMetadata m = new DefaultMetadata();
                m.addField("thelongernameintheworld", Type.STRING);
                m.addField("", Type.POLYGON);
                File shpFile = getTempFile(".shp");
                dsf.createDataSource(new FileSourceCreation(shpFile, m));
                DataSource ds = dsf.getDataSource(shpFile);
                ds.open();
                assertEquals("thelongern", ds.getMetadata().getFieldName(1));
                ds.close();
        }

        @Test
        public void testNullStringValue() throws Exception {
                DefaultMetadata m = new DefaultMetadata();
                m.addField("string", Type.STRING);
                m.addField("int", Type.INT);
                m.addField("", Type.POLYGON);
                File shpFile = getTempFile(".shp");
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
                assertEquals(ds.getInt(0, "int"), 0);
        }

        @Test
        public void test2DReadWriteMultipolygon() throws Exception {
                Geometry geom = wktReader.read("MULTIPOLYGON ((( 107 113, 107 293, 368 293, 368 113, 107 113 )), (( 178 246, 178 270, 196 270, 196 246, 178 246 )))");
                test2DReadWrite(Type.MULTIPOLYGON, geom);
        }

        public void test2DReadWrite(int geometryType, Geometry geom)
                throws Exception {
                int nbCoords = geom.getCoordinates().length;
                DefaultMetadata m = new DefaultMetadata();
                m.addField("thelongernameintheworld", Type.STRING);
                m.addField("", geometryType,
                        new Dimension3DConstraint(2));
                File shpFile = getTempFile(".shp");
                dsf.createDataSource(new FileSourceCreation(shpFile, m));
                DataSource ds = dsf.getDataSource(shpFile);
                ds.open();
                ds.insertEmptyRow();

                ds.setFieldValue(0, 0, ValueFactory.createValue(geom));
                ds.commit();
                ds.close();
                ds.open();
                Geometry geomRes = ds.getFieldValue(0, 0).getAsGeometry();
                Coordinate[] coordinates = geomRes.getCoordinates();
                ds.close();
                assertEquals(nbCoords, coordinates.length);
        }

        @Test
        public void test3DReadWritePoint() throws Exception {
                test3DReadWrite(Type.POINT, Geometries.getPoint3D());
        }

        @Test
        public void test3DReadWriteLineString() throws Exception {
                test3DReadWrite(Type.MULTILINESTRING, Geometries.getMultilineString3D());
        }

        @Test
        public void test3DReadWritePolygon() throws Exception {
                GeometryFactory gf = new GeometryFactory();
                LinearRing lr = gf.createLinearRing(new Coordinate[]{
                                new Coordinate(0, 0, 20), new Coordinate(0, 10, 20),
                                new Coordinate(10, 10, 20), new Coordinate(10, 0, 20),
                                new Coordinate(0, 0, 20)});
                Polygon pol = gf.createPolygon(lr, null);
                MultiPolygon multiPol = gf.createMultiPolygon(new Polygon[]{pol});
                test3DReadWrite(Type.MULTIPOLYGON, multiPol);
        }

        @Test
        public void test3DReadWriteMultipoint() throws Exception {
                test3DReadWrite(Type.MULTIPOINT, Geometries.getMultiPoint3D());
        }

        public void test3DReadWrite(int geometryType, Geometry geom)
                throws Exception {
                DefaultMetadata m = new DefaultMetadata();
                m.addField("thelongernameintheworld", Type.STRING);
                m.addField("", geometryType,
                        new Dimension3DConstraint(3));
                File shpFile = getTempFile(".shp");
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

        @Test
        public void testNoConstraintWith3DGeom2SHP() throws Exception {
                MemoryDataSetDriver omd = new MemoryDataSetDriver(
                        new String[]{"geom"}, new Type[]{TypeFactory.createType(Type.GEOMETRY)});
                omd.addValues(new Value[]{ValueFactory.createValue(new GeometryFactory().createPoint(new Coordinate(
                            2, 2, 2)))});
                DataSource ds = dsf.getDataSource(omd, "main");

                File shpFile = getTempFile(".shp");
                sm.register("shp", shpFile);
                dsf.saveContents("shp", ds);
                ds = dsf.getDataSource("shp");
                ds.open();
                Coordinate coord = ds.getFieldValue(0, 0).getAsGeometry().getCoordinate();
                ds.close();
                assertEquals(coord.z, 2, 0);
        }

        @Test
        public void testWrongTypeForDBF() throws Exception {
                DefaultMetadata m = new DefaultMetadata();
                m.addField("id", Type.TIMESTAMP);
                m.addField("", Type.POINT,
                        new Dimension3DConstraint(3));
                File shpFile = getTempFile(".shp");
                try {
                        dsf.createDataSource(new FileSourceCreation(shpFile, m));
                        fail();
                } catch (DriverException e) {
                }
        }

        @Test
        public void testAllTypes() throws Exception {
                DefaultMetadata m = new DefaultMetadata();
                m.addField("the_geom", Type.POINT,
                        new Dimension3DConstraint(3));
                m.addField("f1", Type.BOOLEAN);
                m.addField("f2", Type.BYTE);
                m.addField("f3", Type.DATE);
                m.addField("f4", Type.DOUBLE);
                m.addField("f5", Type.FLOAT);
                m.addField("f6", Type.INT);
                m.addField("f7", Type.LONG);
                m.addField("f8", Type.SHORT);
                m.addField("f9", Type.STRING);

                File shpFile = getTempFile(".shp");

                dsf.createDataSource(new FileSourceCreation(shpFile, m));
                DataSource ds = dsf.getDataSource(shpFile);
                ds.open();
                assertEquals(m.getFieldType(0).getTypeCode(), Type.POINT);
                assertEquals(m.getFieldType(1).getTypeCode(), Type.BOOLEAN);
                assertEquals(m.getFieldType(2).getTypeCode(), Type.BYTE);
                assertEquals(m.getFieldType(3).getTypeCode(), Type.DATE);
                assertEquals(m.getFieldType(4).getTypeCode(), Type.DOUBLE);
                assertEquals(m.getFieldType(5).getTypeCode(), Type.FLOAT);
                assertEquals(m.getFieldType(6).getTypeCode(), Type.INT);
                assertEquals(m.getFieldType(7).getTypeCode(), Type.LONG);
                assertEquals(m.getFieldType(8).getTypeCode(), Type.SHORT);
                assertEquals(m.getFieldType(9).getTypeCode(), Type.STRING);
                ds.commit();
                ds.close();
        }

        // SEE THE GT BUG REPORT :
        // http://jira.codehaus.org/browse/GEOT-1268
        @Test
        public void testReadAndWriteDBF() throws Exception {
                File file = getTempCopyOf(new File(TestResourceHandler.TESTRESOURCES, "alltypes.dbf"));
                DataSource ds = dsf.getDataSource(file);
                for (int i = 0; i < 2; i++) {
                        ds.open();
                        ds.insertFilledRow(new Value[]{ValueFactory.createValue(1),
                                        ValueFactory.createValue(2.4d),
                                        ValueFactory.createValue(2556),
                                        ValueFactory.createValue("sadkjsr"),
                                        ValueFactory.createValue(sdf.parse("1980-7-23")),
                                        ValueFactory.createValue(true)});
                        ds.commit();
                        ds.close();
                }
                ds.open();
                String content = ds.getAsString();
                ds.commit();
                ds.close();
                ds.open();
                assertEquals(content, ds.getAsString());
                ds.commit();
                ds.close();
        }

        @Test
        public void testReadAndWriteSHP() throws Exception {
                DataSource ds = dsf.getDataSource(getTempCopyOf(new File(
                            TestResourceHandler.TESTRESOURCES, "alltypes.shp")));
                GeometryFactory gf = new GeometryFactory();
                for (int i = 0; i < 2; i++) {
                        ds.open();
                        ds.insertFilledRow(new Value[]{
                                        ValueFactory.createValue(gf.createPoint(new Coordinate(10,
                                        10))), ValueFactory.createValue(1),
                                        ValueFactory.createValue(3.4d),
                                        ValueFactory.createValue(2556),
                                        ValueFactory.createValue("sadkjsr"),
                                        ValueFactory.createValue(sdf.parse("1980-7-23")),
                                        ValueFactory.createValue(true)});
                        ds.commit();
                        ds.close();
                }
                ds.open();
                String content = ds.getAsString();
                ds.commit();
                ds.close();
                ds.open();
                assertEquals(content, ds.getAsString());
                ds.commit();
                ds.close();
        }

        @Test
        public void testSHPGeometryWKB() throws Exception {
                File file = new File(TestResourceHandler.TESTRESOURCES, "hedgerow.shp");
                DataSource ds = dsf.getDataSource(file);
                ds.open();
                Value geom = ds.getFieldValue(0, 0);
                byte[] wkb = geom.getBytes();
                Value read = ValueFactory.createValue(Type.GEOMETRY, wkb);
                ds.close();
                assertTrue(read.equals(geom).getAsBoolean());
        }

        @Test
        public void testNullDates() throws Exception {
                DefaultMetadata m = new DefaultMetadata();
                m.addField("geom", TypeFactory.createType(Type.LINESTRING));
                m.addField("date", Type.DATE);
                DataSourceCreation dsc = new FileSourceCreation(new File(dsf.getTempFile()
                        + ".shp"), m);
                sm.register("sample", dsf.createDataSource(dsc));
                DataSource ds = dsf.getDataSource("sample");
                ds.open();
                ds.insertFilledRow(new Value[]{ValueFactory.createNullValue(),
                                ValueFactory.createNullValue()});
                ds.commit();
                ds.close();

                ds.open();
                Date date = ds.getDate(0, 0);
                ds.close();
                assertNull(date);
        }

        @Test
        public void testSaveSQL() throws Exception {
                sm.register("shape", new File(TestResourceHandler.TESTRESOURCES, "landcover2000.shp"));

                DataSource sql = dsf.getDataSourceFromSQL(
                        "select st_Buffer(the_geom, 20) from shape;",
                        DataSourceFactory.DEFAULT);
                DataSourceCreation target = new FileSourceCreation(getTempFile(".shp"), null);
                sm.register("buffer", target);
                sql.open();
                dsf.saveContents("buffer", sql);
                sql.close();

                DataSource ds = dsf.getDataSource("buffer");
                ds.open();
                sql.open();
                assertEquals(ds.getRowCount(), sql.getRowCount());
                sql.close();
                ds.close();
        }

        @Test
        public void test2DTypeKept() throws Exception {
                sm.register("tokeep", new File(TestResourceHandler.TESTRESOURCES, "landcover2000.shp"));

                DataSource sql = dsf.getDataSourceFromSQL(
                        "select * from tokeep;",
                        DataSourceFactory.DEFAULT);
                File out = getTempFile(".shp");
                DataSourceCreation target = new FileSourceCreation(out, null);

                sm.register("buffer", target);
                sql.open();
                dsf.saveContents("buffer", sql);
                FileInputStream fis = new FileInputStream(out);
                ShapefileHeader sfh = ShapefileReader.readHeader(fis.getChannel());
                assertTrue(sfh.getShapeType().id == ShapeType.POLYGON.id);
                sql.close();
        }
        
        @Test
        public void testCreatePrj() throws Exception {
                CoordinateReferenceSystem crs = DataSourceFactory.getCRSFactory().getCRS("EPSG:27572");
                MemoryDataSetDriver omd = new MemoryDataSetDriver(
                        new String[]{"geom"}, new Type[]{TypeFactory.createType(Type.GEOMETRY, new CRSConstraint(crs))});                
                omd.addValues(new Value[]{ValueFactory.createValue(new GeometryFactory().createPoint(new Coordinate(
                            2, 2, 2)))});
                DataSource ds = dsf.getDataSource(omd, "main");

                File shpFile = getTempFile(".shp");
                sm.register("shp", shpFile);
                dsf.saveContents("shp", ds);
                
                File prj = FileUtils.getFileWithExtension(shpFile, "prj");                
                assertNotNull(prj);                
        }


        @Test
        public void testMultiPatchShp() throws Exception {
            try (FileInputStream shpFis = new FileInputStream(
                    ShapefileDriverTest.class.getResource("First_ring_inside_ring.shp").getFile());
                 FileInputStream shxFis = new FileInputStream(
                         ShapefileDriverTest.class.getResource("First_ring_inside_ring.shp").getFile())) {
                ShapefileReader shapefileDriver = new ShapefileReader(shpFis.getChannel());
                IndexFile shxFile = new IndexFile(shxFis.getChannel());
                ShapefileHeader header = shapefileDriver.getHeader();
                assertEquals(ShapeType.MULTIPATCH.id,header.getShapeType().id);
                assertEquals(1, shxFile.getRecordCount());
                Geometry geom = shapefileDriver.geomAt(shxFile.getOffset(0));
                assertTrue(geom instanceof MultiPolygon);
            }
        }
        
}