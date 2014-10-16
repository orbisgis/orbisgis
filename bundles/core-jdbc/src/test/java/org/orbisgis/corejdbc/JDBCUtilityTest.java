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

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import javax.sql.DataSource;
import org.h2gis.h2spatial.CreateSpatialExtension;
import org.h2gis.h2spatial.ut.SpatialH2UT;
import org.h2gis.utilities.SFSUtilities;
import org.junit.AfterClass;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orbisgis.corejdbc.common.LongUnion;
import org.orbisgis.corejdbc.internal.DataManagerImpl;
import org.orbisgis.progress.NullProgressMonitor;
import org.h2gis.drivers.DriverManager;

/**
 * @author Nicolas Fortin
 */
public class JDBCUtilityTest {
    private static DataSource dataSource;
    private static Connection connection;

    @BeforeClass
    public static void tearUp() throws Exception {
        dataSource = SFSUtilities.wrapSpatialDataSource(SpatialH2UT.createDataSource(JDBCUtilityTest.class.getSimpleName(), true));
        connection = dataSource.getConnection();
        CreateSpatialExtension.registerFunction(connection.createStatement(), new DriverManager(), "");
    }

    @AfterClass
    public static void tearDown() throws Exception {
        if(connection!=null) {
            connection.close();
        }
    }

    @Test
    public void sortTest() throws SQLException {
        try(Statement st = connection.createStatement()) {
            st.execute("DROP TABLE INTTABLE IF EXISTS");
            try {
                // Test without PK
                st.execute("CREATE TABLE INTTABLE (\"vals\" integer)");
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
                sortedRowId = ReadTable.getSortedColumnRowIndex(connection, "INTTABLE", "vals", false, new NullProgressMonitor());
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
                st.execute("CREATE TABLE INTTABLE (id integer primary key, \"vals\" integer)");
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
        Set<Long> indexes = new TreeSet<>(Arrays.asList(new Long[]{0l, 2l, 3l, 4l, 8l, 10l, 15l, 30l, 45l, 78l}));
        try(Statement st = connection.createStatement()) {
            String table = CreateTable.createIndexTempTable(connection, new NullProgressMonitor(), indexes,"ROWID", 5);
            // Do stats using sql
            String[] props = ReadTable.computeStatsSQL(connection, table, "ROWID", new NullProgressMonitor());
            checkStats(props);
            // Do stats using apache math
            props = ReadTable.computeStatsLocal(connection, table, "ROWID",getSortedSet(1,11) , new NullProgressMonitor());
            checkStats(props);
            st.execute("DROP TABLE "+table);
        }
    }

    private static SortedSet<Integer> getSortedSet(int begin,int end) {
        SortedSet<Integer> set = new TreeSet<>();
        for(int i = begin; i < end; i++) {
            set.add(i);
        }
        return set;
    }

    @Test
    public void testSelection() throws SQLException {
        DataManager dataManager = new DataManagerImpl(dataSource);
        try(Statement st = connection.createStatement()) {
            st.execute("DROP TABLE IF EXISTS TEST");
            st.execute("CREATE TABLE TEST(gid integer primary key auto_increment, geom MULTIPOLYGON)");
            st.execute("INSERT INTO TEST(geom) VALUES ('MULTIPOLYGON (((-111 -24, -121 17, -69 25, -66 -38, -111 -24)))'), " +
                    "('MULTIPOLYGON (((-50 -2, -59 50, 48 48, -20 20, -50 -2)))'), " +
                    "('MULTIPOLYGON (((-75 -67, -38 -16, 44 24, 99 26, 112 4, -35 -79, -75 -67)))');");
            // Check selection algorithm
            Envelope envelope = new Envelope(-116, -55, -69, -19);
            Set<Long> intersected = ReadTable.getTablePkByEnvelope(dataManager, "TEST", "GEOM", new GeometryFactory().toGeometry(envelope),
                    false);
            LongUnion rowIds = new LongUnion(intersected);
            Iterator<Long> it = rowIds.iterator();
            assertEquals(1 ,it.next().intValue());
            assertEquals(3 ,it.next().intValue());
            assertFalse(it.hasNext());
        }
    }

    @Test
    public void testColumnInfos() throws SQLException {
        Locale oldLocale = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);
        try(Statement st = connection.createStatement()) {
            st.execute("DROP TABLE IF EXISTS TESTMETA");
            st.execute("CREATE TABLE TESTMETA(gid integer primary key auto_increment, geom MULTIPOLYGON, value double)");
            st.execute("ALTER TABLE TESTMETA ADD CHECK (value > 5)");
        }
        String meta = MetaData.getColumnInformations(connection.getMetaData(), "TESTMETA", 1);
        assertTrue(meta.startsWith("\n" +
                "Field name :\tGID\n" +
                "Field type :\tINTEGER\n" +
                "Size :\t10\n" +
                "Decimal digits :\t0\n" +
                "Nullable : NO\n" +
                "Default value :\t(NEXT VALUE FOR PUBLIC."));
        assertTrue(meta.endsWith("Auto increment :\tYES\n" +
                "Constraints :\n" +
                "\tType :\tother index\n"));
        meta = MetaData.getColumnInformations(connection.getMetaData(), "TESTMETA", 2);
        assertEquals("\n" +
                "Field name :\tGEOM\n" +
                "Field type :\tGEOMETRY\n" +
                "Size :\t6\n" +
                "Decimal digits :\t0\n" +
                "Nullable : allows NULL values\n" +
                "Default value :\tnull\n" +
                "Auto increment :\tNO\n" +
                "Constraints :\n", meta);
        meta = MetaData.getColumnInformations(connection.getMetaData(), "TESTMETA", 3);
        assertEquals("\n" +
                "Field name :\tVALUE\n" +
                "Field type :\tDOUBLE\n" +
                "Size :\t17\n" +
                "Decimal digits :\t0\n" +
                "Nullable : allows NULL values\n" +
                "Default value :\tnull\n" +
                "Auto increment :\tNO\n" +
                "Constraints :\n", meta);
        Locale.setDefault(oldLocale);
    }

    @Test
    public void pkTest() throws SQLException {
        try(Statement st = connection.createStatement()) {
            st.execute("DROP TABLE IF EXISTS TEST");
            st.execute("CREATE TABLE TEST(gid integer auto_increment, geom MULTIPOLYGON)");
            assertEquals("_ROWID_", MetaData.getPkName(connection, "TEST", true));
        }
    }
    
    @Test
    public void testTableType() throws SQLException {
         try(Statement st = connection.createStatement()) {
            st.execute("DROP TABLE IF EXISTS TABLE_TEST");
            st.execute("CREATE TABLE TABLE_TEST(gid integer auto_increment)");
            assertEquals(MetaData.getTableType(connection, "TABLE_TEST"), MetaData.TableType.TABLE);
            st.execute("DROP TABLE IF EXISTS TABLE_GLOBAL");
            st.execute("CREATE GLOBAL TEMPORARY TABLE TABLE_GLOBAL(gid integer auto_increment)");
            //H2 database type is limited
            assertEquals(MetaData.getTableType(connection, "TABLE_GLOBAL"), MetaData.TableType.TABLE);
            st.execute("DROP VIEW IF EXISTS VIEW_TEST");
            st.execute("CREATE VIEW VIEW_TEST as SELECT * FROM TABLE_TEST");
            assertEquals(MetaData.getTableType(connection, "VIEW_TEST"), MetaData.TableType.VIEW);
            st.execute("drop table if exists EXTERNAL_TABLE");
            st.execute("CALL FILE_TABLE('"+JDBCUtilityTest.class.getResource("bv_sap.shp").getPath()+"', 'EXTERNAL_TABLE');");
            assertEquals(MetaData.getTableType(connection, "EXTERNAL_TABLE"), MetaData.TableType.EXTERNAL);
         }
    }

    @Test
    public void testTableCreateFromPK() throws SQLException {
        try(Statement st = connection.createStatement()) {
            st.execute("DROP TABLE IF EXISTS INTTABLE, INTTABLE_SEL");
            st.execute("CREATE TABLE INTTABLE (pk bigint primary key, \"vals\" integer)");
            st.execute("INSERT INTO INTTABLE VALUES (1, 20), (2, 5), (3, 15), (4, 4), (5, 1)");
            CreateTable.createTableFromRowPkSelection(dataSource, "INTTABLE", new HashSet<>(Arrays.asList(1l, 3l, 5l)),
                    "INTTABLE_SEL",new NullProgressMonitor());
            try(ResultSet rs = st.executeQuery("SELECT \"vals\" FROM INTTABLE_SEL")) {
                assertTrue(rs.next());
                assertEquals(20, rs.getInt(1));
                assertTrue(rs.next());
                assertEquals(15, rs.getInt(1));
                assertTrue(rs.next());
                assertEquals(1, rs.getInt(1));
            }
        }
    }
}
