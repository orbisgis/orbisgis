package org.gdms.driver.h2;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.metadata.MetadataUtilities;
import org.gdms.data.types.AutoIncrementConstraint;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.ConstraintNames;
import org.gdms.data.types.DefaultTypeDefinition;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.LengthConstraint;
import org.gdms.data.types.NotNullConstraint;
import org.gdms.data.types.PrecisionConstraint;
import org.gdms.data.types.PrimaryKeyConstraint;
import org.gdms.data.types.ReadOnlyConstraint;
import org.gdms.data.types.ScaleConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.types.UniqueConstraint;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;

/**
 * DBDrivers helper class
 */
public class H2Support {
	public static final String CHAR = "CHAR";

	public static final String VARCHAR = "VARCHAR";

	public static final String LONGVARCHAR = "LONGVARCHAR";

	public static final String BIGINT = "BIGINT";

	public static final String BOOLEAN = "BOOLEAN";

	public static final String DATE = "DATE";

	public static final String DECIMAL = "DECIMAL";

	public static final String NUMERIC = "NUMERIC";

	public static final String FLOAT = "FLOAT";

	public static final String DOUBLE = "DOUBLE";

	public static final String INTEGER = "INTEGER";

	public static final String REAL = "REAL";

	public static final String SMALLINT = "SMALLINT";

	public static final String TINYINT = "TINYINT";

	public static final String BINARY = "BINARY";

	public static final String VARBINARY = "VARBINARY";

	public static final String LONGVARBINARY = "LONGVARBINARY";

	public static final String TIMESTAMP = "TIMESTAMP";

	public static final String TIME = "TIME";

	public static final String BIT = "BIT";

	public static final String PRECISION = "PRECISION";

	public static final String LENGTH = "LENGTH";

	public static final String SCALE = "SCALE";

	public static WKBReader wkbreader = new WKBReader();


	private static Map<Integer, String> typesDescription = new HashMap<Integer, String>();

	private ResultSet resultSet;

	private int rowCount = -1;

	private ResultSetMetaData rsmd;

	static {
		typesDescription.put(Type.BINARY, "binary");
		typesDescription.put(Type.BOOLEAN, "boolean");
		typesDescription.put(Type.BYTE, "tinyint");
		typesDescription.put(Type.DATE, "date");
		typesDescription.put(Type.DOUBLE, "double");
		typesDescription.put(Type.FLOAT, "real");
		typesDescription.put(Type.GEOMETRY, "geometry");
		typesDescription.put(Type.INT, "binary");
		typesDescription.put(Type.LONG, "binary");
		typesDescription.put(Type.SHORT, "short");
		typesDescription.put(Type.STRING, "varchar");
		typesDescription.put(Type.TIME, "time");
		typesDescription.put(Type.TIMESTAMP, "timestamp");
	}

	/**
	 * Creates a new JDBCSupport object.
	 *
	 * @param r
	 *            ResultSet that will be used to return the methods values
	 * @param data
	 */
	private H2Support(ResultSet r, DatabaseMetaData dbmd) {
		resultSet = r;
	}

	/**
	 * @see org.gdms.driver.ReadAccess#getFieldValue(long, int)
	 */
	public Value getFieldValue(final long rowIndex, int fieldId)
			throws DriverException {
		Value value = null;

		try {
			fieldId += 1;
			resultSet.absolute((int) rowIndex + 1);
			final int type = resultSet.getMetaData().getColumnType(fieldId);
			ResultSetMetaData rmsd = resultSet.getMetaData();

			String typeName = rsmd.getColumnTypeName(fieldId);


			if ((type == -3)
					&& rsmd.getColumnTypeName(fieldId).equalsIgnoreCase("geometry")) {

				Geometry geom = null;
				try {
					geom = wkbreader.read(resultSet.getBytes(fieldId));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				value = ValueFactory.createValue(geom);
			}

			else {


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
				auxString = resultSet.getString(fieldId);
				if (auxString != null) {
					value = ValueFactory.createValue(auxString);
				}
				break;
			}
			}

			if (resultSet.wasNull()) {
				return ValueFactory.createNullValue();
			} else {
				return value;
			}
		} catch (SQLException e) {
			throw new DriverException(e);
		}
	}

	public int getFieldType(int i) throws DriverException {
		try {
			return resultSet.getMetaData().getColumnType(i + 1);
		} catch (SQLException e) {
			throw new DriverException(e);
		}
	}

	/**
	 * @see org.gdms.driver.ReadAccess#getFieldCount()
	 */
	public int getFieldCount() throws DriverException {
		try {
			return resultSet.getMetaData().getColumnCount();
		} catch (SQLException e) {
			throw new DriverException(e);
		}
	}

	/**
	 * @see org.gdms.driver.ReadAccess#getFieldName(int)
	 */
	public String getFieldName(final int fieldId) throws DriverException {
		try {
			return resultSet.getMetaData().getColumnName(fieldId + 1);
		} catch (SQLException e) {
			throw new DriverException(e);
		}
	}

	/**
	 * @see org.gdms.driver.ReadAccess#getRowCount()
	 */
	public long getRowCount() throws DriverException {
		try {
			if (rowCount == -1) {
				resultSet.last();
				rowCount = resultSet.getRow();
			}

			return rowCount;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Closes the internal data source
	 *
	 * @throws SQLException
	 *             if the operation fails
	 */
	public void close() throws SQLException {
		resultSet.close();
	}

	/**
	 * Creates a new JDBCSuuport object with the data retrieved from the
	 * connection with the given sql
	 *
	 * @param con
	 *            Connection to the database
	 * @param sql
	 *            SQL defining the data to use
	 *
	 * @return JDBCSupport
	 *
	 * @throws SQLException
	 *             If the data cannot be retrieved
	 */
	public static H2Support newJDBCSupport(Connection con, String tableName,
			String orderFieldName) throws SQLException {
		String sql = "SELECT * FROM " + tableName;
		if (orderFieldName != null) {
			sql += " ORDER BY " + orderFieldName;
		}

		Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		ResultSet res = st.executeQuery(sql);

		return new H2Support(res, con.getMetaData());
	}

	/**
	 * Executes a query with the 'con' connection
	 *
	 * @param con
	 *            connection
	 * @param sql
	 *            instruction to execute
	 *
	 * @throws SQLException
	 *             if execution fails
	 */
	public static void execute(Connection con, String sql) throws SQLException {
		Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_UPDATABLE);
		st.execute(sql);
		st.close();
	}

	/**
	 * @return
	 */
	public ResultSet getResultSet() {
		return resultSet;
	}

	private int getSqlFieldType(final int fieldId) throws DriverException {
		try {
			return resultSet.getMetaData().getColumnType(fieldId + 1);
		} catch (SQLException e) {
			throw new DriverException(e);
		}
	}

	public Metadata getMetadata(Connection c, String tableName)
			throws DriverException {
		final int fc = getFieldCount();
		final Type[] fieldsTypes = new Type[fc];
		final String[] fieldsNames = new String[fc];

		try {
			final DatabaseMetaData dbmd = c.getMetaData();
			rsmd = resultSet.getMetaData();
			final ResultSet pKSet = dbmd.getPrimaryKeys(null, null, tableName);
			final List<String> pKFieldsList = new LinkedList<String>();
			while (pKSet.next()) {
				pKFieldsList.add(pKSet.getString("COLUMN_NAME"));
			}

			for (int i = 0; i < fc; i++) {
				fieldsNames[i] = getFieldName(i);

				final int sqlFieldType = getSqlFieldType(i);
				final String driverType = typesDescription.get(sqlFieldType);
				 int type = getType(sqlFieldType);
				final Map<ConstraintNames, Constraint> lc = new HashMap<ConstraintNames, Constraint>();


				if ((type == Type.BINARY)
						&& rsmd.getColumnTypeName(i+1).equalsIgnoreCase(
								"geometry")) {
					type = Type.GEOMETRY;


				}
				if (pKFieldsList.contains(fieldsNames[i])) {
					lc.put(ConstraintNames.PK, new PrimaryKeyConstraint());
				}
				if (ResultSetMetaData.columnNoNulls == rsmd.isNullable(i + 1)) {
					lc.put(ConstraintNames.NOT_NULL, new NotNullConstraint());
				}
				if (rsmd.isReadOnly(i + 1)) {
					lc.put(ConstraintNames.READONLY, new ReadOnlyConstraint());
				}
				if (rsmd.isAutoIncrement(i + 1)) {
					lc.put(ConstraintNames.AUTO_INCREMENT,
							new AutoIncrementConstraint());
				}
				if (0 < rsmd.getPrecision(i + 1)) {
					lc.put(ConstraintNames.PRECISION, new PrecisionConstraint(
							rsmd.getPrecision(i + 1)));
				}
				if (0 < rsmd.getScale(i + 1)) {
					lc.put(ConstraintNames.SCALE, new ScaleConstraint(rsmd
							.getScale(i + 1)));
				}
				if (0 < rsmd.getColumnDisplaySize(i + 1)) {
					// TODO should I do another test ? What does length
					// constraint means in case of a boolean type eg ?
					lc.put(ConstraintNames.LENGTH, new LengthConstraint(rsmd
							.getColumnDisplaySize(i + 1)));
				}

				final ConstraintNames[] constraintNames = new ConstraintNames[lc
						.size()];
				lc.keySet().toArray(constraintNames);

				final Constraint[] constraints = new Constraint[lc.size()];
				lc.values().toArray(constraints);

				final TypeDefinition typeDefinition = new DefaultTypeDefinition(
						driverType, type, constraintNames);

				fieldsTypes[i] = typeDefinition.createType(constraints);
			}
		} catch (InvalidTypeException e) {
			throw new DriverException(e);
		} catch (SQLException e) {
			throw new DriverException(e);
		}
		return new DefaultMetadata(fieldsTypes, fieldsNames);
	}

	public static int getType(int jdbcType) {
		switch (jdbcType) {
		case Types.CHAR:
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
			return Type.STRING;
		case Types.BIGINT:
			return Type.LONG;
		case Types.BOOLEAN:
		case Types.BIT:
			return Type.BOOLEAN;
		case Types.DATE:
			return Type.DATE;
		case Types.DECIMAL:
		case Types.NUMERIC:
		case Types.FLOAT:
		case Types.DOUBLE:
			return Type.DOUBLE;
		case Types.INTEGER:
			return Type.INT;
		case Types.REAL:
			return Type.FLOAT;
		case Types.SMALLINT:
			return Type.SHORT;
		case Types.TINYINT:
			return Type.BYTE;
		case Types.BINARY:
		case Types.VARBINARY:
		case Types.LONGVARBINARY:
			return Type.BINARY;
		case Types.TIMESTAMP:
			return Type.TIMESTAMP;
		case Types.TIME:
			return Type.TIME;
		}

		throw new RuntimeException("Where this driver type come from? "
				+ jdbcType);
	}

	public static String[] getAvailableTypes(Connection connection)
			throws SQLException {
		final Set<String> ret = new HashSet<String>();
		final ResultSet rs = connection.getMetaData().getTypeInfo();
		rs.first();
		while (!rs.isAfterLast()) {
			String typeDescription = typesDescription.get(rs
					.getInt("DATA_TYPE"));
			if (typeDescription != null) {
				ret.add(typeDescription);
			}
			rs.next();
		}

		return ret.toArray(new String[0]);
	}

	public static String[] getDefaultSQLTypes() {
		return new String[] { CHAR, VARCHAR, LONGVARCHAR, BIGINT, BOOLEAN, BIT,
				DATE, DECIMAL, NUMERIC, FLOAT, DOUBLE, INTEGER, REAL, SMALLINT,
				TINYINT, BINARY, VARBINARY, LONGVARBINARY, TIMESTAMP, TIME };
	}

	public static String[] getDefaultSQLParameters(String driverType) {
		if (driverType.equals(CHAR) || driverType.equals(VARCHAR)
				|| driverType.equals(LONGVARCHAR)) {
			return new String[] { LENGTH };
		} else if (driverType.equals(DECIMAL) || driverType.equals(NUMERIC)) {
			return new String[] { PRECISION, SCALE };
		}

		return new String[0];
	}

	public static String[] getParameters(Connection connection,
			String driverType) throws SQLException {
		ResultSet rs = connection.getMetaData().getTypeInfo();
		rs.first();
		while (!rs.isAfterLast()) {
			String type = typesDescription.get(rs.getInt("DATA_TYPE"));
			if (type == null) {
				rs.next();
				continue;
			}
			if (type.equals(driverType)) {
				String ret = rs.getString("CREATE_PARAMS");
				if (ret == null) {
					return new String[0];
				} else {
					return ret.split("\\Q,\\E");
				}
			}
			rs.next();
		}

		throw new RuntimeException("Where does this type come from? "
				+ driverType);
	}


	public static StringBuilder getTypeInAddColumnStatement(final Type fieldType)
			throws DriverException {
		final Constraint[] constraints = fieldType.getConstraints();
		final StringBuilder tmp1 = new StringBuilder();
		final String[] tmp2 = new String[2];
		final StringBuilder result = new StringBuilder(typesDescription
				.get(fieldType.getTypeCode()));

		for (Constraint c : constraints) {
			if (c instanceof NotNullConstraint) {
				tmp1.append(" NOT NULL");
			} else if (c instanceof UniqueConstraint) {
				tmp1.append(" UNIQUE");
			} else if (c instanceof AutoIncrementConstraint) {
				// tmp.append(" AUTO_INCREMENT"); // MySQL
				// result.append("SERIAL"); // PostgreSQL
			} else if (c instanceof LengthConstraint) {
				result.append('(').append(c.getConstraintValue()).append(')');
			} else if (c instanceof PrecisionConstraint) {
				tmp2[0] = c.getConstraintValue();
			} else if (c instanceof ScaleConstraint) {
				tmp2[1] = c.getConstraintValue();
			}
		}
		if (null != tmp2[0]) {
			result.append('(').append(tmp2[0]);
			if (null != tmp2[1]) {
				result.append(',').append(tmp2[1]);
			}
			result.append(')');
		}
		return result.append(tmp1);
	}

	public static void createSource(Connection c, String tableName,
			Metadata driverMetadata) throws DriverException {
		final StringBuilder sql = new StringBuilder("CREATE TABLE " + tableName
				+ " (");
		final int fc = driverMetadata.getFieldCount();
		String separator = "";

		for (int i = 0; i < fc; i++) {
			sql.append(separator).append(driverMetadata.getFieldName(i))
					.append(' ').append(
							getTypeInAddColumnStatement(driverMetadata
									.getFieldType(i)));
			separator = ", ";
		}

		final String[] pks = MetadataUtilities.getPKNames(driverMetadata);
		if (pks.length == 0) {
			throw new DriverException("No primary key specified");
		} else {
			sql.append(", PRIMARY KEY(").append(pks[0]);
			for (int i = 1; i < pks.length; i++) {
				sql.append(", ").append(pks[i]);
			}
			sql.append(')');
		}
		sql.append(')');

		try {
			Statement st = c.createStatement();
			st.execute(sql.toString());
			st.close();
		} catch (SQLException e) {
			throw new DriverException(e.getMessage() + ":" + sql.toString(), e);
		}
	}



	public TypeDefinition[] getTypesDefinitions() throws DriverException {
		final Set<ConstraintNames> sc = new HashSet<ConstraintNames>();
		sc.add(ConstraintNames.NOT_NULL);
		sc.add(ConstraintNames.READONLY);
		final ConstraintNames[] c1 = (ConstraintNames[]) sc
				.toArray(new ConstraintNames[sc.size()]);
		sc.add(ConstraintNames.PK);
		sc.add(ConstraintNames.UNIQUE);
		final ConstraintNames[] c2 = (ConstraintNames[]) sc
				.toArray(new ConstraintNames[sc.size()]);
		sc.add(ConstraintNames.MIN);
		sc.add(ConstraintNames.MAX);
		sc.add(ConstraintNames.RANGE);
		final ConstraintNames[] c3 = (ConstraintNames[]) sc
				.toArray(new ConstraintNames[sc.size()]);
		sc.add(ConstraintNames.PRECISION);
		sc.add(ConstraintNames.SCALE);
		final ConstraintNames[] c4 = (ConstraintNames[]) sc
				.toArray(new ConstraintNames[sc.size()]);

		try {
			return new TypeDefinition[] {
					new DefaultTypeDefinition(BINARY, Type.BINARY, c1),
					new DefaultTypeDefinition(BIT, Type.BOOLEAN, c2),
					new DefaultTypeDefinition(BOOLEAN, Type.BOOLEAN, c2),
					new DefaultTypeDefinition(DATE, Type.DATE, c3),
					new DefaultTypeDefinition(DOUBLE, Type.DOUBLE, c4),
					new DefaultTypeDefinition(FLOAT, Type.FLOAT, c4),
					new DefaultTypeDefinition(INTEGER, Type.INT, c3),
					new DefaultTypeDefinition(BIGINT, Type.LONG, c3),
					new DefaultTypeDefinition(INTEGER, Type.SHORT, c3),
					new DefaultTypeDefinition(VARCHAR, Type.STRING,
							new ConstraintNames[] { ConstraintNames.NOT_NULL,
									ConstraintNames.READONLY,
									ConstraintNames.PK, ConstraintNames.UNIQUE,
									ConstraintNames.LENGTH }),
					new DefaultTypeDefinition(CHAR, Type.STRING,
							new ConstraintNames[] { ConstraintNames.NOT_NULL,
									ConstraintNames.READONLY,
									ConstraintNames.PK, ConstraintNames.UNIQUE,
									ConstraintNames.LENGTH }),
					new DefaultTypeDefinition(TIME, Type.TIME, c3),
					new DefaultTypeDefinition(TIMESTAMP, Type.TIMESTAMP, c3) };
		} catch (InvalidTypeException e) {
			throw new DriverException("Invalid type");
		}
	}
}