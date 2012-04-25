/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer, Maxence LAURENT,
 * computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
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
package org.gdms.data.db;

import java.io.Serializable;

/**
 * Class that contains the information to identify a database table
 *
 * @author Fernando Gonzalez Cortes
 */
public class DBSource implements Serializable {

        private static final long serialVersionUID = 0L;
        private String host;
        private int port;
        private String tableName;
        private String schemaName;
        private String user;
        private String dbName;
        private String password;
        private String prefix;
        private boolean ssl = false;

        public DBSource(String host, int port, String dbName, String user,
                String password, String prefix) {
                this.host = host;
                this.port = port;
                this.dbName = dbName;
                this.user = user;
                this.password = password;
                this.prefix = prefix;
        }

        public DBSource(String host, int port, String dbName, String user,
                String password, String prefix, boolean ssl) {
                this(host, port, dbName, user, password, prefix);
                this.ssl = ssl;
        }

        public DBSource(String host, int port, String dbName, String user,
                String password, String tableName, String prefix) {
                this(host, port, dbName, user, password, prefix);
                this.tableName = tableName;
        }
        
        public DBSource(String host, int port, String dbName, String user,
                String password, String tableName, String prefix, boolean ssl) {
                this(host, port, dbName, user, password, tableName, prefix);
                this.ssl = ssl;
        }

        public DBSource(String host, int port, String dbName, String user,
                String password, String schemaName, String tableName, String prefix) {
                this(host, port, dbName, user, password, tableName, prefix);
                this.schemaName = schemaName;
        }
        
        public DBSource(String host, int port, String dbName, String user,
                String password, String schemaName, String tableName, String prefix, boolean ssl) {
                this(host, port, dbName, user, password, schemaName, tableName, prefix);
                this.ssl = ssl;
        }

        public String getDbName() {
                return dbName;
        }

        public void setDbName(String dbName) {
                this.dbName = dbName;
        }

        public String getHost() {
                return host;
        }

        public void setHost(String host) {
                this.host = host;
        }

        public String getPassword() {
                return password;
        }

        public void setPassword(String password) {
                this.password = password;
        }

        public int getPort() {
                return port;
        }

        public void setPort(int port) {
                this.port = port;
        }

        public String getTableName() {
                return tableName;
        }

        public void setSchemaName(String schemaName) {
                this.schemaName = schemaName;
        }

        public String getSchemaName() {
                return schemaName;
        }

        public void setTableName(String tableName) {
                this.tableName = tableName;
        }

        public String getUser() {
                return user;
        }

        public void setUser(String user) {
                this.user = user;
        }

        /**
         * Returns a human-readable description of this DBSource
         * @return a description String
         */
        @Override
        public String toString() {
                return host + "-" + port + "-" + dbName + "-" + user + "-" + password
                        + "-" + tableName;
        }

        public String getDbms() {
                return host + ":" + port + "//" + dbName;
        }

        public String getPrefix() {
                return prefix;
        }

        public void setPrefix(String prefix) {
                this.prefix = prefix;
        }

        /**
         * @return the ssl
         */
        public boolean isSsl() {
                return ssl;
        }

        /**
         * @param ssl the ssl to set
         */
        public void setSsl(boolean ssl) {
                this.ssl = ssl;
        }
}
