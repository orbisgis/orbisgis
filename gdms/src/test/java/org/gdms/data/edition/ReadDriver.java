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
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DBDriver;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileDriver;
import org.gdms.driver.ObjectDriver;
import org.gdms.spatial.FID;
import org.gdms.spatial.GeometryValue;
import org.gdms.spatial.StringFid;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

public class ReadDriver implements ObjectDriver, FileDriver, DBDriver {

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

		try {
			fieldsTypes[0] = new DefaultTypeDefinition("GEOMETRY",
					Type.GEOMETRY, null).createType(null);
			fieldsTypes[1] = new DefaultTypeDefinition("STRING", Type.STRING,
					new ConstraintNames[] { ConstraintNames.PK })
					.createType(new Constraint[] { new DefaultConstraint(
							ConstraintNames.PK, "true") });
		} catch (InvalidTypeException e) {
			throw new RuntimeException("Bug in the driver", e);
		}

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

	public String getTypeInAddColumnStatement(Type driverType) {
		return null;
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

	public String getStatementString(GeometryValue g) {
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

	public void createSource(String path, Metadata dsm) throws DriverException {
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

	public Number[] getScope(int dimension, String fieldName)
			throws DriverException {
		return new Number[] { 10, 10 };
	}

	public void open(Connection con, String tableName, String orderFieldName)
			throws DriverException {
	}

	public FID getFid(long row) {
		return new StringFid(values.get((int) row));
	}

	public boolean hasFid() {
		return true;
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
		try {
			return new TypeDefinition[] {
					new DefaultTypeDefinition("STRING", Type.STRING),
					new DefaultTypeDefinition("GEOMETRY", Type.GEOMETRY) };
		} catch (InvalidTypeException e) {
			throw new DriverException("Invalid type");
		}
	}

	public String getChangeFieldNameStatement(String tableName, String oldName, String newName) {
		return null;
	}
}