package org.gdms.data.db;

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

/**
 * DBDrivers helper class
 */
public class JDBCSupport {
	public static final String CHAR = "char";

	public static final String VARCHAR = "varchar";

	public static final String LONGVARCHAR = "longvarchar";

	public static final String BIGINT = "bigint";

	public static final String BOOLEAN = "boolean";

	public static final String DATE = "date";

	public static final String DECIMAL = "decimal";

	public static final String NUMERIC = "numeric";

	public static final String FLOAT = "float";

	public static final String DOUBLE = "double";

	public static final String INTEGER = "integer";

	public static final String REAL = "real";

	public static final String SMALLINT = "smallint";

	public static final String TINYINT = "tinyint";

	public static final String BINARY = "binary";

	public static final String VARBINARY = "varbinary";

	public static final String LONGVARBINARY = "longvarbinary";

	public static final String TIMESTAMP = "timestamp";

	public static final String TIME = "time";

	public static final String BIT = "bit";

	private static Map<Integer, String> typesDescription = new HashMap<Integer, String>();

	private ResultSet resultSet;

	private int rowCount = -1;

	static {
		typesDescription.put(Type.BINARY, BINARY);
		typesDescription.put(Type.BOOLEAN, BOOLEAN);
		typesDescription.put(Type.BYTE, TINYINT);
		typesDescription.put(Type.DATE, DATE);
		typesDescription.put(Type.DOUBLE, DOUBLE);
		typesDescription.put(Type.FLOAT, REAL);
		typesDescription.put(Type.INT, INTEGER);
		typesDescription.put(Type.LONG, BINARY);
		typesDescription.put(Type.SHORT, INTEGER);
		typesDescription.put(Type.STRING, VARCHAR);
		typesDescription.put(Type.TIME, TIME);
		typesDescription.put(Type.TIMESTAMP, TIMESTAMP);
	}

	/**
	 * Creates a new JDBCSupport object.
	 *
	 * @param r
	 *            ResultSet that will be used to return the methods values
	 * @param data
	 */
	private JDBCSupport(ResultSet r, DatabaseMetaData dbmd) {
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

	private static String getOrderFields(Connection c, String tableName)
			throws SQLException {
		DatabaseMetaData metadata = c.getMetaData();
		ResultSet res = metadata.getPrimaryKeys(null, null, tableName);

		String order = null;
		if (res.next()) {
			order = "\"" + res.getString("COLUMN_NAME") + "\"";
		}
		while (res.next()) {
			order = order + ", \"" + res.getString("COLUMN_NAME") + "\"";
		}

		return order;
	}

	/**
	 * Creates a new JDBCSuuport object with the data retrieved from the
	 * connection with the given sql
	 *
	 * @param con
	 *            Connection to the database
	 * @param tableName
	 * @param sql
	 *            SQL defining the data to use
	 *
	 * @return JDBCSupport
	 *
	 * @throws SQLException
	 *             If the data cannot be retrieved
	 * @throws DriverException
	 */
	public static JDBCSupport newJDBCSupport(Connection con,
			String tableNameInSQL, String tableName) throws SQLException,
			DriverException {

		String orderFieldName = getOrderFields(con, tableName);

		String sql = "SELECT * FROM " + tableNameInSQL;
		if (orderFieldName != null) {
			sql += " ORDER BY " + orderFieldName;
		} else {
			throw new DriverException("The database has no primary key");
		}

		Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		ResultSet res = st.executeQuery(sql);

		return new JDBCSupport(res, con.getMetaData());
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
			final ResultSetMetaData rsmd = resultSet.getMetaData();

			final ResultSet pKSet = dbmd.getPrimaryKeys(null, null, tableName);
			final List<String> pKFieldsList = new LinkedList<String>();
			while (pKSet.next()) {
				pKFieldsList.add(pKSet.getString("COLUMN_NAME"));
			}

			for (int i = 0; i < fc; i++) {
				fieldsNames[i] = getFieldName(i);

				final int sqlFieldType = getSqlFieldType(i);
				final int type = getType(sqlFieldType);
				final String driverType = typesDescription.get(type);
				final Map<ConstraintNames, Constraint> lc = new HashMap<ConstraintNames, Constraint>();

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
		case Types.OTHER:
			return Type.BINARY;
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

	// public boolean isReadOnly(int fieldId) throws DriverException {
	// try {
	// return resultSet.getMetaData().isReadOnly(fieldId + 1);
	// } catch (SQLException e) {
	// throw new DriverException(e);
	// }
	// }
	//
	// public static boolean isValidParameter(String driverType, String
	// paramName,
	// String paramValue) {
	// if (paramName.equals(LENGTH) || paramName.equals(PRECISION)
	// || paramName.equals(SCALE)) {
	// if (paramValue == null) {
	// return true;
	// } else {
	// try {
	// Integer.parseInt(paramValue);
	// } catch (NumberFormatException e) {
	// return false;
	// }
	// }
	// }
	// return true;
	// }

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