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
package org.gdms.driver;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Interface implemented by drivers that use JDBC to access data.
 */
public interface DBDriver extends Driver {

        /**
         * Provides connections to the database. Each invocation creates and returns
         * a new connection. The connection are managed in upper layers.
         *
         * @param connectionString
         * @return Connection
         * @throws SQLException if some error happens
         */
        Connection getConnection(String connectionString) throws SQLException;
        
        /**
         * Creates a connection string for the given database parameters
         *
         * @param host
         * @param port port of the database management system. -1 means default port
         * @param ssl
         * @param dbName
         * @param user
         * @param password
         * @return the connection string
         */
        String getConnectionString(String host, int port, boolean ssl, String dbName, String user,
                String password);

        /**
         * Frees any resource allocated in the open method.
         *
         * @param conn
         * @throws DriverException
         */
        void close(Connection conn) throws DriverException;

        /**
         * Connects to the data source and reads the specified table of
         * the default schema in the database. Sets this table as current.
         *
         * @param con connection to the database
         * @param tableName name of the table where the data is in
         * @throws DriverException
         */
        void open(Connection con, String tableName) throws DriverException;

        /**
         * Connects to the data source and reads the specified table in the
         * specified schema. Sets this table and this schema as current.
         *
         * @param con connection to the database
         * @param tableName name of the table where the data is in
         * @param schemaName name of the schema where the table is in
         * @throws DriverException
         */
        void open(Connection con, String tableName, String schemaName) throws DriverException;

        /**
         * Retrieves all table names in a database (with types TABLES or VIEW).
         *
         * @param c connection to the database
         * @return the descriptions of the tables
         * @throws DriverException
         */
        TableDescription[] getTables(Connection c) throws DriverException;

        /**
         * Retrieves all schema names in a database.
         *
         * @param c the connection
         * @return the names of the schemas
         *
         * @throws DriverException
         */
        String[] getSchemas(Connection c) throws DriverException;

        /**
         * Retrieves all table names in a database, in the specified catalog and schema
         * and that match with a "tableNamePattern" and with some types.
         *
         * @param c the database connection.
         * @param catalog
         * @param schemaPattern
         * @param tableNamePattern
         * @param types
         * @return a table description of the Tables.
         * @throws DriverException
         * @see {@link java.sql.DatabaseMetaData#getTables(String, String, String, String[])}
         */
        TableDescription[] getTables(Connection c, String catalog,
                String schemaPattern, String tableNamePattern, String[] types) throws DriverException;

        /**
         * Gets the port the dbms accessed by this driver listens to by default.
         *
         * @return the port number
         */
        int getDefaultPort();

        /**
         * Gets the prefixes accepted by this driver.
         *
         * @return
         */
        String[] getPrefixes();

        /**
         * Gets all non blocking errors that happened since the last
         * call to any DBDriver method that could throw DriverException.
         *
         * @return an array of exceptions, possibly empty
         */
        DriverException[] getLastNonBlockingErrors();
}