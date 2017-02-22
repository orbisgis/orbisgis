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
package org.orbisgis.editorjdbc.jobs;

import org.h2gis.functions.factory.H2GISDBFactory;
import org.h2gis.functions.factory.H2GISFunctions;
import org.h2gis.utilities.SFSUtilities;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.corejdbc.ReversibleRowSet;
import org.orbisgis.corejdbc.common.LongUnion;
import org.orbisgis.corejdbc.internal.DataManagerImpl;

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
        dataSource = SFSUtilities.wrapSpatialDataSource(H2GISDBFactory.createDataSource(DeleteSelectedRowsTest.class.getSimpleName()
                , false));
        try(Connection connection = dataSource.getConnection()) {
            H2GISFunctions.load(connection);
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
    @Test
    public void testDeleteWithRowSet() throws SQLException, InterruptedException {
        try(Connection connection = dataSource.getConnection();
            Statement st = connection.createStatement()) {
            st.execute("DROP TABLE IF EXISTS \"MY TABLE\"");
            st.execute("CREATE TABLE \"MY TABLE\"(PK INTEGER PRIMARY KEY) AS SELECT X FROM SYSTEM_RANGE(1,50);");
            // Drop some rows
            LongUnion pkToDelete = new LongUnion(1, 25);
            DataManager dataManager = new DataManagerImpl(dataSource);
            try(ReversibleRowSet reversibleRowSet = dataManager.createReversibleRowSet()) {
                reversibleRowSet.setCommand("SELECT * FROM \"MY TABLE\"");
                reversibleRowSet.execute();
                DeleteSelectedRows.deleteUsingRowSet(reversibleRowSet, pkToDelete);
            }
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
