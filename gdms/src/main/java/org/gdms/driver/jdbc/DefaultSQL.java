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
package org.gdms.driver.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.vividsolutions.jts.geom.Geometry;
import org.apache.log4j.Logger;

import org.gdms.data.db.DBSource;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.DefaultSchema;
import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.Schema;
import org.gdms.data.types.AutoIncrementConstraint;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueWriter;
import org.gdms.driver.AbstractDataSet;
import org.gdms.driver.DBReadWriteDriver;
import org.gdms.driver.DriverException;
import org.gdms.driver.TableDescription;
import org.gdms.driver.driverManager.DriverManager;

/**
 * class that implements the methods of the database drivers related to SQL
 * 
 * @author Fernando Gonzalez Cortes
 * 
 */
public abstract class DefaultSQL extends AbstractDataSet implements DBReadWriteDriver, ValueWriter {

        protected String tableName;
        protected String schemaName;
        protected Schema schema = new DefaultSchema("DB" + this.hashCode());
        private Metadata metadata;
        private ValueWriter valueWriter = ValueWriter.DEFAULTWRITER;
        private static final Logger LOG = Logger.getLogger(DefaultSQL.class);

        public DefaultSQL() {
                metadata = new DefaultMetadata();
                schema.addTable(DriverManager.DEFAULT_SINGLE_TABLE_NAME, metadata);
        }

        /**
         * @return "schemaName"."tableName" or "tableName" if any schema have been
         *         specified or "" if neither schema nor table have been specified.
         *
         *         NOTA : Current schema and table are specified by using open or
         *         createSource methods. open or createSource methods must have been
         *         called before.
         *
         * @see org.gdms.driver.DBDriver.{@link #open(Connection, String)}
         * @see org.gdms.driver.DBDriver.{@link #open(Connection, String)}
         * @see org.gdms.driver.DBDriver.{@link #createSource(DBSource, Metadata)}
         */
        protected String getTableAndSchemaName() {
                String tableAndSchemaName = "";
                if (tableName != null && tableName.length() != 0) {
                                tableAndSchemaName = tableName;
                                if (schemaName != null && schemaName.length() != 0) {
                                        tableAndSchemaName = schemaName + ".\"" + tableName + "\"";
                                }
                }
                return tableAndSchemaName;
        }

        @Override
        public String getInsertSQL(String[] fieldNames, Type[] fieldTypes,
                Value[] row) throws DriverException {
                StringBuilder sql = new StringBuilder();
                sql.append("INSERT INTO ").append(getTableAndSchemaName()).append(
                        " (\"").append(fieldNames[0]);

                for (int i = 1; i < fieldNames.length; i++) {
                        sql.append("\", \"").append(fieldNames[i]);
                }

                sql.append("\") VALUES(");

                String separator = "";

                for (int i = 0; i < row.length; i++) {
                        if (isAutoNumerical(fieldTypes[i])) {
                                sql.append(separator).append(getAutoIncrementDefaultValue());
                        } else {
                                sql.append(separator).append(row[i].getStringValue(this));
                        }
                        separator = ", ";
                }

                return sql.append(")").toString();
        }

        @Override
        public String getUpdateSQL(String[] pkNames, Value[] pkValues,
                String[] fieldNames, Type[] fieldTypes, Value[] row)
                throws DriverException {
                StringBuilder sql = new StringBuilder();
                sql.append("UPDATE ").append(getTableAndSchemaName()).append(" SET ");
                String separator = "";
                for (int i = 0; i < fieldNames.length; i++) {
                        if (isAutoNumerical(fieldTypes[i])) {
                                continue;
                        } else {
                                String fieldValue = row[i].getStringValue(this);
                                sql.append(separator).append("\"").append(fieldNames[i]).append("\" = ").append(fieldValue);
                                separator = ", ";
                        }
                }

                sql.append(" WHERE \"").append(pkNames[0]).append("\" = ").append(
                        pkValues[0].getStringValue(this));

                for (int i = 1; i < pkNames.length; i++) {
                        sql.append(" AND \"").append(pkNames[0]).append("\" = ").append(
                                pkValues[0].getStringValue(this));
                }

                return sql.toString();
        }

        @Override
        public void rollBackTrans(Connection con) throws SQLException {
                LOG.trace("Transaction rollback");
                execute(con, "ROLLBACK;");
        }

        @Override
        public String[] getSchemas(Connection c) throws DriverException {
                DatabaseMetaData md;
                ResultSet rs;
                ArrayList<String> schemas = new ArrayList<String>();
                try {
                        md = c.getMetaData();
                        rs = md.getSchemas();
                        while (rs.next()) {

                                schemas.add(rs.getString("TABLE_SCHEM"));
                        }
                        rs.close();
                } catch (SQLException e) {
                        throw new DriverException(e);
                }
                return schemas.toArray(new String[schemas.size()]);
        }

        @Override
        public TableDescription[] getTables(Connection c, String catalog,
                String schemaPattern, String tableNamePattern, String[] types)
                throws DriverException {

                // Retrieves the name, schema, and type of each Database.

                DatabaseMetaData md;
                ResultSet rs;
                ArrayList<TableDescription> tables = new ArrayList<TableDescription>();

                try {
                        md = c.getMetaData();
                        rs = md.getTables(catalog, schemaPattern, tableNamePattern, types);
                        while (rs.next()) {
                                tables.add(new TableDescription(rs.getString("TABLE_NAME"), rs.getString("TABLE_TYPE"), rs.getString("TABLE_SCHEM")));
                        }
                        rs.close();
                } catch (SQLException e) {
                        throw new DriverException(e);
                }

                return tables.toArray(new TableDescription[tables.size()]);
        }

        @Override
        public TableDescription[] getTables(Connection c) throws DriverException {
                String[] types = {"TABLE", "VIEW"};
                return getTables(c, null, null, null, types);
        }

        @Override
        public TypeDefinition[] getTypesDefinitions() {
                return getConversionRules();
        }

        @Override
        public String getNullStatementString() {
                return valueWriter.getNullStatementString();
        }

        @Override
        public String getStatementString(boolean b) {
                return valueWriter.getStatementString(b);
        }

        @Override
        public String getStatementString(byte[] binary) {
                return valueWriter.getStatementString(binary);
        }

        @Override
        public String getStatementString(Date d) {
                return valueWriter.getStatementString(d);
        }

        @Override
        public String getStatementString(double d, int sqlType) {
                return valueWriter.getStatementString(d, sqlType);
        }

        @Override
        public String getStatementString(Geometry g) {
                return valueWriter.getStatementString(g);
        }

        @Override
        public String getStatementString(int i, int sqlType) {
                return valueWriter.getStatementString(i, sqlType);
        }

        @Override
        public String getStatementString(long i) {
                return valueWriter.getStatementString(i);
        }

        @Override
        public String getStatementString(CharSequence str, int sqlType) {
                return valueWriter.getStatementString(str, sqlType);
        }

        @Override
        public String getStatementString(Time t) {
                return valueWriter.getStatementString(t);
        }

        @Override
        public String getStatementString(Timestamp ts) {
                return valueWriter.getStatementString(ts);
        }

        @Override
        public void beginTrans(Connection con) throws SQLException {
                LOG.trace("Beginning transaction");
                execute(con, "BEGIN;");
        }

        @Override
        public void commitTrans(Connection con) throws SQLException {
                LOG.trace("Commiting transaction");
                execute(con, "COMMIT;");
        }

        private ConversionRule getSuitableRule(Type fieldType)
                throws DriverException {
                ConversionRule[] rules = getConversionRules();
                ConversionRule rule = null;
                for (ConversionRule typeDefinition : rules) {
                        if (typeDefinition.canApply(fieldType)) {
                                rule = typeDefinition;
                                break;
                        }
                }
                if (rule == null) {
                        throw new DriverException(getTypeName() + " doesn't accept "
                                + TypeFactory.getTypeName(fieldType.getTypeCode())
                                + " types");
                } else {
                        return rule;
                }
        }

        /**
         * Gets the conversion rules.
         *
         * @return the conversion rules.
         */
        protected abstract ConversionRule[] getConversionRules();

        @Override
        public void createSource(DBSource source, Metadata metadata)
                throws DriverException {
                LOG.trace("Creating source table");
                StringBuilder sql = new StringBuilder();
                Connection c = null;

                this.metadata = metadata;
                this.tableName = source.getTableName();
                this.schemaName = source.getSchemaName();

                try {
                        try {
                                String cs = getConnectionString(source.getHost(), source.getPort(), source.isSsl(), source.getDbName(), source.getUser(), source.getPassword());
                                c = getConnection(cs);
                                beginTrans(c);
                                sql.append(getCreateTableKeyWord()).append(" ");
                                sql.append(getTableAndSchemaName());
                                sql.append(" (");
                                final int fc = metadata.getFieldCount();
                                String separator = "";

                                for (int i = 0; i < fc; i++) {
                                        String fieldName = "\"" + metadata.getFieldName(i) + "\"";
                                        Type fieldType = metadata.getFieldType(i);
                                        ConversionRule rule = getSuitableRule(fieldType);
                                        String fieldDefinition = rule.getSQL(fieldName, fieldType);

                                        if (fieldDefinition != null) {
                                                sql.append(separator).append(fieldDefinition);
                                                separator = ", ";
                                        } else {
                                                continue;
                                        }
                                }

                                sql.append(");");

                                sql.append(getPostCreateTableSQL(metadata));
                                commitTrans(c);
                                Statement st = null;
                                try {
                                        st = c.createStatement();
                                        st.execute(sql.toString());
                                } finally {
                                        st.close();
                                }
                                LOG.trace("Source table " + this.tableName + " created");
                        } catch (SQLException e1) {
                                if (c != null) {
                                        try {
                                                rollBackTrans(c);
                                        } catch (SQLException e) {
                                                LOG.error("Failed to rollback", e);
                                                throw new DriverException(sql.toString() + ": " + e1.getMessage(),
                                                        e1);
                                        }
                                }
                                throw new DriverException(sql.toString() + ": " + e1.getMessage(),
                                        e1);
                        }
                } finally {
                        if (c != null) {
                                try {
                                        c.close();
                                } catch (SQLException ex) {
                                        throw new DriverException(sql.toString() + ": " + ex.getMessage(),
                                                ex);
                                }
                        }
                }

        }

        protected String getCreateTableKeyWord() {
                return "CREATE TABLE";
        }

        /**
         * Gets the instructions to execute after a table creation
         *
         * @param metadata
         * @return
         * @throws DriverException
         */
        protected String getPostCreateTableSQL(Metadata metadata)
                throws DriverException {
                // Nothing by default
                return "";
        }

        @Override
        public void execute(Connection con, String sql) throws SQLException {
                LOG.trace("Executing SQL Statement againt db :\n" + sql);
                Statement st = con.createStatement();
                st.execute(sql);
                st.close();
        }

        /**
         * If the type is increased automatically
         *
         * @param type
         * @return
         * @throws DriverException
         */
        protected boolean isAutoNumerical(Type type) throws DriverException {
                AutoIncrementConstraint c = (AutoIncrementConstraint) type.getConstraint(Constraint.AUTO_INCREMENT);
                return c != null;
        }

        /**
         * Gets the value to show in insert statements for autoincrement fields
         *
         * @return
         */
        protected String getAutoIncrementDefaultValue() {
                return "DEFAULT";
        }

        @Override
        public String getAddFieldSQL(String fieldName, Type fieldType)
                throws DriverException {
                ConversionRule rule = getSuitableRule(fieldType);
                return "ALTER TABLE " + getTableAndSchemaName() + " ADD "
                        + rule.getSQL(fieldName, fieldType);
        }

        @Override
        public String getChangeFieldNameSQL(String oldName, String newName)
                throws DriverException {
                return "ALTER TABLE " + getTableAndSchemaName() + " RENAME COLUMN "
                        + oldName + " TO " + newName;
        }

        @Override
        public String getDeleteFieldSQL(String fieldName) throws DriverException {
                return "ALTER TABLE " + getTableAndSchemaName() + " DROP COLUMN "
                        + fieldName;
        }

        @Override
        public String getDeleteRecordSQL(String[] names, Value[] pks)
                throws DriverException {
                // Delete sql statement
                StringBuffer sql = new StringBuffer("DELETE FROM ").append(
                        getTableAndSchemaName()).append(" WHERE ").append(names[0]).append("=").append(pks[0].getStringValue(this));

                for (int i = 1; i < pks.length; i++) {
                        sql.append(" AND ").append(names[i]).append('=').append(
                                pks[i].getStringValue(this));
                }

                return sql.toString();
        }

        @Override
        public Schema getSchema() throws DriverException {
                return schema;
        }

        @Override
        public DriverException[] getLastNonBlockingErrors() {
                return new DriverException[0];
        }
}
