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
package org.orbisgis.editorjdbc.jobs;

import org.h2gis.h2spatial.CreateSpatialExtension;
import org.h2gis.h2spatial.ut.SpatialH2UT;
import org.h2gis.utilities.SFSUtilities;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orbisgis.corejdbc.common.LongUnion;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.*;

public class DeleteSelectedRowsTest {
    private static DataSource dataSource;

    @BeforeClass
    public static void tearUp() throws Exception {
        dataSource = SFSUtilities.wrapSpatialDataSource(SpatialH2UT.createDataSource(DeleteSelectedRowsTest.class.getSimpleName()
                , false));
        try(Connection connection = dataSource.getConnection()) {
            CreateSpatialExtension.initSpatialExtension(connection);
        }
    }

    @Test
    public void testDelete() throws SQLException {
        try(Connection connection = dataSource.getConnection();
            Statement st = connection.createStatement()) {
            st.execute("DROP TABLE IF EXISTS \"MY TABLE\"");
            st.execute("CREATE TABLE \"MY TABLE\"(PK INTEGER PRIMARY KEY) AS SELECT X FROM SYSTEM_RANGE(1,50);");
            // Drop some rows
            LongUnion pkToDelete = new LongUnion(1, 25);
            DeleteSelectedRows deleteSelectedRows = new DeleteSelectedRows(pkToDelete, "MY TABLE", dataSource);
            deleteSelectedRows.doInBackground();
            // Check if rows are deleted
            try(ResultSet rs = st.executeQuery("SELECT PK FROM \"MY TABLE\"")) {
                for(long expectedPk : new LongUnion(26,50)) {
                    assertTrue(rs.next());
                    assertEquals(expectedPk, rs.getLong(1));
                }
                assertFalse(rs.next());
            }
        }
    }
}