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
package org.gdms.sql.indexes;

import org.gdms.driver.DriverException;
import org.gdms.data.indexes.RTreeIndex;
import org.gdms.data.indexes.DataSourceIndex;
import org.junit.Before;
import org.junit.Test;
import java.io.File;


import org.gdms.SQLBaseTest;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SQLDataSourceFactory;
import org.gdms.data.indexes.BTreeIndex;
import org.gdms.data.indexes.IndexManager;
import org.gdms.source.SourceManager;

import static org.junit.Assert.*;

public class IndexManagementTest {

        private SQLDataSourceFactory dsf;
        private IndexManager im;

        @Before
        public void setUp() throws Exception {
                dsf = new SQLDataSourceFactory();
                dsf.setTempDir(SQLBaseTest.backupDir.getAbsolutePath());
                dsf.setResultDir(SQLBaseTest.backupDir);
                SourceManager sm = dsf.getSourceManager();
                sm.removeAll();
                sm.register("source", new File(SQLBaseTest.internalData,
                        "hedgerow.shp"));
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
}
