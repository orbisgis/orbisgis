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
package org.gdms.data.sql;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.orbisgis.progress.ProgressMonitor;

import org.gdms.data.AbstractDataSourceDefinition;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceDefinition;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.memory.MemoryDataSourceAdapter;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.DefaultSchema;
import org.gdms.data.schema.Schema;
import org.gdms.data.types.Type;
import org.gdms.driver.DataSet;
import org.gdms.driver.Driver;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.driver.sql.SqlStatementDriver;
import org.gdms.source.SourceManager;
import org.gdms.source.directory.DefinitionType;
import org.gdms.source.sqldirectory.SqlDefinitionType;
import org.gdms.sql.engine.SQLStatement;

/**
 * Represents the result of a SQL query
 * @author Antoine Gourlay
 */
public final class SQLSourceDefinition extends AbstractDataSourceDefinition {

        private SQLStatement statement;
        private Schema schema;
        private DefaultMetadata metadata;
        private static final Logger LOG = Logger.getLogger(SQLSourceDefinition.class);

        /**
         * Creates a new SQLSourceDefinition from a SQL statement
         * @param instruction a statement
         */
        public SQLSourceDefinition(SQLStatement instruction) {
                LOG.trace("Constructor");
                this.statement = instruction;
                schema = new DefaultSchema("SQL" + instruction.hashCode());
                metadata = new DefaultMetadata();
                schema.addTable(DriverManager.DEFAULT_SINGLE_TABLE_NAME, metadata);
        }

        @Override
        public DataSourceFactory getDataSourceFactory() {
                return (DataSourceFactory) super.getDataSourceFactory();
        }

        /**
         * Gets the GDMS file that backs up this SQLSource, or null if there is not.
         * @return
         */
        public File getFile() {
                return null;
        }

        private DataSource execute(String tableName, ProgressMonitor pm) throws DriverException,
                DataSourceCreationException {
                LOG.trace("Preparing SQLSource");
                metadata.clear();
                DataSource def = null;
                if (!pm.isCancelled()) {
                        SqlStatementDriver d = new SqlStatementDriver(statement, getDataSourceFactory());
                        def = new MemoryDataSourceAdapter(getSource(tableName), d);
                        statement.setDataSourceFactory(getDataSourceFactory());
                        statement.prepare();
                        metadata.addAll(statement.getResultMetadata());
                        statement.cleanUp();
                        LOG.trace("Built temp MemoryDataSourceAdapter with SQL Query results");
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
//				return new FileDataSourceAdapter(getSource(tableName), file,
//						new GdmsDriver(), false);
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
         * @param definitionType
         * @return
         */
        public static DataSourceDefinition createFromXML(
                SqlDefinitionType definitionType) {
                throw new UnsupportedOperationException();
        }

        @Override
        protected Driver getDriverInstance() {
                return null;
        }

        @Override
        public List<String> getSourceDependencies() throws DriverException {
                LOG.trace("Getting Source dependencies");
                ArrayList<String> ret = new ArrayList<String>();
                String[] sources = statement.getReferencedSources();
                ret.addAll(Arrays.asList(sources));

                return ret;

        }

        @Override
        public Driver getDriver() {
                return null;
        }

        /**
         * Gets the SQL statement behind this definition
         * @return
         */
        public String getSQL() {
                return statement.getSQL();
        }

        @Override
        public int getType() {
                int type = SourceManager.SQL | SourceManager.LIVE;
                if (statement != null) {
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
                return schema;
        }

        @Override
        public String calculateChecksum(DataSource openDS) throws DriverException {
                return null;
        }
}
