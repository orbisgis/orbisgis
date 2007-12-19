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
 *    Fernando GONZALES CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALES CORTES, Thomas LEDUC
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

/**
 * Class with the implementation of the methods in database driver interfaces
 * that are related to JDBC
 *
 * @author Fernando Gonzalez Cortes
 */
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

	/**
	 * @see org.gdms.driver.ReadOnlyDriver#getMetadata()
	 */
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

	/**
	 * Gets the constraints for the field at 'fieldIndex' index
	 *
	 * @param fieldsNames
	 * @param pKFieldsList
	 * @param fieldIndex
	 * @return
	 * @throws SQLException
	 */
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

	/**
	 * Gets the code of the type in gdms {@link Type} for the specified jdbc
	 * type
	 *
	 * @param jdbcType
	 * @param jdbcFieldNumber
	 * @param fieldName
	 * @return
	 * @throws DriverException
	 */
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

	/**
	 * Gets the order by clause of an instruction that orders by the primary key
	 * fields
	 *
	 * @param c
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
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

	/**
	 * @see org.gdms.driver.DBDriver#open(java.sql.Connection, java.lang.String)
	 */
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

	/**
	 * catches the {@link ResultSet} and {@link ResultSetMetaData}
	 *
	 * @throws DriverException
	 */
	protected void getData() throws DriverException {
		String sql = getSelectSQL(tableName, orderFieldName);
		try {
			resultSet = statement.executeQuery(sql);
			resultsetMetadata = resultSet.getMetaData();
		} catch (SQLException e) {
			throw new DriverException(sql, e);
		}
	}

	/**
	 * Gets the Select statement that will be accessed by the driver
	 *
	 * @param tableName
	 * @param orderFieldName
	 * @return
	 * @throws DriverException
	 */
	protected String getSelectSQL(String tableName, String orderFieldName)
			throws DriverException {
		String sql = "SELECT * FROM \"" + tableName + "\"" + " ORDER BY "
				+ orderFieldName;
		return sql;
	}

	/**
	 * @see org.gdms.driver.ReadAccess#getFieldValue(long, int)
	 */
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
			throw new DriverException(e);
		}
	}

	/**
	 * @see org.gdms.driver.DBDriver#close(java.sql.Connection)
	 */
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

	/**
	 * @see org.gdms.driver.ReadWriteDriver#isCommitable()
	 */
	public boolean isCommitable() {
		return true;
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
	 * setter for the {@link ResultSet}
	 *
	 * @param resultSet
	 */
	protected void setResultSet(ResultSet resultSet) {
		this.resultSet = resultSet;
	}

	/**
	 * setter for the {@link ResultSetMetaData}
	 *
	 * @param resultsetMetadata
	 */
	protected void setResultsetMetadata(ResultSetMetaData resultsetMetadata) {
		this.resultsetMetadata = resultsetMetadata;
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
	public void setDataSourceFactory(DataSourceFactory dsf) {
		this.dsf = dsf;
	}

	/**
	 * setter for the table name
	 *
	 * @param tableName
	 */
	protected void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * getter for the table name
	 *
	 * @return
	 */
	protected String getTableName() {
		return tableName;
	}

}
