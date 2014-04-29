/**
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
package org.orbisgis.core;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.core.beanshell.BeanShellScriptTest;
import org.orbisgis.core.beanshell.BeanshellScript;
import org.orbisgis.core.context.main.MainContext;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Init OrbisGIS core on test class loading
 */
public abstract class AbstractTest {
    private static Connection connection;
    protected static DataSource dataSource;
    protected static MainContext mainContext;

    @BeforeClass
    public static void init() throws Exception {
        BeanshellScript.init(BeanShellScriptTest.mainParams("../src/test/resources/beanshell/helloWorld.bsh"));
        mainContext = BeanshellScript.getMainContext();
        dataSource =  mainContext.getDataSource();
    }

    @AfterClass
    public static void dispose() throws Exception {
        BeanshellScript.dispose();
        // Remove database file
        try {
            File dbFile = new File("workspace/database.h2.db");
            if(dbFile.exists()) {
                dbFile.delete();
            }
            File dbMvFile = new File("workspace/database.mv.db");
            if(dbMvFile.exists()) {
                dbMvFile.delete();
            }
        } catch (Exception ex) {
        }
    }

    protected Connection getConnection() throws SQLException {
        if(connection==null || connection.isClosed()) {
            connection = dataSource.getConnection();
        }
        return connection;
    }

    @After
    public void closeConnection() throws SQLException {
        if(connection!=null && !connection.isClosed()) {
            connection.close();
        }
    }

    protected DataManager getDataManager() {
        return mainContext.getDataManager();
    }
}
