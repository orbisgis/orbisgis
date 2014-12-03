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
package org.gdms.data.edition;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.orbisgis.commons.progress.ProgressMonitor;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.db.DBSource;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.DefaultSchema;
import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.Schema;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.ConstraintFactory;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.AbstractDataSet;
import org.gdms.driver.DBDriver;
import org.gdms.driver.DataSet;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileDriver;
import org.gdms.driver.MemoryDriver;
import org.gdms.driver.TableDescription;
import org.gdms.driver.jdbc.ConversionRule;
import org.gdms.driver.jdbc.DefaultDBDriver;

public class ReadDriver extends DefaultDBDriver implements MemoryDriver,
        FileDriver, DBDriver {

        public static boolean failOnWrite = false;
        public static boolean failOnClose = false;
        public static boolean failOnCopy = false;
        public static boolean isEditable = false;
        private static ArrayList<String> values = new ArrayList<String>();
        private GeometryFactory gf = new GeometryFactory();
        private static ArrayList<String> newValues;
        private static DataSource currentDataSource;
        
        private boolean open = false;

        public static void initialize() {
                values.clear();
                values.add("cadena1");
                values.add("cadena2");
                values.add("cadena3");
                values.add("cadena4");

                newValues = null;

                failOnClose = false;
                failOnWrite = false;
                failOnCopy = false;
                isEditable = false;
        }

        public boolean write(DataSet dataWare, ProgressMonitor pm)
                throws DriverException {
                if (failOnWrite) {
                        throw new DriverException();
                }
                values = getContent(dataWare);

                return false;
        }

        private ArrayList<String> getContent(DataSet d) throws DriverException {
                ArrayList<String> newValues = new ArrayList<String>();
                for (int i = 0; i < d.getRowCount(); i++) {
                        newValues.add(d.getFieldValue(i, 1).getAsString());
                }
                return newValues;
        }

        @Override
        public void setDataSourceFactory(DataSourceFactory dsf) {
        }

        @Override
        public String getDriverId() {
                return "failing driver";
        }
        
        @Override
        public int getSupportedType() {
                return 0;
        }

        public int getType(String driverType) {
                return Type.STRING;
        }

        @Override
        public void open() throws DriverException {
                final Type[] fieldsTypes = new Type[]{
                        TypeFactory.createType(Type.GEOMETRY),
                        TypeFactory.createType(Type.STRING)};
                final String[] fieldsNames = new String[]{"geom", "alpha"};
                this.schema.addTable("main", new DefaultMetadata(fieldsTypes, fieldsNames));
                open = true;
        }

        @Override
        public void close() throws DriverException {
                schema.removeTable("main");
                close(null);
        }

        @Override
        public void close(Connection conn) throws DriverException {
                if (newValues != null) {
                        values = newValues;
                }

                if (failOnClose) {
                        throw new DriverException();
                }
                open = false;
        }

        @Override
        public void execute(Connection con, String sql) throws SQLException {
                if (failOnWrite) {
                        throw new SQLException();
                }
                /*
                 * this is not a real database driver. we fake the committing by
                 * accessing directly to the ds the test specified by calling
                 * setCurrentDataSource()
                 */

                try {
                        newValues = getContent(currentDataSource);
                } catch (DriverException e) {
                        throw new RuntimeException();
                }
        }

        public static void setCurrentDataSource(DataSource ds) {
                currentDataSource = ds;
        }

        @Override
        public Connection getConnection(String connStr) throws SQLException {
                return new FooConnection("alpha");
        }

        @Override
        public String getNullStatementString() {
                return null;
        }

        @Override
        public String getStatementString(long i) {
                return null;
        }

        @Override
        public String getStatementString(int i, int sqlType) {
                return null;
        }

        @Override
        public String getStatementString(double d, int sqlType) {
                return null;
        }

        @Override
        public String getStatementString(CharSequence str, int sqlType) {
                return null;
        }

        @Override
        public String getStatementString(Date d) {
                return null;
        }

        @Override
        public String getStatementString(Time t) {
                return null;
        }

        @Override
        public String getStatementString(Timestamp ts) {
                return null;
        }

        @Override
        public String getStatementString(byte[] binary) {
                return null;
        }

        @Override
        public String getStatementString(boolean b) {
                return null;
        }

        @Override
        public void createSource(DBSource source, Metadata driverMetadata)
                throws DriverException {
        }

        public void copy(File in, File out) throws IOException {
                if (failOnCopy) {
                        throw new IOException();
                }
                if (newValues != null) {
                        values = newValues;
                }
        }

        public void createSource(String path, Metadata dsm,
                DataSourceFactory dataSourceFactory) throws DriverException {
        }

        public void writeFile(File file, DataSet dataSource, ProgressMonitor pm)
                throws DriverException {
                if (failOnWrite) {
                        throw new DriverException();
                }
                newValues = getContent(dataSource);
        }

        public String getReferenceInSQL(String fieldName) {
                return null;
        }
        
        

        @Override
        public void open(Connection con, String tableName, String schemaName) throws DriverException {
                this.schema = new DefaultSchema("test");
                final Type[] fieldsTypes = new Type[]{
                        TypeFactory.createType(Type.GEOMETRY),
                        TypeFactory.createType(Type.STRING, ConstraintFactory.createConstraint(Constraint.PK))};
                final String[] fieldsNames = new String[]{"geom", "alpha"};
                this.schema.addTable("main", new DefaultMetadata(fieldsTypes, fieldsNames));
                open = true;
        }

        @Override
        public void open(Connection con, String tableName) throws DriverException {
                open(null, null, null);
        }

        @Override
        public void beginTrans(Connection con) throws SQLException {
        }

        @Override
        public void commitTrans(Connection con) throws SQLException {
        }

        @Override
        public void rollBackTrans(Connection con) throws SQLException {
        }

        @Override
        public boolean isCommitable() {
                return isEditable;
        }

        @Override
        public ConversionRule[] getConversionRules() {
                return null;
        }

        public String getChangeFieldNameSQL(String tableName, String oldName,
                String newName) {
                return null;
        }

        @Override
        public TableDescription[] getTables(Connection c) throws DriverException {
                return new TableDescription[0];
        }

        @Override
        public int getType() {
                return 0;
        }

        @Override
        public String validateMetadata(Metadata metadata) {
                return null;
        }

        @Override
        public int getDefaultPort() {
                return 0;
        }

        @Override
        public String[] getFileExtensions() {
                return new String[]{""};
        }

        @Override
        public String[] getPrefixes() {
                return new String[]{"jdbc:test"};
        }

        @Override
        public String getTypeDescription() {
                return null;
        }

        @Override
        public String getTypeName() {
                return "";
        }

        @Override
        public DataSet getTable(String name) {
                if (!name.equals("main")) {
                        return null;
                }
                return new AbstractDataSet() {

                        @Override
                        public Value getFieldValue(long rowIndex, int fieldId)
                                throws DriverException {
                                if (fieldId == 0) {
                                        return ValueFactory.createValue(gf.createPoint(new Coordinate(0, 0)));
                                } else {
                                        return ValueFactory.createValue(values.get((int) rowIndex));
                                }
                        }

                        @Override
                        public long getRowCount() throws DriverException {
                                return values.size();
                        }

                        @Override
                        public Number[] getScope(int dimension) throws DriverException {
                                return new Number[]{10, 10};
                        }

                        @Override
                        public Metadata getMetadata() throws DriverException {
                                return schema.getTableByName("main");
                        }
                };
        }

        @Override
        public void setFile(File file) {
                this.schema = new DefaultSchema("test");
                Constraint[] constraints = new Constraint[0];
                final Type[] fieldsTypes = new Type[]{
                        TypeFactory.createType(Type.GEOMETRY),
                        TypeFactory.createType(Type.STRING, constraints)};
                final String[] fieldsNames = new String[]{"geom", "alpha"};
                this.schema.addTable("main", new DefaultMetadata(fieldsTypes, fieldsNames));
        }
        
        @Override
        public Schema getSchema() throws DriverException {
                return schema;
        }

        @Override
        public boolean isOpen() {
                return open;
        }

        @Override
        public String getConnectionString(String host, int port, boolean ssl, String dbName, String user, String password) {
                return "";
        }
        
        
}
