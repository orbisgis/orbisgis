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
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
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
package org.gdms.data;

import java.io.File;
import java.util.List;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import org.gdms.TestBase;
import org.gdms.TestResourceHandler;

public class FilterDataSourceDecoratorTest extends TestBase {

        @Before
        public void setUp() throws Exception {
                super.setUpTestsWithoutEdition();
        }

        @Test
        public void testFilterDecorator() throws Exception {
                sm.register("hedgerow", new File(TestResourceHandler.TESTRESOURCES, "hedgerow.shp"));
                DataSource original = dsf.getDataSource("hedgerow");
                FilterDataSourceDecorator decorator = new FilterDataSourceDecorator(
                        original);
                decorator.setFilter("\"type\" = 'talus'");

                original.open();
                decorator.open();

                assertEquals(original.getFieldCount(), decorator.getFieldCount());

                for (int i = 0; i < original.getMetadata().getFieldCount(); i++) {
                        assertEquals(original.getFieldName(i), decorator.getFieldName(i));
                }

                int cols = original.getFieldCount();

                for (int i = 0; i < decorator.getRowCount() && i < 10000; i++) {
                        long o = decorator.getOriginalIndex(i);
                        assertTrue(decorator.getFieldValue(i, decorator.getFieldIndexByName("type")).toString().equals(
                                "talus"));
                        for (int j = 0; j < cols; j++) {
                                assertTrue(decorator.getFieldValue(i, j).equals(original.getFieldValue(o, j)).getAsBoolean());
                        }
                }

                List<Integer> map = decorator.getIndexMap();
                for (int i = 0; i < map.size(); i++) {
                        assertTrue(decorator.getFieldValue(i, decorator.getFieldIndexByName("type")).toString().equals(
                                "talus"));
                        for (int j = 0; j < cols; j++) {
                                assertTrue(decorator.getFieldValue(i, j).equals(original.getFieldValue(map.get(i), j)).getAsBoolean());
                        }
                }

                decorator.close();
                original.close();
        }

        @Test
        public void testEditableListener() throws Exception {
                sm.register("hedgerow", new File(TestResourceHandler.TESTRESOURCES, "hedgerow.shp"));
                dsf.executeSQL("CREATE TABLE test AS SELECT * FROM hedgerow;");
                DataSource original = dsf.getDataSource("test", DataSourceFactory.EDITABLE);

                FilterDataSourceDecorator decorator = new FilterDataSourceDecorator(original);
                decorator.setFilter("\"type\" = 'talus'");
                decorator.open();
                long rowC = decorator.getRowCount();
                assertFalse(rowC == 0);

                original.open();
                original.deleteRow(decorator.getOriginalIndex(0));
                original.commit();
                original.close();

                assertFalse(rowC == decorator.getRowCount());
                assertEquals(rowC - 1, decorator.getRowCount());
                rowC = decorator.getRowCount();

                original.open();
                original.setString(decorator.getOriginalIndex(1), "type", "talus2");
                original.commit();
                original.close();

                assertFalse(rowC == decorator.getRowCount());
                assertEquals(rowC - 1, decorator.getRowCount());
                decorator.close();
        }
        
        @Test
        public void testFilterWithOrder() throws Exception {
                sm.register("hedgerow", new File(TestResourceHandler.TESTRESOURCES, "hedgerow.shp"));
                
                FilterDataSourceDecorator decorator = new FilterDataSourceDecorator(dsf.getDataSource("hedgerow"));
                decorator.setFilter("\"type\" = 'talus'");
                decorator.setOrder("gid DESC");
                decorator.open();
                long rowC = decorator.getRowCount();
                assertFalse(rowC == 0);
                
                int lastgid = Integer.MAX_VALUE;
                for (int i = 0; i < rowC; i++) {
                        int d = decorator.getInt(i, 1);
                        assertTrue(d <= lastgid);
                        lastgid = d;
                }
                
                decorator.close();
        }

        @Test
        public void testSpatialFilter() throws Exception {

                sm.register("landcover2000", new File(TestResourceHandler.TESTRESOURCES, "landcover2000.shp"));

                WKTReader wktReader = new WKTReader();
                Geometry geomExtent = wktReader.read("POLYGON ((183456.16879270627 2428883.34989648 0, 183461.0194286128 2428262.4685004433 0, 184467.5263792192 2428233.364685004 0, 184477.22765103227 2428883.34989648 0, 183456.16879270627 2428883.34989648 0))");

                int waintingResult = 77;

                Envelope extent = geomExtent.getEnvelopeInternal();
                DataSource original = dsf.getDataSource("landcover2000");

                FilterDataSourceDecorator filterDataSourceDecorator = new FilterDataSourceDecorator(
                        original);

                String filter = "ST_Intersects(ST_GeomFromText('POLYGON(("
                        + extent.getMinX() + " " + extent.getMinY() + ","
                        + extent.getMinX() + " " + extent.getMaxY() + ","
                        + extent.getMaxX() + " " + extent.getMaxY() + ","
                        + extent.getMaxX() + " " + extent.getMinY() + ","
                        + extent.getMinX() + " " + extent.getMinY() + "))'), "
                        + "the_geom" + ")";
                filterDataSourceDecorator.setFilter(filter);

                filterDataSourceDecorator.open();
                long filterCount = filterDataSourceDecorator.getRowCount();
                filterDataSourceDecorator.close();
                assertEquals(filterCount, waintingResult);
        }
}
