/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer, Maxence LAURENT,
 * computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
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
package org.gdms.spatial;

import java.io.File;
import java.util.Iterator;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import org.gdms.TestBase;
import org.gdms.TestResourceHandler;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.DigestUtilities;
import org.gdms.data.file.FileSourceCreation;
import org.gdms.data.indexes.DefaultSpatialIndexQuery;
import org.gdms.data.indexes.IndexManager;
import org.gdms.data.indexes.IndexQuery;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.LengthConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DataSet;
import org.gdms.driver.DriverException;
import org.gdms.driver.memory.MemoryDataSetDriver;


public class SpatialEditionTest extends TestBase {

        private GeometryFactory gf = new GeometryFactory();
        
        @Before
        public void setUp() throws Exception {
                super.setUpTestsWithEdition(false);
        }

        private boolean contains(DataSource sds,
                Iterator<Integer> list, Geometry geometry) throws DriverException {
                while (list.hasNext()) {
                        Integer dir = list.next();
                        if (TestBase.equals(
                                sds.getFieldValue(dir, sds.getSpatialFieldIndex()),
                                ValueFactory.createValue(geometry))) {
                                return true;
                        }
                }

                return false;
        }

        @Test
        public void testIndex() throws Exception {
                String dsName = "toto";
                sm.register(dsName, super.getAnySpatialResource());

                DataSource d = dsf.getDataSource(dsName);
                d.open();
                String spatialField = d.getFieldName(d.getSpatialFieldIndex());
                int max = d.getRowCount() > 50 ? 50 : (int) d.getRowCount();
                Geometry[] geometries = new Geometry[max];
                for (int i = 0; i < max; i++) {
                        geometries[i] = d.getGeometry(i);
                }
                d.close();

                Envelope[] bounds = new Envelope[geometries.length];
                for (int i = 0; i < bounds.length; i++) {
                        bounds[i] = geometries[i].getEnvelopeInternal();
                }
                dsf.getIndexManager().buildIndex(dsName, spatialField, IndexManager.RTREE_SPATIAL_INDEX, null);
                d = dsf.getDataSource(dsName);

                d.open();
                int sfi = d.getSpatialFieldIndex();
                long rc = d.getRowCount();
                Value[] row = d.getRow(0);

                for (int i = 0; i < geometries.length; i++) {
                        row[sfi] = ValueFactory.createValue(geometries[i]);
                        d.insertFilledRow(nullifyAutoIncrement(d, row));
                }

                for (int i = 0; i < geometries.length; i++) {
                        assertTrue(contains(d, d.queryIndex(new DefaultSpatialIndexQuery(
                                bounds[i], spatialField)),
                                geometries[i]));
                }

                for (int i = 0; i < geometries.length; i++) {
                        d.setFieldValue(rc + i, sfi, ValueFactory.createValue(geometries[geometries.length - i - 1]));
                }

                for (int i = 0; i < geometries.length; i++) {
                        assertTrue(contains(d, d.queryIndex(new DefaultSpatialIndexQuery(
                                bounds[i], spatialField)),
                                geometries[geometries.length - i - 1]));
                }

                for (int i = 0; i < geometries.length; i++) {
                        d.deleteRow(rc + i);
                }

                d.close();
        }

        @Test
        public void testManyDeleteIndexedEdition() throws Exception {
                String dsName = "toto";
                sm.register(dsName, getTempCopyOf(new File(TestResourceHandler.TESTRESOURCES, "hedgerow.shp")));

                DataSource d = dsf.getDataSource(dsName);
                d.open();
                String spatialField = d.getFieldName(d.getSpatialFieldIndex());
                d.close();


                dsf.getIndexManager().buildIndex(dsName,
                        spatialField,
                        IndexManager.RTREE_SPATIAL_INDEX, null);

                d = dsf.getDataSource(dsName);

                d.open();
                long rc = d.getRowCount();
                Envelope e = d.getFullExtent();
                d.deleteRow(0);
                d.deleteRow(0);
                DefaultSpatialIndexQuery query = new DefaultSpatialIndexQuery(e, spatialField);
                assertEquals(rc - 2, count(d.queryIndex(query)));
                d.commit();
                d.close();

                d.open();
                assertEquals(rc - 2, d.getRowCount());
                d.close();

                d.open();
                rc = d.getRowCount();
                e = d.getFullExtent();
                d.insertFilledRowAt(0, nullifyAutoIncrement(d, d.getRow(0)));
                d.deleteRow(1);
                query = new DefaultSpatialIndexQuery(e, spatialField);
                assertEquals(rc, count(d.queryIndex(query)));
                d.commit();
                d.close();

                d.open();
                assertEquals(rc, d.getRowCount());
                d.close();
        }

        private long count(Iterator<Integer> iter) {
                int count = 0;
                while (iter.hasNext()) {
                        iter.next();
                        count++;

                }
                return count;
        }

        @Test
        public void testIndexedEdition() throws Exception {
                String dsName = "toto";
                sm.register(dsName, getTempCopyOf(new File(TestResourceHandler.TESTRESOURCES, "hedgerow.shp")));

                DataSource d = dsf.getDataSource(dsName);
                d.open();
                String spatialField = d.getFieldName(d.getSpatialFieldIndex());
                d.close();

                dsf.getIndexManager().buildIndex(dsName,
                        spatialField,
                        IndexManager.RTREE_SPATIAL_INDEX, null);
                d = dsf.getDataSource(dsName);

                d.open();
                long originalRowCount = d.getRowCount();
                Envelope e = d.getFullExtent();
                DefaultSpatialIndexQuery query = new DefaultSpatialIndexQuery(e, spatialField);
                d.deleteRow(0);
                assertEquals(count(d.queryIndex(query)), originalRowCount - 1);
                d.insertEmptyRowAt(1);
                assertTrue(d.isNull(1, 0));
                d.deleteRow(1);
                d.insertFilledRowAt(1, nullifyAutoIncrement(d, d.getRow(0)));
                assertTrue(d.getFieldValue(1, 1).equals(d.getFieldValue(0, 1)).getAsBoolean());
                assertEquals(count(d.queryIndex(query)), originalRowCount);
                d.deleteRow(1);
                assertEquals(count(d.queryIndex(query)), originalRowCount - 1);
                d.commit();
                d.close();
                d.open();
                assertEquals(d.getRowCount(), originalRowCount - 1);
                assertEquals(count(d.queryIndex(query)), originalRowCount - 1);
                d.close();
        }

        @Test
        public void testAdd() throws Exception {
                String dsName = "toto";
                sm.register(dsName, getTempCopyOf(new File(TestResourceHandler.TESTRESOURCES, "hedgerow.shp")));

                DataSource d = dsf.getDataSource(dsName);
                d.open();
                String spatialField = d.getFieldName(d.getSpatialFieldIndex());
                Geometry geom = d.getGeometry(0);
                d.close();

                d = dsf.getDataSource(dsName);

                d.open();

                long previousRowCount = d.getRowCount();
                byte[] digest = DigestUtilities.getDigest(d);
                Value nv2 = d.getFieldValue(0, 1);
                d.insertEmptyRow();
                d.setFieldValue(d.getRowCount() - 1, d.getFieldIndexByName(spatialField), ValueFactory.createValue(geom));
                d.setFieldValue(d.getRowCount() - 1, 1, nv2);
                d.commit();
                d.close();

                d = dsf.getDataSource(dsName);
                d.open();
                byte[] secondDigest = DigestUtilities.getDigest(d, previousRowCount);
                DigestUtilities.equals(digest, secondDigest);
                assertEquals(d.getRowCount(), previousRowCount + 1);
                assertTrue(d.getGeometry(previousRowCount).equals(geom));
                assertTrue(d.getFieldValue(previousRowCount, 1).equals(nv2).getAsBoolean());
                d.close();
        }

        @Test
        public void testBigFileCreation() throws Exception {
                File shpFile = getTempFile(".shp");
                DefaultMetadata dsdm = new DefaultMetadata();
                dsdm.addField("geom", Type.LINESTRING);
                dsdm.addField("text", Type.STRING, new LengthConstraint(10));

                dsf.createDataSource(new FileSourceCreation(shpFile, dsdm));

                String dsName = "big";
                dsf.getSourceManager().register(dsName, new FileSourceCreation(shpFile, null));

                DataSource d = dsf.getDataSource(dsName);

                d.open();
                Coordinate[] coords = new Coordinate[3];
                coords[0] = new Coordinate(0, 0);
                coords[1] = new Coordinate(10, 10);
                coords[2] = new Coordinate(10, 15);
                Geometry geom = gf.createMultiLineString(new LineString[]{gf.createLineString(coords)});
                Value nv2 = ValueFactory.createValue("3.0");
                int n = 10000;
                for (int i = 0; i < n; i++) {
                        d.insertEmptyRow();
                        d.setFieldValue(d.getRowCount() - 1, 0, ValueFactory.createValue(geom));
                        d.setFieldValue(d.getRowCount() - 1, 1, nv2);
                }
                d.commit();
                d.close();

                d = dsf.getDataSource(dsName);
                d.open();
                assertEquals(d.getRowCount(), n);
                for (int i = 0; i < n; i++) {
                        Geometry readGeom = d.getGeometry(i);
                        assertEquals(readGeom, geom);
                        assertEquals(d.getFieldValue(i, 1), nv2);
                }
                d.close();
        }

        @Test
        public void testIsModified() throws Exception {
                DataSource d = dsf.getDataSource(new File(TestResourceHandler.TESTRESOURCES, "hedgerow.shp"));

                d.open();
                assertFalse(d.isModified());
                d.insertEmptyRow();
                assertTrue(d.isModified());
                d.close();

                d.open();
                assertFalse(d.isModified());
                d.insertFilledRow(d.getRow(0));
                assertTrue(d.isModified());
                d.close();

                d.open();
                assertFalse(d.isModified());
                d.removeField(1);
                assertTrue(d.isModified());
                d.close();

                d.open();
                assertFalse(d.isModified());
                d.addField("name", d.getMetadata().getFieldType(0));
                assertTrue(d.isModified());
                d.close();

                d.open();
                assertFalse(d.isModified());
                d.setFieldName(1, "asd");
                assertTrue(d.isModified());
                d.close();

                d.open();
                assertFalse(d.isModified());
                d.setFieldValue(0, 0, ValueFactory.createNullValue());
                assertTrue(d.isModified());
                d.close();

                DataSource ads = d;
                ads.open();
                assertFalse(ads.isModified());
                ads.deleteRow(0);
                assertTrue(ads.isModified());
                ads.close();

                ads.open();
                assertFalse(ads.isModified());
                ads.insertEmptyRowAt(0);
                assertTrue(ads.isModified());
                ads.close();

                ads.open();
                assertFalse(ads.isModified());
                ads.insertFilledRowAt(0, ads.getRow(0));
                assertTrue(ads.isModified());
                ads.close();

        }

        private boolean fullExtentContainsAll(DataSource sds)
                throws DriverException {
                Envelope fe = sds.getFullExtent();
                for (int i = 0; i < sds.getRowCount(); i++) {
                        if (!sds.isNull(i, sds.getSpatialFieldIndex())) {
                                if (!fe.contains(sds.getGeometry(i).getEnvelopeInternal())) {
                                        return false;
                                }
                        }
                }

                DefaultSpatialIndexQuery query = new DefaultSpatialIndexQuery(fe, sds.getFieldName(sds.getSpatialFieldIndex()));
                Iterator<Integer> it = sds.queryIndex(query);
                if (it != null) {
                        return count(it) == sds.getRowCount();
                }
                return true;
        }

        private Value getOutsideGeom(Type gc, double x, double y,
                double offset) {
                Geometry g = null;
                Point point = gf.createPoint(new Coordinate(x + offset, y + offset));
                LineString lineString = gf.createLineString(new Coordinate[]{
                                new Coordinate(x + offset, y + offset), new Coordinate(x, y)});
                LinearRing linearRing = gf.createLinearRing(new Coordinate[]{
                                new Coordinate(x, y), new Coordinate(x + offset, y + offset),
                                new Coordinate(x + offset, y), new Coordinate(x, y)});
                Polygon polygon = gf.createPolygon(linearRing, null);
                if ((gc == null) || (gc.getTypeCode() == Type.POINT)) {
                        g = point;
                } else if (gc.getTypeCode() == Type.MULTIPOINT) {
                        g = gf.createMultiPoint(new Point[]{point});
                } else if (gc.getTypeCode() == Type.LINESTRING) {
                        g = lineString;
                } else if (gc.getTypeCode() == Type.MULTILINESTRING) {
                        g = gf.createMultiLineString(new LineString[]{lineString});
                } else if (gc.getTypeCode() == Type.POLYGON) {
                        g = polygon;
                } else if (gc.getTypeCode() == Type.MULTIPOLYGON) {
                        g = gf.createMultiPolygon(new Polygon[]{polygon});
                } else {
                        throw new RuntimeException();
                }

                return ValueFactory.createValue(g);
        }

        @Test
        public void testEditedSpatialDataSourceFullExtentFile() throws Exception {
                String resource = "toto";
                sm.register(resource, getTempCopyOf(new File(TestResourceHandler.TESTRESOURCES, "hedgerow.shp")));

                DataSource d = dsf.getDataSource(resource);
                d.open();
                String spatialField = d.getFieldName(d.getSpatialFieldIndex());
                d.close();


                dsf.getIndexManager().buildIndex(resource, spatialField, IndexManager.RTREE_SPATIAL_INDEX, null);
                
                
                d = dsf.getDataSource(resource, DataSourceFactory.EDITABLE);
                d.open();
                int sfi = d.getSpatialFieldIndex();
                Envelope originalExtent = d.getFullExtent();

                Value[] row = d.getRow(0);
                double x = originalExtent.getMinX();
                double y = originalExtent.getMinY();
                Type gc = d.getFieldType(sfi);
                row[sfi] = getOutsideGeom(gc, x, y, -10);
                d.insertFilledRow(nullifyAutoIncrement(d, row));
                assertTrue(fullExtentContainsAll(d));

                d.setFieldValue(d.getRowCount() - 1, sfi, getOutsideGeom(gc, x,
                        y, -11));
                assertTrue(fullExtentContainsAll(d));

                d.setFieldValue(d.getRowCount() - 1, sfi, getOutsideGeom(gc, x, y, -9));
                assertTrue(fullExtentContainsAll(d));

                d.deleteRow(d.getRowCount() - 1);
                assertTrue(fullExtentContainsAll(d));

                d.undo();
                assertTrue(fullExtentContainsAll(d));

                d.redo();
                assertTrue(fullExtentContainsAll(d));
                
                d.commit();
                d.close();
        }

        @Test
        public void testIndexInRetrievedDataSource() throws Exception {
                String dsName = "toto";
                sm.register(dsName, super.getAnySpatialResource());
                
                DataSource d = dsf.getDataSource(dsName);
                d.open();
                String spatialField = d.getFieldName(d.getSpatialFieldIndex());
                d.close();
                
                dsf.getIndexManager().buildIndex(dsName,
                        spatialField,
                        IndexManager.RTREE_SPATIAL_INDEX, null);
                DataSource sds = dsf.getDataSource(dsName);
                sds.open();
                DefaultSpatialIndexQuery query = new DefaultSpatialIndexQuery(sds.getFullExtent(), spatialField);

                Iterator<Integer> it = sds.queryIndex(query);
                assertEquals(count(it), sds.getRowCount());
        }

        @Test
        public void testUpdateScope() throws Exception {
                String dsName = "toto";
                sm.register(dsName, getTempCopyOf(super.getAnySpatialResource()));
                
                DataSource d = dsf.getDataSource(dsName);
                d.open();
                String spatialField = d.getFieldName(d.getSpatialFieldIndex());
                d.close();
                
                dsf.getIndexManager().buildIndex(dsName,
                        spatialField,
                        IndexManager.RTREE_SPATIAL_INDEX, null);
                
                
                d = dsf.getDataSource(dsName);
                d.open();
                Number[] scope = d.getScope(DataSet.X);
                for (int i = 0; i < d.getRowCount(); i++) {
                        d.deleteRow(0);
                }
                Number[] newScope = d.getScope(DataSet.X);
                assertTrue((scope[0] != newScope[0]) || (scope[1] != newScope[1]));
        }

        @Test
        public void testNullValuesDuringEdition() throws Exception {
                String dsName = "toto";
                sm.register(dsName, getTempCopyOf(super.getAnySpatialResource()));
                
                DataSource d = dsf.getDataSource(dsName);
                d.open();
                String spatialField = d.getFieldName(d.getSpatialFieldIndex());
                d.close();
                
                dsf.getIndexManager().buildIndex(dsName,
                        spatialField,
                        IndexManager.RTREE_SPATIAL_INDEX, null);
                
                
                d = dsf.getDataSource(dsName);
                
                d.open();
                int fieldIndexByName = d.getFieldIndexByName(spatialField);
                d.setFieldValue(0, fieldIndexByName, null);
                d.insertFilledRow(new Value[d.getFieldCount()]);
                assertTrue(d.getFieldValue(0, fieldIndexByName).isNull());
                assertTrue(d.getFieldValue(d.getRowCount() - 1, fieldIndexByName).isNull());
                d.close();
        }

        @Test
        public void testCommitIndex() throws Exception {
                MemoryDataSetDriver omd = new MemoryDataSetDriver(
                        new String[]{"geom"}, new Type[]{TypeFactory.createType(Type.GEOMETRY)});
                Point p1 = gf.createPoint(new Coordinate(10, 10));
                Point p2 = gf.createPoint(new Coordinate(20, 20));
                omd.addValues(new Value[]{ValueFactory.createValue(p1)});
                omd.addValues(new Value[]{ValueFactory.createValue(p2)});
                DataSource sds = dsf.getDataSource(omd, "main");
                dsf.getIndexManager().buildIndex(sds.getName(), "geom",
                        IndexManager.RTREE_SPATIAL_INDEX, null);
                sds.open();
                Envelope extent = sds.getFullExtent();
                Geometry pointOutside = gf.createPoint(new Coordinate(
                        extent.getMinX() - 15, extent.getMinY() - 15));
                Value[] row = sds.getRow(0);
                row[sds.getSpatialFieldIndex()] = ValueFactory.createValue(pointOutside);
                sds.insertFilledRow(row);
                sds.commit();
                sds.close();

                sds = dsf.getDataSource(sds.getName());
                sds.open();
                IndexQuery query = new DefaultSpatialIndexQuery(sds.getFullExtent(),
                        "geom");
                assertEquals(count(sds.queryIndex(query)), sds.getRowCount());
                sds.close();
        }

        private Value[] nullifyAutoIncrement(DataSource ds, Value[] row)
                throws DriverException {
                Value[] ret = new Value[row.length];
                for (int i = 0; i < ds.getFieldCount(); i++) {
                        if (ds.getFieldType(i).getBooleanConstraint(
                                Constraint.AUTO_INCREMENT)) {
                                ret[i] = ValueFactory.createNullValue();
                        } else {
                                ret[i] = row[i];
                        }
                }

                return ret;
        }
}
