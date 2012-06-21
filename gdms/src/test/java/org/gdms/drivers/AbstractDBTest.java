/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.drivers;

import org.junit.Before;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import org.gdms.TestBase;
import org.gdms.TestResourceHandler;


import org.gdms.data.DataSourceFactory;
import org.gdms.data.db.DBSource;
import org.gdms.source.SourceManager;

public abstract class AbstractDBTest extends TestBase {

        @Before
        public void setUp() throws Exception {
                super.setUpTestsWithoutEdition();
        }

        protected void executeScript(DBSource dbSource, String statement)
                throws Exception {
                Class.forName("org.postgresql.Driver").newInstance();
                Class.forName("org.hsqldb.jdbcDriver").newInstance();
                String connectionString = dbSource.getPrefix() + ":";
                if (dbSource.getHost() != null) {
                        connectionString += "//" + dbSource.getHost();

                        if (dbSource.getPort() != -1) {
                                connectionString += (":" + dbSource.getPort());
                        }
                        connectionString += "/";
                }

                connectionString += (dbSource.getDbName());

                Connection c = DriverManager.getConnection(connectionString, dbSource.getUser(), dbSource.getPassword());

                Statement st = c.createStatement();
                st.execute(statement);
                st.close();
                c.close();
        }

        protected DBSource getPostgreSQLSource(String tableName) {
                return new DBSource("127.0.0.1", 5432, "gisdb", "gis", "gis",
                        tableName, "jdbc:postgresql");
        }

        protected DBSource getHSQLDBSource(String tableName) {
                return new DBSource(null, -1, TestResourceHandler.OTHERRESOURCES + tableName,
                        "sa", "", tableName, "jdbc:hsqldb:file");
        }

        protected void deleteTable(DBSource source) {
                String script = "DROP TABLE " + source.getTableName() + ";";
                try {
                        executeScript(source, script);
                } catch (Exception e) {
                }
        }
}
