/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.gdms.data.sql;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;
import org.gdms.data.AbstractDataSourceDefinition;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceDefinition;
import org.gdms.data.SQLDataSourceFactory;
import org.gdms.data.object.ObjectDataSourceAdapter;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.DefaultSchema;

import org.gdms.data.schema.Schema;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.gdms.driver.Driver;
import org.gdms.driver.DataSet;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.driver.generic.GenericObjectDriver;
import org.gdms.source.SourceManager;
import org.gdms.source.directory.DefinitionType;
import org.gdms.source.sqldirectory.SqlDefinitionType;
import org.gdms.sql.engine.SQLEngine;
import org.gdms.sql.engine.SqlStatement;
import org.gdms.sql.engine.ParseException;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.utils.FileUtils;

/**
 * Represents the result of a SQL query
 * @author Antoine Gourlay
 */
public final class SQLSourceDefinition extends AbstractDataSourceDefinition {

        private SqlStatement statement;
        private String tempSQL;
        private File file = null;
        private Schema schema;
        private DefaultMetadata metadata;
        private static final Logger LOG = Logger.getLogger(SQLSourceDefinition.class);

        /**
         * Creates a new SQLSourceDefinition from a SQL statement
         * @param instruction a statement
         */
        public SQLSourceDefinition(SqlStatement instruction) {
                LOG.trace("Constructor");
                this.statement = instruction;
                schema = new DefaultSchema("SQL" + instruction.hashCode());
                metadata = new DefaultMetadata();
                schema.addTable(DriverManager.DEFAULT_SINGLE_TABLE_NAME, metadata);
        }

        /**
         * Creates a new SQLSourceDefinition from a SQL script
         * @param sql a script
         */
        private SQLSourceDefinition(String sql) {
                LOG.trace("Constructor");
                this.tempSQL = sql;
        }

        @Override
        public SQLDataSourceFactory getDataSourceFactory() {
                return (SQLDataSourceFactory) super.getDataSourceFactory();
        }

        /**
         * Gets the GDMS file that backs up this SQLSource, or null if there is not.
         * @return
         */
        public File getFile() {
                return file;
        }

        private DataSource execute(String tableName, ProgressMonitor pm) throws DriverException,
                DataSourceCreationException {
                LOG.trace("Executing SQLSource");
                getDataSourceFactory().fireInstructionExecuted(statement.getSQL());
                statement.prepare(getDataSourceFactory());
                DataSet source = statement.execute();
                DataSource def = null;
                if (!pm.isCancelled()) {
                        if (source == null) {
                                throw new IllegalArgumentException(
                                        "The query produces no result: " + statement.getSQL());
                        } else {
                                GenericObjectDriver d = new GenericObjectDriver(source, true);
                                d.setCommitable(false);
                                def = new ObjectDataSourceAdapter(getSource(tableName), d);
                                LOG.trace("Built temp ObjectDataSourceAdapter with SQL Query results");
                        }
                }

//                statement.operationFinished();
                metadata.clear();
                metadata.addAll(source.getMetadata());
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
                SQLSourceDefinition sqlSourceDefinition = new SQLSourceDefinition(
                        definitionType.getSql());
                return sqlSourceDefinition;
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
                return statement == null ? tempSQL : statement.getSQL();
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
        public void initialize() throws DriverException {
                if (statement == null) {
                        SQLEngine engine = new SQLEngine(getDataSourceFactory());
                        try {
                                statement = engine.parse(tempSQL)[0];
                        } catch (ParseException ex) {
                                throw new DriverException(ex);
                        }
                }
                LOG.trace("Initializing source");
        }

        @Override
        public boolean equals(Object obj) {
                if (obj instanceof SQLSourceDefinition) {
                        SQLSourceDefinition dsd = (SQLSourceDefinition) obj;
                        final String sql = getSQL();
                        if (sql != null) {
                                return sql.equals(dsd.getSQL());
                        } else {
                                return statement.equals(dsd.statement);
                        }
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
                FileUtils.deleteFile(file);
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
