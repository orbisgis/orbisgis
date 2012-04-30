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
package org.gdms.sql.indexes;

import org.gdms.driver.DriverException;
import org.gdms.data.indexes.RTreeIndex;
import org.gdms.data.indexes.DataSourceIndex;
import org.junit.Before;
import org.junit.Test;
import java.io.File;


import org.gdms.TestBase;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.indexes.BTreeIndex;
import org.gdms.data.indexes.IndexManager;
import org.gdms.source.SourceManager;

import static org.junit.Assert.*;

import org.gdms.TestResourceHandler;
import org.gdms.data.DataSource;
import org.gdms.data.indexes.DefaultAlphaQuery;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;

public class IndexManagementTest extends TestBase {

        private IndexManager im;

        @Before
        public void setUp() throws Exception {
                super.setUpTestsWithoutEdition();
                sm.register("source", new File(TestResourceHandler.TESTRESOURCES, "hedgerow.shp"));
                im = dsf.getIndexManager();
        }

        @Test
        public void testDeleteIndex() throws Exception {
                im.buildIndex("source", "the_geom", null);
                String sql = "DROP INDEX ON source(the_geom);";
                dsf.executeSQL(sql);
                assertNull(im.getIndex("source", "the_geom"));
        }

        @Test
        public void testBuildSpatialIndexSpecifyingField() throws Exception {
                String sql = "CREATE INDEX ON source(the_geom);";
                dsf.executeSQL(sql);
                DataSourceIndex idx = im.getIndex("source", "the_geom");
                assertNotNull(idx);
                assertTrue(idx instanceof RTreeIndex);
        }

        @Test
        public void testBuildAlphaIndexSpecifyingField() throws Exception {
                String sql = "CREATE INDEX ON source(gid);";
                dsf.executeSQL(sql);
                DataSourceIndex idx = im.getIndex("source", "gid");
                assertNotNull(idx);
                assertTrue(idx instanceof BTreeIndex);
        }

        @Test(expected = DriverException.class)
        public void testWrongFieldName() throws Exception {
                String sql = "CREATE INDEX ON source(gid2);";
                dsf.executeSQL(sql);
                fail();
        }

        @Test(expected = NoSuchTableException.class)
        public void testWrongTableName() throws Exception {
                String sql = "CREATE INDEX ON source2(gid);";
                dsf.executeSQL(sql);
                fail();
        }

        @Test(expected = DriverException.class)
        public void testDropWrongFieldName() throws Exception {
                String sql = "DROP INDEX ON source(gid2);";
                dsf.executeSQL(sql);
                fail();
        }

        @Test(expected = NoSuchTableException.class)
        public void testDropWrongTableName() throws Exception {
                String sql = "DROP INDEX ON source2(gid);";
                dsf.executeSQL(sql);
                fail();
        }

        @Test
        public void testMultipleFieldIndex() throws Exception {
                sm.register("landc", new File(TestResourceHandler.TESTRESOURCES, "landcover2000.shp"));
                dsf.executeSQL("CREATE INDEX ON landc(runoff_win, runoff_sum);");
                DefaultAlphaQuery al = new DefaultAlphaQuery(new String[]{"runoff_win", "runoff_sum"}, ValueFactory.createValue(new Value[]{
                        ValueFactory.createValue(0.05), ValueFactory.createValue(0.4)
                }));
                int[] i = im.queryIndex("landc", al);
                DataSource d = dsf.getDataSourceFromSQL("SELECT COUNT(*) FROM landc WHERE runoff_win = 0.05  AND runoff_sum = 0.4 ;");
                d.open();
                assertEquals(i.length, d.getInt(0, 0));
                d.close();
        }
        
        @Test
        public void testMultipleFieldIndexRangeQuery() throws Exception {
                sm.register("landc", new File(TestResourceHandler.TESTRESOURCES, "landcover2000.shp"));
                dsf.executeSQL("CREATE INDEX ON landc(runoff_win, runoff_sum);");
                DefaultAlphaQuery al = new DefaultAlphaQuery(new String[]{"runoff_win", "runoff_sum"}, ValueFactory.createValue(new Value[]{
                        ValueFactory.createValue(0.05), ValueFactory.createValue(0.4)
                }), true, null, false);
                int[] i = im.queryIndex("landc", al);
                DataSource d = dsf.getDataSourceFromSQL("SELECT COUNT(*) FROM landc WHERE runoff_win > 0.05  OR (runoff_win = 0.05 AND runoff_sum >= 0.4) ;");
                d.open();
                assertEquals(i.length, d.getInt(0, 0));
                d.close();
        }
}
