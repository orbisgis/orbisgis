package org.gdms.driver;

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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.WarningListener;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
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
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;

public abstract class DefaultDBDriver extends DefaultSQL implements DBDriver {

	private ResultSet resultSet;
	private long rowCount = -1;
	private Metadata metadata = null;
	private Connection conn;
	private String tableName;
	private ResultSetMetaData resultsetMetadata;
	private DataSourceFactory dsf;
	private Statement statement;
	private String orderFieldName;

	public Metadata getMetadata() throws DriverException {
		if (metadata == null) {
			try {
				final int fc = resultsetMetadata.getColumnCount();
				final Type[] fieldsTypes = new Type[fc];
				final String[] fieldsNames = new String[fc];

				final DatabaseMetaData dbmd = conn.getMetaData();

				final ResultSet pKSet = dbmd.getPrimaryKeys(null, null,
						tableName);
				final List<String> pKFieldsList = new LinkedList<String>();
				while (pKSet.next()) {
					pKFieldsList.add(pKSet.getString("COLUMN_NAME"));
				}

				for (int i = 0; i < fc; i++) {
					try {
						fieldsNames[i] = resultsetMetadata.getColumnName(i + 1);

						final int sqlFieldType = resultsetMetadata
								.getColumnType(i + 1);
						final int gdmsType = getGDMSType(sqlFieldType, i + 1,
								fieldsNames[i]);
						final String driverType = getTypesDescription().get(
								gdmsType);
						final Map<ConstraintNames, Constraint> lc = getConstraints(
								fieldsNames, pKFieldsList, i);

						final ConstraintNames[] constraintNames = new ConstraintNames[lc
								.size()];
						lc.keySet().toArray(constraintNames);

						final Constraint[] constraints = new Constraint[lc
								.size()];
						lc.values().toArray(constraints);

						final TypeDefinition typeDefinition = new DefaultTypeDefinition(
								driverType, gdmsType, constraintNames);

						fieldsTypes[i] = typeDefinition.createType(constraints);
					} catch (SQLException e) {
						getWL().throwWarning(
								"Cannot read type in field: " + i
										+ ". Using binary instead");
						fieldsTypes[i] = TypeFactory.createType(Type.BINARY,
								"Unknown_field_" + i);
					}
				}

				metadata = new DefaultMetadata(fieldsTypes, fieldsNames);
			} catch (InvalidTypeException e) {
				throw new DriverException(e);
			} catch (SQLException e) {
				throw new DriverException(e);
			}
		}

		return metadata;
	}

	protected Map<ConstraintNames, Constraint> getConstraints(
			final String[] fieldsNames, final List<String> pKFieldsList,
			int fieldIndex) throws SQLException {
		final Map<ConstraintNames, Constraint> lc = new HashMap<ConstraintNames, Constraint>();

		if (pKFieldsList.contains(fieldsNames[fieldIndex])) {
			lc.put(ConstraintNames.PK, new PrimaryKeyConstraint());
		}
		if (ResultSetMetaData.columnNoNulls == resultsetMetadata
				.isNullable(fieldIndex + 1)) {
			lc.put(ConstraintNames.NOT_NULL, new NotNullConstraint());
		}
		if (resultsetMetadata.isReadOnly(fieldIndex + 1)) {
			lc.put(ConstraintNames.READONLY, new ReadOnlyConstraint());
		}
		if (resultsetMetadata.isAutoIncrement(fieldIndex + 1)) {
			lc.put(ConstraintNames.AUTO_INCREMENT,
					new AutoIncrementConstraint());
			lc.put(ConstraintNames.READONLY, new ReadOnlyConstraint());
		}
		if (0 < resultsetMetadata.getPrecision(fieldIndex + 1)) {
			lc.put(ConstraintNames.PRECISION, new PrecisionConstraint(
					resultsetMetadata.getPrecision(fieldIndex + 1)));
		}
		if (0 < resultsetMetadata.getScale(fieldIndex + 1)) {
			lc.put(ConstraintNames.SCALE, new ScaleConstraint(resultsetMetadata
					.getScale(fieldIndex + 1)));
		}
		if (0 < resultsetMetadata.getColumnDisplaySize(fieldIndex + 1)) {
			lc.put(ConstraintNames.LENGTH, new LengthConstraint(
					resultsetMetadata.getColumnDisplaySize(fieldIndex + 1)));
		}
		return lc;
	}

	protected int getGDMSType(int jdbcType, int jdbcFieldNumber,
			String fieldName) throws DriverException {
		switch (jdbcType) {
		case Types.CHAR:
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
		case Types.CLOB:
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
		case Types.BLOB:
			return Type.BINARY;
		case Types.TIMESTAMP:
			return Type.TIMESTAMP;
		case Types.TIME:
			return Type.TIME;
		case Types.OTHER:
			return Type.BINARY;
		}

		getWL().throwWarning("Wrong type " + jdbcType + ". byte[] is used.");
		return Type.BINARY;
	}

	protected static String getOrderFields(Connection c, String tableName)
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

	public void open(Connection con, String tableName) throws DriverException {
		try {
			orderFieldName = getOrderFields(con, tableName);
			if (orderFieldName == null) {
				throw new DriverException("The table has no primary key");
			}

			statement = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY,
					ResultSet.CLOSE_CURSORS_AT_COMMIT);
			conn = con;
			this.tableName = tableName;
			getData();
		} catch (SQLException e) {
			throw new DriverException(e.getMessage(), e);
		}
	}

	protected void getData() throws DriverException {
		String sql = getSelectSQL(tableName, orderFieldName);
		try {
			resultSet = statement.executeQuery(sql);
			resultsetMetadata = resultSet.getMetaData();
		} catch (SQLException e) {
			throw new DriverException(sql, e);
		}
	}

	protected String getSelectSQL(String tableName, String orderFieldName)
			throws DriverException {
		String sql = "SELECT * FROM \"" + tableName + "\"" + " ORDER BY "
				+ orderFieldName;
		return sql;
	}

	public Value getFieldValue(long rowIndex, int fieldId)
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

	public void close(Connection conn) throws DriverException {
		try {
			resultSet.close();
			resultSet = null;
			resultsetMetadata = null;
			statement.close();
			statement = null;
			conn.close();
			conn = null;
			metadata = null;
			tableName = null;
			rowCount = -1;
		} catch (SQLException e) {
			throw new DriverException(e);
		}
	}

	public boolean isCommitable() {
		return true;
	}

	protected ResultSet getResultSet() {
		return resultSet;
	}

	protected void setResultSet(ResultSet resultSet) {
		this.resultSet = resultSet;
	}

	protected void setResultsetMetadata(ResultSetMetaData resultsetMetadata) {
		this.resultsetMetadata = resultsetMetadata;
	}

	protected ResultSetMetaData getResultsetMetadata() {
		return resultsetMetadata;
	}

	protected WarningListener getWL() {
		return dsf.getWarningListener();
	}

	/**
	 * @see org.gdms.data.driver.DriverCommons#setDataSourceFactory(org.gdms.data.DataSourceFactory)
	 */
	public void setDataSourceFactory(DataSourceFactory dsf) {
		this.dsf = dsf;
	}

	protected void setTableName(String tableName) {
		this.tableName = tableName;
	}

	protected String getTableName() {
		return tableName;
	}

}
