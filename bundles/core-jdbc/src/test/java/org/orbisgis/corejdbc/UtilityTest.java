/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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

import org.h2gis.functions.factory.H2GISDBFactory;
import org.h2gis.utilities.SFSUtilities;
import org.h2gis.utilities.TableLocation;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orbisgis.commons.progress.NullProgressMonitor;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * JDBC utility unit test
 * @author Nicolas Fortin
 */
public class UtilityTest {
    private static DataSource dataSource;

    @BeforeClass
    public static void tearUp() throws Exception {
        dataSource = SFSUtilities.wrapSpatialDataSource(H2GISDBFactory.createDataSource(UtilityTest.class.getSimpleName(), true));
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
        Set<Long> indexes = new TreeSet<>(Arrays.asList(new Long[]{0l, 2l, 3l, 4l, 8l, 10l, 15l, 30l, 45l, 78l}));
        try(Connection connection = dataSource.getConnection();
            Statement st = connection.createStatement()) {
            String table = CreateTable.createIndexTempTable(connection, new NullProgressMonitor(), indexes,"pk", 5);
            // Read table content
            Iterator<Long> it = indexes.iterator();
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
