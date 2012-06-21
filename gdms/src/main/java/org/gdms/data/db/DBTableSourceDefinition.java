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
package org.gdms.data.db;

import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.orbisgis.progress.ProgressMonitor;

import org.gdms.data.AbstractDataSource;
import org.gdms.data.AbstractDataSourceDefinition;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceDefinition;
import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.MetadataUtilities;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DBDriver;
import org.gdms.driver.DBReadWriteDriver;
import org.gdms.driver.DataSet;
import org.gdms.driver.DriverException;
import org.gdms.driver.DriverUtilities;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.source.directory.DbDefinitionType;
import org.gdms.source.directory.DefinitionType;

public class DBTableSourceDefinition extends AbstractDataSourceDefinition<DBDriver> {

        protected DBSource def;
        private static final Logger LOG = Logger.getLogger(DBTableSourceDefinition.class);

        /**
         * Creates a new DBTableSourceDefinition
         *
         * @param def
         */
        public DBTableSourceDefinition(DBSource def) {
                LOG.trace("Constructor");
                this.def = def;
        }

        @Override
        public DataSource createDataSource(String tableName, ProgressMonitor pm)
                throws DataSourceCreationException {
                LOG.trace("Creating datasource");

                DBDriver driver;
                try {
                        driver = getDriver();
                } catch (DriverException ex) {
                        throw new DataSourceCreationException(ex);
                }

                (driver).setDataSourceFactory(getDataSourceFactory());

                AbstractDataSource adapter = new DBTableDataSourceAdapter(
                        getSource(tableName), def, driver);
                adapter.setDataSourceFactory(getDataSourceFactory());
                LOG.trace("Datasource created");
                return adapter;
        }

        @Override
        protected DBDriver getDriverInstance() {
                return DriverUtilities.getDriver(getDataSourceFactory().getSourceManager().getDriverManager(), def.getPrefix());
        }

        public DBSource getSourceDefinition() {
                return def;
        }

        public String getPrefix() {
                return def.getPrefix();
        }

        @Override
        public void createDataSource(DataSet contents, ProgressMonitor pm)
                throws DriverException {
                LOG.trace("Writing datasource to database");
                final long rowCount = contents.getRowCount();
                pm.startTask("Writing to database", rowCount);
                DBReadWriteDriver driver = (DBReadWriteDriver) getDriver();
                driver.setDataSourceFactory(getDataSourceFactory());
                Connection con;
                try {
                        String cs = driver.getConnectionString(def.getHost(), def.getPort(), def.isSsl(), def.getDbName(), def.getUser(), def.getPassword());
                        con = driver.getConnection(cs);
                } catch (SQLException e) {
                        throw new DriverException(e);
                }

                try {
                        driver.beginTrans(con);
                } catch (SQLException e) {
                        throw new DriverException(e);
                }

                if (contents instanceof DataSource) {
                        contents = getDataSourceWithPK((DataSource) contents);
                }

                driver.createSource(def, contents.getMetadata());

                for (int i = 0; i < rowCount; i++) {
                        if (i >= 100 && i % 100 == 0) {
                                if (pm.isCancelled()) {
                                        break;
                                } else {
                                        pm.progressTo(i);
                                }
                        }
                        Value[] row = new Value[contents.getMetadata().getFieldNames().length];
                        for (int j = 0; j < row.length; j++) {
                                row[j] = contents.getFieldValue(i, j);
                        }

                        try {
                                Type[] fieldTypes = MetadataUtilities.getFieldTypes(contents.getMetadata());
                                String sqlInsert = driver.getInsertSQL(
                                        contents.getMetadata().getFieldNames(), fieldTypes, row);
                                driver.execute(con, sqlInsert);
                        } catch (SQLException e) {
                                try {
                                        driver.rollBackTrans(con);
                                } catch (SQLException e1) {
                                        LOG.error("Failed to rollback", e1);
                                        throw new DriverException(e);
                                }

                                throw new DriverException(e);
                        }
                }

                pm.progressTo(rowCount);
                try {
                        driver.commitTrans(con);
                } catch (SQLException e) {
                        throw new DriverException(e);
                }
                pm.endTask();
        }

        private DataSource getDataSourceWithPK(DataSource ds)
                throws DriverException {
                Metadata metadata = ds.getMetadata();
                for (int i = 0; i < metadata.getFieldCount(); i++) {
                        Type fieldType = metadata.getFieldType(i);
                        if (fieldType.getConstraint(Constraint.PK) != null) {
                                return ds;
                        }
                }

                return new PKDataSourceAdapter(ds);
        }

        @Override
        public DefinitionType getDefinition() {
                DbDefinitionType ret = new DbDefinitionType();
                ret.setDbName(def.getDbName());
                ret.setHost(def.getHost());
                ret.setPort(Integer.toString(def.getPort()));
                ret.setTableName(def.getTableName());
                ret.setPassword(def.getPassword());
                ret.setUser(def.getUser());
                ret.setPrefix(def.getPrefix());
                ret.setSchemaName(def.getSchemaName());

                return ret;
        }

        public static DataSourceDefinition createFromXML(DbDefinitionType definition) {
                DBSource dbSource = new DBSource(definition.getHost(), Integer.parseInt(definition.getPort()), definition.getDbName(),
                        definition.getUser(), definition.getPassword(), definition.getSchemaName(), definition.getTableName(), definition.getPrefix());
                if (definition.getSsl() != null && definition.getSsl().equals("true")) {
                        dbSource.setSsl(true);
                }
                return new DBTableSourceDefinition(dbSource);
        }

        @Override
        public boolean equals(Object obj) {
                if (obj instanceof DBTableSourceDefinition) {
                        DBTableSourceDefinition dsd = (DBTableSourceDefinition) obj;
                        return (equals(dsd.def.getDbms(), def.getDbms())
                                && equals(dsd.def.getDbName(), def.getDbName())
                                && equals(dsd.def.getHost(), def.getHost())
                                && equals(dsd.def.getPassword(), def.getPassword())
                                && (dsd.def.getPort() == def.getPort())
                                && equals(dsd.def.getUser(), def.getUser())
                                && equals(dsd.def.getTableName(), def.getTableName())
                                && equals(dsd.def.getSchemaName(), def.getSchemaName())
                                && equals(dsd.def.getPrefix(), def.getPrefix()));
                } else {
                        return false;
                }
        }

        @Override
        public int hashCode() {
                return 145 + this.def.hashCode();
        }

        private boolean equals(String str, String str2) {
                if (str == null) {
                        return true;
                } else {
                        return str.equals(str2);
                }
        }

        @Override
        public String getDriverTableName() {
                return DriverManager.DEFAULT_SINGLE_TABLE_NAME;
        }

        @Override
        public URI getURI() throws DriverException {
                StringBuilder ds = new StringBuilder();
                String cs = getDriver().getConnectionString(def.getHost(), def.getPort(), def.isSsl(), def.getDbName(), def.getUser(), def.getPassword());
                ds.append(cs);
                
                if (cs.contains("?")) {
                        if (def.getSchemaName() != null) {
                                ds.append("&schema=").append(def.getSchemaName());
                        }
                        ds.append("&table=").append(def.getTableName());
                } else {
                        ds.append("?table=").append(def.getTableName());
                        if (def.getSchemaName() != null) {
                                ds.append("&schema=").append(def.getSchemaName());
                        }
                }
                
                // substring drops the jdbc at the beginning
                URI con = URI.create(ds.toString().substring(5));
                return con;
        }
}
