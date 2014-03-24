/**
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
package org.orbisgis.coremap.renderer.se.parameter;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;

import org.h2gis.h2spatial.ut.SpatialH2UT;
import org.h2gis.h2spatialext.CreateSpatialExtension;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.corejdbc.internal.DataManagerImpl;
import org.orbisgis.coremap.renderer.se.parameter.real.RealAttribute;
import org.orbisgis.coremap.renderer.se.parameter.real.RealParameter;
import org.orbisgis.coremap.renderer.se.parameter.string.StringAttribute;
import org.orbisgis.coremap.renderer.se.parameter.string.StringParameter;

import javax.sql.DataSource;

import static org.junit.Assert.*;

/**
 *
 * @author Maxence Laurent
 */
public class PropertyNameTest {
    private static Connection connection;
    private static DataManager dataManager;

    @BeforeClass
    public static void tearUpClass() throws Exception {
        DataSource dataSource = SpatialH2UT.createDataSource(PropertyNameTest.class.getSimpleName(), false);
        connection = dataSource.getConnection();
        CreateSpatialExtension.initSpatialExtension(connection);
        dataManager = new DataManagerImpl(dataSource);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        connection.close();
        dataManager.dispose();
    }

    private DataManager getDataManager() {
        return dataManager;
    }

    private Connection getConnection() {
        return connection;
    }

    // Data to test
    File src = new File("../src/test/resources/data/landcover2000.shp");

    /**
     * Test of getValue method, of class StringAttribute.
     * @throws Exception
     */
    @Test
    public void testRealAttribute() throws Exception {
        String tableName = getDataManager().registerDataSource(src.toURI());
        try (ResultSet rs = getConnection().createStatement().executeQuery("select * from " + tableName)) {
            RealParameter real = new RealAttribute("runoff_win");
            assertTrue(real.getValue(rs, 1) == 0.05);
            assertTrue(real.getValue(rs, 51) == 0.4);
            assertTrue(real.getValue(rs, 1222) == 0.4);
        } finally {
            getConnection().createStatement().execute("DROP TABLE landcover2000");
        }
    }

    /**
     * Test of getValue method, of class StringAttribute.
     * @throws Exception
     */
    @Test
    public void testStringAttribute() throws Exception {
        String tableName = getDataManager().registerDataSource(src.toURI());
        try (ResultSet rs = getConnection().createStatement().executeQuery("select * from " + tableName)) {
            StringParameter string = new StringAttribute("type");
            assertTrue(string.getValue(rs, 41).equals("grassland"));
            assertTrue(string.getValue(rs, 48).equals("corn"));
            assertTrue(string.getValue(rs, 57).equals("vegetables"));
        } finally {
            getConnection().createStatement().execute("DROP TABLE landcover2000");
        }
    }
    

}
