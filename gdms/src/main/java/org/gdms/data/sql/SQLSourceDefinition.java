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
 * Copyright (C) 2007-2014 IRSTV FR CNRS 2488
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
package org.gdms.data.sql;

import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.orbisgis.progress.ProgressMonitor;

import org.gdms.data.AbstractDataSourceDefinition;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceDefinition;
import org.gdms.data.memory.MemoryDataSourceAdapter;
import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.Schema;
import org.gdms.data.types.Type;
import org.gdms.driver.DataSet;
import org.gdms.driver.DriverException;
import org.gdms.driver.MemoryDriver;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.driver.sql.SqlStatementDriver;
import org.gdms.source.SourceManager;
import org.gdms.source.directory.DefinitionType;
import org.gdms.source.sqldirectory.SqlDefinitionType;
import org.gdms.sql.engine.SQLStatement;

/**
 * Represents the result of a SQL query
 *
 * @author Antoine Gourlay
 */
public final class SQLSourceDefinition extends AbstractDataSourceDefinition<MemoryDriver> {

        private SQLStatement statement;
        private static final Logger LOG = Logger.getLogger(SQLSourceDefinition.class);

        /**
         * Creates a new SQLSourceDefinition from a SQL statement
         *
         * @param instruction a statement
         */
        public SQLSourceDefinition(SQLStatement instruction) {
                LOG.trace("Constructor");
                this.statement = instruction;
        }

        /**
         * Gets the GDMS file that backs up this SQLSource, or null if there is not.
         *
         * @return
         */
        public File getFile() {
                return null;
        }

        private DataSource execute(String tableName, ProgressMonitor pm) throws DriverException,
                DataSourceCreationException {
                LOG.trace("Preparing SQLSource");
                DataSource def = null;
                if (!pm.isCancelled()) {
                        def = new MemoryDataSourceAdapter(getSource(tableName), getDriver());
                        LOG.trace("Built temp MemoryDataSourceAdapter as an SQL view");
                }
                return def;
        }

        @Override
        public DataSource createDataSource(String tableName, ProgressMonitor pm)
                throws DataSourceCreationException {
                LOG.trace("Creating datasource");
                try {
                        DataSource def = execute(tableName, pm);
                        if (pm.isCancelled()) {
                                return null;
                        } else {
                                LOG.trace("Datasource created");
                                return def;
                        }
                } catch (DriverException e) {
                        throw new DataSourceCreationException(
                                "Cannot instantiate the source", e);
                }
        }

        @Override
        public void createDataSource(DataSet contents, ProgressMonitor pm)
                throws DriverException {
                throw new DriverException("Read only source");
        }

        @Override
        public DefinitionType getDefinition() {
                SqlDefinitionType ret = new SqlDefinitionType();
                ret.setSql(getSQL());
                return ret;
        }

        /**
         * Builds this definition from XML
         *
         * @param definitionType
         * @return
         */
        public static DataSourceDefinition createFromXML(
                SqlDefinitionType definitionType) {
                throw new UnsupportedOperationException();
        }

        @Override
        protected MemoryDriver getDriverInstance() throws DriverException {
                return new SqlStatementDriver(statement, getDataSourceFactory());
        }

        @Override
        public List<String> getSourceDependencies() throws DriverException {
                LOG.trace("Getting Source dependencies");
                return Collections.unmodifiableList(Arrays.asList(statement.getReferencedSources()));
        }

        /**
         * Gets the SQL statement behind this definition
         *
         * @return
         */
        public String getSQL() {
                return statement.getSQL();
        }

        @Override
        public int getType() throws DriverException {
                int type = SourceManager.SQL | SourceManager.LIVE;
                Metadata metadata = statement.getResultMetadata();
                if (metadata != null) {
                        for (int i = 0; i < metadata.getFieldCount(); i++) {
                                int typeCode = metadata.getFieldType(i).getTypeCode();
                                if (typeCode == Type.GEOMETRY) {
                                        type |= SourceManager.VECTORIAL;
                                } else if (typeCode == Type.RASTER) {
                                        type |= SourceManager.RASTER;
                                }
                        }
                }
                return type;
        }

        @Override
        public String getTypeName() {
                return "SQL";
        }

        @Override
        public boolean equals(Object obj) {
                if (obj instanceof SQLSourceDefinition) {
                        SQLSourceDefinition dsd = (SQLSourceDefinition) obj;
                        final String sql = getSQL();
                        return sql.equals(dsd.getSQL());
                } else {
                        return false;
                }
        }

        @Override
        public String getDriverTableName() {
                return DriverManager.DEFAULT_SINGLE_TABLE_NAME;
        }

        @Override
        public int hashCode() {
                return 751 + getSQL().hashCode();
        }

        /**
         * Deletes the GDMS file backing this SQL source, if one exists.
         */
        @Override
        public void delete() {
        }

        @Override
        public Schema getSchema() throws DriverException {
                return getDriver().getSchema();
        }

        @Override
        public String calculateChecksum(DataSource openDS) throws DriverException {
                return null;
        }

        /**
         * {@inheritDoc }
         * <p>
         * There is no URI associated with this definition: this method always returns null.
         * </p>
         * @return null
         */
        @Override
        public URI getURI() {
                return null;
        }
}
