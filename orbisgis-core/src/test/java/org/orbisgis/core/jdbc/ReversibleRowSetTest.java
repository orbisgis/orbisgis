/**
 * h2spatial is a library that brings spatial support to the H2 Java database.
 *
 * h2spatial is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
 *
 * h2patial is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * h2spatial is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * h2spatial. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.core.jdbc;

import org.h2gis.h2spatial.ut.SpatialH2UT;
import org.h2gis.utilities.TableLocation;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orbisgis.core.ReversibleRowSetImpl;
import org.orbisgis.core.api.ReversibleRowSet;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Nicolas Fortin
 */
public class ReversibleRowSetTest {
    private static DataSource dataSource;

    @BeforeClass
    public static void tearUp() throws Exception {
        dataSource = SpatialH2UT.createDataSource("ReversibleRowSetTest", true);
    }

    @Test
    public void testReadTable() throws SQLException {
        try (
                Connection connection = dataSource.getConnection();
                Statement st = connection.createStatement()) {
            st.execute("drop table if exists test");
            st.execute("create table test (id integer)");
            st.execute("insert into test values (42), (666)");
            try (ReversibleRowSet rs = new ReversibleRowSetImpl(dataSource, TableLocation.parse("test"))) {
                assertTrue(rs.next());
                assertEquals(42, rs.getInt(1));
                assertTrue(rs.next());
                assertEquals(666, rs.getInt(1));
            }
        }
    }
}
