/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.core.jdbc;

import org.h2gis.h2spatial.ut.SpatialH2UT;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orbisgis.core.common.IntegerUnion;
import org.orbisgis.progress.NullProgressMonitor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;

/**
 * @author Nicolas Fortin
 */
public class JDBCUtilityTest {
    private static Connection connection;

    @BeforeClass
    public static void tearUp() throws Exception {
        connection = SpatialH2UT.createSpatialDataBase(JDBCUtilityTest.class.getName(), false);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        connection.close();
    }

    @Test
    public void sortTest() throws SQLException {
        try(Statement st = connection.createStatement()) {
            st.execute("DROP TABLE INTTABLE IF EXISTS");
            try {
                // Test without PK
                st.execute("CREATE TABLE INTTABLE (vals integer)");
                st.execute("INSERT INTO INTTABLE VALUES (20), (5), (15), (4), (1)");
                // Test ascending
                Collection<Integer> sortedRowId = ReadTable.getSortedColumnRowIndex(connection, "INTTABLE", "vals", true, new NullProgressMonitor());
                Iterator<Integer> itTest = sortedRowId.iterator();
                assertEquals(5, itTest.next().intValue());
                assertEquals(4, itTest.next().intValue());
                assertEquals(2, itTest.next().intValue());
                assertEquals(3, itTest.next().intValue());
                assertEquals(1, itTest.next().intValue());
                // Test descending
                sortedRowId = ReadTable.getSortedColumnRowIndex(connection, "inttable", "vals", false, new NullProgressMonitor());
                itTest = sortedRowId.iterator();
                assertEquals(1, itTest.next().intValue());
                assertEquals(3, itTest.next().intValue());
                assertEquals(2, itTest.next().intValue());
                assertEquals(4, itTest.next().intValue());
                assertEquals(5, itTest.next().intValue());
            } finally {
                st.execute("DROP TABLE INTTABLE IF EXISTS");
            }
            st.execute("DROP TABLE INTTABLE IF EXISTS");
            try {
                // Test with PK
                st.execute("CREATE TABLE INTTABLE (id integer primary key, vals integer)");
                st.execute("INSERT INTO INTTABLE VALUES (1,20), (2,5), (4,15), (8,4), (16,1)");
                // Test ascending
                Collection<Integer> sortedRowId = ReadTable.getSortedColumnRowIndex(connection, "INTTABLE", "vals", true, new NullProgressMonitor());
                Iterator<Integer> itTest = sortedRowId.iterator();
                assertEquals(5, itTest.next().intValue());
                assertEquals(4, itTest.next().intValue());
                assertEquals(2, itTest.next().intValue());
                assertEquals(3, itTest.next().intValue());
                assertEquals(1, itTest.next().intValue());
                // Test descending
                sortedRowId = ReadTable.getSortedColumnRowIndex(connection, "INTTABLE", "vals", false, new NullProgressMonitor());
                itTest = sortedRowId.iterator();
                assertEquals(1, itTest.next().intValue());
                assertEquals(3, itTest.next().intValue());
                assertEquals(2, itTest.next().intValue());
                assertEquals(4, itTest.next().intValue());
                assertEquals(5, itTest.next().intValue());
            } finally {
                st.execute("DROP TABLE inttable IF EXISTS");
            }
        }
    }

    private static void checkStats(String[] props) {
        assertEquals(0, Double.valueOf(props[ReadTable.STATS.MIN.ordinal()]).intValue());
        assertEquals(78, Double.valueOf(props[ReadTable.STATS.MAX.ordinal()]).intValue());
        assertEquals(19.5, Double.valueOf(props[ReadTable.STATS.AVG.ordinal()]),1e-12);
        assertEquals(10, Double.valueOf(props[ReadTable.STATS.COUNT.ordinal()]).intValue());
        assertEquals(24.998888864196434, Double.valueOf(props[ReadTable.STATS.STDDEV_SAMP.ordinal()]),1e-15);
        assertEquals(195, Double.valueOf(props[ReadTable.STATS.SUM.ordinal()]).intValue());
    }

    @Test
    public void testStats() throws SQLException {
        Set<Integer> indexes = new TreeSet<>(Arrays.asList(new Integer[]{0, 2, 3, 4, 8, 10, 15, 30, 45, 78}));
        try(Statement st = connection.createStatement()) {
            String table = CreateTable.createIndexTempTable(connection, new NullProgressMonitor(), indexes, 5);
            // Do stats using sql
            String[] props = ReadTable.computeStatsSQL(connection, table, "ROWID", new NullProgressMonitor());
            checkStats(props);
            // Do stats using apache math
            props = ReadTable.computeStatsLocal(connection, table, "ROWID", new IntegerUnion(1,10), new NullProgressMonitor());
            checkStats(props);
            st.execute("DROP TABLE "+table);
        }
    }
}
