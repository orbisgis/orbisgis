/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
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
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.ConstraintNames;
import org.gdms.data.types.DefaultConstraint;
import org.gdms.data.types.DefaultTypeDefinition;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DBDriver;
import org.gdms.driver.DefaultDBDriver;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileDriver;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.TableDescription;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

public class ReadDriver extends DefaultDBDriver implements ObjectDriver,
		FileDriver, DBDriver {

	public static boolean failOnWrite = false;

	public static boolean failOnClose = false;

	public static boolean failOnCopy = false;

	public static boolean isEditable = false;

	private static ArrayList<String> values = new ArrayList<String>();

	private GeometryFactory gf = new GeometryFactory();

	private static ArrayList<String> newValues;

	private static DataSource currentDataSource;

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

	public void write(DataSource dataWare) throws DriverException {
		if (failOnWrite) {
			throw new DriverException();
		}
		values = getContent(dataWare);
	}

	private ArrayList<String> getContent(DataSource d) throws DriverException {
		ArrayList<String> newValues = new ArrayList<String>();
		for (int i = 0; i < d.getRowCount(); i++) {
			newValues.add(d.getString(i, 1));
		}
		return newValues;
	}

	public void setDataSourceFactory(DataSourceFactory dsf) {

	}

	public Metadata getMetadata() throws DriverException {
		final Type[] fieldsTypes = new Type[2];
		final String[] fieldsNames = new String[] { "geom", "alpha" };

		fieldsTypes[0] = new DefaultTypeDefinition("GEOMETRY", Type.GEOMETRY)
				.createType();
		fieldsTypes[1] = new DefaultTypeDefinition("STRING", Type.STRING,
				new ConstraintNames[] { ConstraintNames.PK })
				.createType(new Constraint[] { new DefaultConstraint(
						ConstraintNames.PK, "true") });

		return new DefaultMetadata(fieldsTypes, fieldsNames);
	}

	public String getName() {
		return null;
	}

	public int getType(String driverType) {
		return Type.STRING;
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		if (fieldId == 0) {
			return ValueFactory.createValue(gf
					.createPoint(new Coordinate(0, 0)));
		} else {
			return ValueFactory.createValue(values.get((int) rowIndex));
		}
	}

	public long getRowCount() throws DriverException {
		return values.size();
	}

	public void start() throws DriverException {

	}

	public void stop() throws DriverException {

	}

	public void close(Connection conn) throws DriverException {
		if (newValues != null) {
			values = newValues;
		}

		if (failOnClose) {
			throw new DriverException();
		}
	}

	public void execute(Connection con, String sql) throws SQLException {
		if (failOnWrite) {
			throw new SQLException();
		}
		/*
		 * this is not a real database driver. we fake the commiting by
		 * accessing directly to the ds that the test specified by calling
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

	public Connection getConnection(String host, int port, String dbName,
			String user, String password) throws SQLException {
		return new FooConnection("alpha");
	}

	public boolean prefixAccepted(String prefix) {
		return true;
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

	public String completeFileName(String fileName) {
		return fileName;
	}

	public void copy(File in, File out) throws IOException {
		if (failOnCopy) {
			throw new IOException();
		}
		if (newValues != null) {
			values = newValues;
		}
	}

	public boolean fileAccepted(File f) {
		return true;
	}

	public void open(File file) throws DriverException {
	}

	public void createSource(String path, Metadata dsm,
			DataSourceFactory dataSourceFactory) throws DriverException {
	}

	public void writeFile(File file, DataSource dataSource)
			throws DriverException {
		if (failOnWrite) {
			throw new DriverException();
		}
		newValues = getContent(dataSource);
	}

	public void close() throws DriverException {
		close(null);
	}

	public String getReferenceInSQL(String fieldName) {
		return null;
	}

	public Number[] getScope(int dimension) throws DriverException {
		return new Number[] { 10, 10 };
	}

	public void open(Connection con, String tableName) throws DriverException {
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

	public TypeDefinition[] getTypesDefinitions() throws DriverException {
		return new TypeDefinition[] {
				new DefaultTypeDefinition("STRING", Type.STRING),
				new DefaultTypeDefinition("GEOMETRY", Type.GEOMETRY) };
	}

	public String getChangeFieldNameSQL(String tableName, String oldName,
			String newName) {
		return null;
	}

	public TableDescription[] getTables(Connection c) throws DriverException {
		return null;
	}

	public int getType() {
		return 0;
	}
}