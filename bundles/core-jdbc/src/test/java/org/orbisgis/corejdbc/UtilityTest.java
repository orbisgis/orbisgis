/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.corejdbc;

import org.h2gis.h2spatial.ut.SpatialH2UT;
import org.h2gis.utilities.SFSUtilities;
import org.h2gis.utilities.TableLocation;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orbisgis.progress.NullProgressMonitor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * JDBC utility unit test
 * @author Nicolas Fortin
 */
public class UtilityTest {
    private static DataSource dataSource;

    @BeforeClass
    public static void tearUp() throws Exception {
        dataSource = SFSUtilities.wrapSpatialDataSource(SpatialH2UT.createDataSource(UtilityTest.class.getSimpleName(), true));
    }

    @Test
    public void testTableUniqueName() throws SQLException {
        try(Connection connection = dataSource.getConnection();
            Statement st = connection.createStatement()) {
            DatabaseMetaData meta = connection.getMetaData();
            st.execute("DROP TABLE IF EXISTS TEST");
            assertEquals(TableLocation.parse("TEST").toString(), MetaData.getNewUniqueName("TEST", meta, ""));
            assertEquals(TableLocation.parse("TEST_UNIQUE").toString(), MetaData.getNewUniqueName("TEST", meta, "UNIQUE"));
            st.execute("CREATE TABLE TEST(id integer primary key)");
            assertEquals(TableLocation.parse("TEST_1").toString(), MetaData.getNewUniqueName("TEST", meta, ""));
            assertEquals(TableLocation.parse("TEST_UNIQUE").toString(),MetaData.getNewUniqueName("TEST", meta, "UNIQUE"));
            st.execute("DROP TABLE IF EXISTS TEST");
        }
    }

    @Test
    public void testCreateTableIndex() throws SQLException {
        Set<Integer> indexes = new TreeSet<>(Arrays.asList(new Integer[]{0, 2, 3, 4, 8, 10, 15, 30, 45, 78}));
        try(Connection connection = dataSource.getConnection();
            Statement st = connection.createStatement()) {
            String table = CreateTable.createIndexTempTable(connection, new NullProgressMonitor(), indexes, 5);
            // Read table content
            Iterator<Integer> it = indexes.iterator();
            try(ResultSet rs = st.executeQuery("SELECT * FROM "+table)) {
                while(rs.next() && it.hasNext()) {
                    assertEquals(it.next().intValue(), rs.getInt(1));
                }
                assertFalse(rs.next());
                assertFalse(it.hasNext());
            }
        }
    }
}
