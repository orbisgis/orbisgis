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
package org.gdms.data.edition;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.db.DBSource;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.Schema;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DBDriver;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileDriver;
import org.gdms.driver.MemoryDriver;
import org.gdms.driver.DataSet;
import org.gdms.driver.TableDescription;
import org.gdms.driver.jdbc.ConversionRule;
import org.gdms.driver.jdbc.DefaultDBDriver;
import org.orbisgis.progress.ProgressMonitor;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.gdms.data.schema.DefaultSchema;
import org.gdms.data.types.PrimaryKeyConstraint;
import org.gdms.driver.AbstractDataSet;

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
        public static boolean pk = true;
        
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

        public void setDataSourceFactory(DataSourceFactory dsf) {
        }

        public String getDriverId() {
                return "failing driver";
        }
        
        public int getSupportedType() {
                return 0;
        }

        public int getType(String driverType) {
                return Type.STRING;
        }

        public void open() throws DriverException {
                final Type[] fieldsTypes = new Type[]{
                        TypeFactory.createType(Type.GEOMETRY),
                        TypeFactory.createType(Type.STRING)};
                final String[] fieldsNames = new String[]{"geom", "alpha"};
                this.schema.addTable("main", new DefaultMetadata(fieldsTypes, fieldsNames));
                open = true;
        }

        public void close() throws DriverException {
                schema.removeTable("main");
                close(null);
        }

        public void close(Connection conn) throws DriverException {
                if (newValues != null) {
                        values = newValues;
                }

                if (failOnClose) {
                        throw new DriverException();
                }
                open = false;
        }

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

        public Connection getConnection(String host, int port, boolean ssl, String dbName,
                String user, String password) throws SQLException {
                return new FooConnection("alpha");
        }

        public String getNullStatementString() {
                return null;
        }

        public String getStatementString(long i) {
                return null;
        }

        public String getStatementString(int i, int sqlType) {
                return null;
        }

        public String getStatementString(double d, int sqlType) {
                return null;
        }

        public String getStatementString(String str, int sqlType) {
                return null;
        }

        public String getStatementString(Date d) {
                return null;
        }

        public String getStatementString(Time t) {
                return null;
        }

        public String getStatementString(Timestamp ts) {
                return null;
        }

        public String getStatementString(byte[] binary) {
                return null;
        }

        public String getStatementString(boolean b) {
                return null;
        }

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

        public void open(Connection con, String tableName) throws DriverException {
                open();
        }

        public void beginTrans(Connection con) throws SQLException {
        }

        public void commitTrans(Connection con) throws SQLException {
        }

        public void rollBackTrans(Connection con) throws SQLException {
        }

        public boolean isCommitable() {
                return isEditable;
        }

        public ConversionRule[] getConversionRules() {
                return null;
        }

        public String getChangeFieldNameSQL(String tableName, String oldName,
                String newName) {
                return null;
        }

        public TableDescription[] getTables(Connection c) throws DriverException {
                return new TableDescription[0];
        }

        public int getType() {
                return 0;
        }

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
                if (pk) {
                        constraints = new Constraint[]{new PrimaryKeyConstraint()};
                }
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
        
        
}
