/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.driver.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.WarningListener;
import org.gdms.data.indexes.IndexQuery;
import org.gdms.data.schema.DefaultSchema;
import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.SchemaMetadata;
import org.gdms.data.types.AutoIncrementConstraint;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.ForeignKeyConstraint;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.LengthConstraint;
import org.gdms.data.types.NotNullConstraint;
import org.gdms.data.types.PrecisionConstraint;
import org.gdms.data.types.PrimaryKeyConstraint;
import org.gdms.data.types.ReadOnlyConstraint;
import org.gdms.data.types.ScaleConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.DataSet;
import org.gdms.driver.TableDescription;
import org.gdms.driver.driverManager.DriverManager;

/**
 * Class with the implementation of the methods in database driver interfaces
 * that are related to JDBC
 * 
 */
public abstract class DefaultDBDriver extends DefaultSQL {

        private ResultSet resultSet;
        private long rowCount = -1;
        private Metadata metadata = null;
        private Connection conn;
        private ResultSetMetaData resultsetMetadata;
        private DataSourceFactory dsf;
        private Statement statement;
        private String orderFieldName;
        private static final Logger LOG = Logger.getLogger(DefaultDBDriver.class);

        protected Metadata getInternalMetadata() throws DriverException {
                if (metadata == null) {
                        try {
                                final int fc = resultsetMetadata.getColumnCount();
                                final Type[] fieldsTypes = new Type[fc];
                                final String[] fieldsNames = new String[fc];

                                final DatabaseMetaData dbmd = conn.getMetaData();

                                ResultSet pKSet = null;
                                final List<String> pKFieldsList = new LinkedList<String>();
                                try {
                                        pKSet = dbmd.getPrimaryKeys(null, schemaName,
                                                tableName);

                                        while (pKSet.next()) {
                                                pKFieldsList.add(pKSet.getString("COLUMN_NAME"));
                                        }
                                } finally {
                                        if (pKSet != null) {
                                                pKSet.close();
                                        }
                                }

                                ResultSet fKSet = null;
                                final List<String> fKFieldsList = new LinkedList<String>();
                                try {
                                        fKSet = dbmd.getImportedKeys(null, schemaName,
                                                tableName);

                                        while (fKSet != null && fKSet.next()) {
                                                fKFieldsList.add(pKSet.getString("COLUMN_NAME"));
                                        }
                                } finally {
                                        if (fKSet != null) {
                                                fKSet.close();
                                        }
                                }

                                for (int i = 0; i < fc; i++) {
                                        try {
                                                fieldsNames[i] = resultsetMetadata.getColumnName(i + 1);

                                                fieldsTypes[i] = getGDMSType(resultsetMetadata,
                                                        pKFieldsList, fKFieldsList, i + 1);
                                        } catch (SQLException e) {
                                                getWL().throwWarning(
                                                        "Cannot read type in field: " + i
                                                        + ". Using binary instead");
                                                fieldsTypes[i] = TypeFactory.createType(Type.BINARY,
                                                        "Unknown_field_" + i);
                                        }
                                }

                                metadata = new SchemaMetadata(schema, fieldsTypes, fieldsNames);
                        } catch (InvalidTypeException e) {
                                throw new DriverException(e);
                        } catch (SQLException e) {
                                throw new DriverException(e);
                        }
                }

                return metadata;
        }

        protected Type getGDMSType(ResultSetMetaData resultsetMetadata,
                List<String> pkFieldsList, List<String> fkFieldsList, int jdbcFieldIndex) throws SQLException,
                DriverException {
                ArrayList<Constraint> constraints = new ArrayList<Constraint>();
                int jdbcType = resultsetMetadata.getColumnType(jdbcFieldIndex);
                int precision = resultsetMetadata.getPrecision(jdbcFieldIndex);
                int scale = resultsetMetadata.getScale(jdbcFieldIndex);
                int length = resultsetMetadata.getColumnDisplaySize(jdbcFieldIndex);
                int ret = -1;
                switch (jdbcType) {
                        case Types.CHAR:
                        case Types.VARCHAR:
                        case Types.LONGVARCHAR:
                        case Types.CLOB:
                                if (Integer.MAX_VALUE != length) {
                                        constraints.add(new LengthConstraint( length));
                                }
                                ret = Type.STRING;
                                break;
                        case Types.BIGINT:
                                ret = Type.LONG;
                                break;
                        case Types.BOOLEAN:
                        case Types.BIT:
                                ret = Type.BOOLEAN;
                                break;
                        case Types.DATE:
                                ret = Type.DATE;
                                break;
                        case Types.DECIMAL:
                        case Types.NUMERIC:
                                if (precision != 0) {
                                        constraints.add(new PrecisionConstraint(precision));
                                }
                                if (scale != 0) {
                                        constraints.add(new ScaleConstraint(scale));
                                }
                                ret = Type.DOUBLE;
                                break;
                        case Types.FLOAT:
                        case Types.DOUBLE:
                                ret = Type.DOUBLE;
                                break;
                        case Types.INTEGER:
                                ret = Type.INT;
                                break;
                        case Types.REAL:
                                ret = Type.FLOAT;
                                break;
                        case Types.SMALLINT:
                                ret = Type.SHORT;
                                break;
                        case Types.TINYINT:
                                ret = Type.BYTE;
                                break;
                        case Types.BINARY:
                        case Types.VARBINARY:
                        case Types.LONGVARBINARY:
                        case Types.TIMESTAMP:
                                ret = Type.TIMESTAMP;
                                break;
                        case Types.TIME:
                                ret = Type.TIME;
                                break;
                        case Types.BLOB:
                        case Types.OTHER:
                                ret = Type.BINARY;
                                break;
                        default:
                                throw new DriverException("Couldn't map the type " + jdbcType);
                }

                constraints.addAll(addGlobalConstraints(resultsetMetadata,
                        pkFieldsList, fkFieldsList, jdbcFieldIndex));

                return TypeFactory.createType(ret, constraints.toArray(new Constraint[constraints.size()]));
        }

        protected List<Constraint> addGlobalConstraints(
                ResultSetMetaData resultsetMetadata, List<String> pkFieldsList, List<String> fkFieldsList,
                int jdbcFieldIndex) throws SQLException {
                List<Constraint> constraints = new ArrayList<Constraint>();
                if (pkFieldsList.contains(resultsetMetadata.getColumnName(jdbcFieldIndex))) {
                        constraints.add(new PrimaryKeyConstraint());
                }
                if (fkFieldsList.contains(resultsetMetadata.getColumnName(jdbcFieldIndex))) {
                        constraints.add(new ForeignKeyConstraint());
                }
                if (ResultSetMetaData.columnNoNulls == resultsetMetadata.isNullable(jdbcFieldIndex)) {
                        constraints.add(new NotNullConstraint());
                }
                if (resultsetMetadata.isReadOnly(jdbcFieldIndex)) {
                        constraints.add(new ReadOnlyConstraint());
                }
                if (resultsetMetadata.isAutoIncrement(jdbcFieldIndex)) {
                        constraints.add(new AutoIncrementConstraint());
                        constraints.add(new ReadOnlyConstraint());
                }

                return constraints;
        }

        /**
         * Gets the order by clause of an instruction that orders by the primary key
         * fields for the specified table in the default schema
         *
         * @param c
         * @param tableName
         * @return
         * @throws SQLException
         */
        protected static String getOrderFields(Connection c, String tableName)
                throws SQLException {
                return getOrderFields(c, tableName, null);
        }

        /**
         * Gets the order by clause of an instruction that orders by the primary key
         * fields for the specified table in the specified schema
         *
         * @param c
         * @param tableName
         * @param schemaName
         * @return
         * @throws SQLException
         *
         * @see DefaultDBDriver#getOrderFields(Connection, String)
         */
        protected static String getOrderFields(Connection c, String tableName,
                String schemaName) throws SQLException {
                DatabaseMetaData metadata = c.getMetaData();
                ResultSet res = null;
                StringBuilder order = new StringBuilder();
                try {
                        res = metadata.getPrimaryKeys(null, schemaName, tableName);


                        if (res.next()) {
                                order.append(res.getString("COLUMN_NAME"));
                        }
                        while (res.next()) {
                                order.append(", ").append(res.getString("COLUMN_NAME"));
                        }
                } finally {
                        if (res != null) {
                                res.close();
                        }
                }

                return order.toString();
        }

        /**
         * @see {@link DriverException.gdms.driver.DBDriver}
         *      {@link #open(Connection, String)}
         */
        @Override
        public void open(Connection con, String tableName) throws DriverException {
                open(con, tableName, null);
        }

        /**
         * @see {@link DriverException.gdms.driver.DBDriver}
         *      {@link #open(Connection, String, String)}
         */
        @Override
        public void open(Connection con, String tableName, String schemaName)
                throws DriverException {
                LOG.trace("Opening connection");
                try {
                        orderFieldName = getOrderFields(con, tableName, schemaName);

                        statement = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                                ResultSet.CONCUR_READ_ONLY,
                                ResultSet.CLOSE_CURSORS_AT_COMMIT);
                        statement.setQueryTimeout(60);
                        this.conn = con;
                        this.tableName = tableName;
                        this.schemaName = schemaName;
                        if (this.schemaName == null) {
                                final TableDescription[] tables = getTables(con);
                                if (tables == null) {
                                        throw new DriverException("Impossible to connect to the database!");
                                }
                                for (TableDescription table : tables) {
                                        if (table.getName().equals(tableName)) {
                                                this.schemaName = table.getSchema();
                                                break;
                                        }
                                }

                        }

                        schema = new DefaultSchema(tableName);
                        getData();

                        schema.addTable(DriverManager.DEFAULT_SINGLE_TABLE_NAME, getInternalMetadata());
                } catch (SQLException e) {
                        throw new DriverException(e.getMessage(), e);
                }
        }

        /**
         * catches the {@link ResultSet} and {@link ResultSetMetaData}
         *
         * @throws DriverException
         */
        protected void getData() throws DriverException {
                String sql = getSelectSQL(orderFieldName);
                try {
                        resultSet = statement.executeQuery(sql);
                        resultsetMetadata = resultSet.getMetaData();
                } catch (SQLException e) {
                        throw new DriverException("Impossible to execute the query: " + sql, e);
                }
        }

        /**
         * Gets the Select statement that will be accessed by the driver
         *
         * @param orderFieldName
         * @return
         * @throws DriverException
         */
        protected String getSelectSQL(String orderFieldName) throws DriverException {
                String sql = "SELECT * FROM " + getTableAndSchemaName();
                if (orderFieldName != null && !orderFieldName.trim().isEmpty()) {
                        sql += " ORDER BY " + orderFieldName;
                }
                return sql;
        }

        /**
         * @see org.gdms.driver.DBDriver#close(java.sql.Connection)
         */
        @Override
        public void close(Connection conn) throws DriverException {
                LOG.trace("Closing connection");
                try {
                        if (resultSet != null) {
                                resultSet.close();
                                resultsetMetadata = null;
                                statement.close();
                                conn.close();
                        }
                        resultSet = null;
                        statement = null;
                        metadata = null;
                        tableName = null;
                        schemaName = null;
                        rowCount = -1;
                } catch (SQLException e) {
                        throw new DriverException(e);
                }
        }

        /**
         * @see org.gdms.driver.ReadWriteDriver#isCommitable()
         */
        @Override
        public boolean isCommitable() {
                return orderFieldName != null;
        }

        /**
         * getter for the {@link ResultSet}
         *
         * @return
         */
        protected ResultSet getResultSet() {
                return resultSet;
        }

        /**
         * getter for the {@link ResultSetMetaData}
         *
         * @return
         */
        protected ResultSetMetaData getResultsetMetadata() {
                return resultsetMetadata;
        }

        /**
         * getter for the {@link WarningListener} of the {@link DataSourceFactory}
         *
         * @return
         */
        protected WarningListener getWL() {
                return dsf.getWarningListener();
        }

        /**
         * @see org.gdms.data.driver.DriverCommons#setDataSourceFactory(org.gdms.data.DataSourceFactory)
         */
        /**
         * @see org.gdms.driver.ReadOnlyDriver#setDataSourceFactory(org.gdms.data.DataSourceFactory)
         */
        @Override
        public void setDataSourceFactory(DataSourceFactory dsf) {
                this.dsf = dsf;
        }

        /**
         * getter for the table name
         *
         * @return
         */
        protected String getTableName() {
                return tableName;
        }

        /**
         * getter for the schema name
         *
         * @return
         */
        protected String getSchemaName() {
                return schemaName;
        }

        @Override
        public DataSet getTable(String name) {
                if (!name.equals(DriverManager.DEFAULT_SINGLE_TABLE_NAME)) {
                        return null;
                }
                return this;
        }

        @Override
        public Value getFieldValue(long rowIndex, int fieldId) throws DriverException {
                LOG.trace("Retrieving row " + rowIndex);
                Value value = null;

                try {
                        fieldId += 1;
                        resultSet.absolute((int) rowIndex + 1);
                        final int type = resultSet.getMetaData().getColumnType(fieldId);

                        switch (type) {
                                case Types.BIGINT:
                                        value = ValueFactory.createValue(resultSet.getLong(fieldId));
                                        break;

                                case Types.BIT:
                                case Types.BOOLEAN:
                                        value = ValueFactory.createValue(resultSet.getBoolean(fieldId));
                                        break;

                                case Types.CHAR:
                                case Types.VARCHAR:
                                case Types.LONGVARCHAR:
                                case Types.CLOB:
                                        String auxString = resultSet.getString(fieldId);
                                        if (auxString != null) {
                                                value = ValueFactory.createValue(auxString);
                                        }
                                        break;

                                case Types.DATE:
                                        final Date auxDate = resultSet.getDate(fieldId);
                                        if (auxDate != null) {
                                                value = ValueFactory.createValue(auxDate);
                                        }
                                        break;

                                case Types.DECIMAL:
                                case Types.NUMERIC:
                                case Types.FLOAT:
                                case Types.DOUBLE:
                                        value = ValueFactory.createValue(resultSet.getDouble(fieldId));
                                        break;

                                case Types.INTEGER:
                                        value = ValueFactory.createValue(resultSet.getInt(fieldId));
                                        break;

                                case Types.REAL:
                                        value = ValueFactory.createValue(resultSet.getFloat(fieldId));
                                        break;

                                case Types.SMALLINT:
                                        value = ValueFactory.createValue(resultSet.getShort(fieldId));
                                        break;

                                case Types.TINYINT:
                                        value = ValueFactory.createValue(resultSet.getByte(fieldId));
                                        break;

                                case Types.BINARY:
                                case Types.VARBINARY:
                                case Types.LONGVARBINARY:
                                case Types.BLOB:
                                        final byte[] auxByteArray = resultSet.getBytes(fieldId);
                                        if (auxByteArray != null) {
                                                value = ValueFactory.createValue(auxByteArray);
                                        }
                                        break;

                                case Types.TIMESTAMP:
                                        final Timestamp auxTimeStamp = resultSet.getTimestamp(fieldId);
                                        if (auxTimeStamp != null) {
                                                value = ValueFactory.createValue(auxTimeStamp);
                                        }
                                        break;

                                case Types.TIME:
                                        final Time auxTime = resultSet.getTime(fieldId);
                                        if (auxTime != null) {
                                                value = ValueFactory.createValue(auxTime);
                                        }
                                        break;

                                default:
                                        byte[] aux = resultSet.getBytes(fieldId);
                                        if (aux != null) {
                                                value = ValueFactory.createValue(aux);
                                        }
                                        break;
                        }

                        if (resultSet.wasNull()) {
                                return ValueFactory.createNullValue();
                        } else {
                                return value;
                        }
                } catch (SQLException e) {
                        getWL().throwWarning(
                                "Cannot get the value in row " + rowIndex + " field "
                                + fieldId + ". Returning null instead");
                        return ValueFactory.createNullValue();
                }
        }

        @Override
        public long getRowCount() throws DriverException {
                try {
                        if (rowCount == -1) {
                                resultSet.last();
                                rowCount = resultSet.getRow();
                        }

                        return rowCount;
                } catch (SQLException e) {
                        throw new DriverException(e);
                }
        }

        @Override
        public Number[] getScope(int dimension) throws DriverException {
                return null;
        }

        @Override
        public Metadata getMetadata() throws DriverException {
                return schema.getTableByName(tableName);
        }

        @Override
        public Value[] getRow(long rowIndex) throws DriverException {
                Value[] ret = new Value[getMetadata().getFieldCount()];

                for (int i = 0; i < ret.length; i++) {
                        ret[i] = getFieldValue(rowIndex, i);
                }

                return ret;
        }

        @Override
        public Iterator<Integer> queryIndex(DataSourceFactory dsf, IndexQuery queryIndex) throws DriverException {
                return dsf.getIndexManager().iterateUsingIndexQuery(this, queryIndex);
        }
}
