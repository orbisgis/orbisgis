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
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.gdms.data.db.DBSource;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.metadata.MetadataUtilities;
import org.gdms.data.types.AutoIncrementConstraint;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueWriter;
import org.gdms.driver.DBReadWriteDriver;
import org.gdms.driver.DriverException;
import org.gdms.driver.TableDescription;

import com.vividsolutions.jts.geom.Geometry;

/**
 * class that implements the methods of the database drivers related to SQL
 *
 * @author Fernando Gonzalez Cortes
 *
 */
public abstract class DefaultSQL implements DBReadWriteDriver, ValueWriter {

	private ValueWriter valueWriter = ValueWriter.internalValueWriter;

	/**
	 * @see org.gdms.driver.DBReadWriteDriver#getInsertSQL(java.lang.String,
	 *      java.lang.String[], org.gdms.data.types.Type[],
	 *      org.gdms.data.values.Value[])
	 */
	public String getInsertSQL(String tableName, String[] fieldNames,
			Type[] fieldTypes, Value[] row) throws DriverException {
		StringBuffer sql = new StringBuffer();
		sql.append("INSERT INTO \"").append(tableName).append("\" (\"").append(
				fieldNames[0]);

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

	/**
	 * @see org.gdms.driver.DBReadWriteDriver#getUpdateSQL(java.lang.String,
	 *      java.lang.String[], org.gdms.data.values.Value[],
	 *      java.lang.String[], org.gdms.data.types.Type[],
	 *      org.gdms.data.values.Value[])
	 */
	public String getUpdateSQL(String tableName, String[] pkNames,
			Value[] pkValues, String[] fieldNames, Type[] fieldTypes,
			Value[] row) throws DriverException {
		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE \"").append(tableName).append("\" SET ");
		String separator = "";
		for (int i = 0; i < fieldNames.length; i++) {
			if (isAutoNumerical(fieldTypes[i])) {
				continue;
			} else {
				String fieldValue = row[i].getStringValue(this);
				sql.append(separator).append("\"").append(fieldNames[i])
						.append("\" = ").append(fieldValue);
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

	public void rollBackTrans(Connection con) throws SQLException {
		execute(con, "ROLLBACK;");
	}

	public TableDescription[] getTables(Connection c) throws DriverException {
		DatabaseMetaData md = null;
		ResultSet rs = null;
		ArrayList<TableDescription> tables = new ArrayList<TableDescription>();

		try {
			String[] types = { "TABLE", "VIEW" };
			md = c.getMetaData();
			rs = md.getTables(null, null, null, types);
			int i = 0;
			while (rs.next()) {
				tables.add(new TableDescription(rs.getString("TABLE_NAME"), rs
						.getString("TABLE_TYPE")));
				i++;
			}
		} catch (SQLException e) {
			throw new DriverException(e);
		}

		return tables.toArray(new TableDescription[0]);
	}

	/**
	 * @see org.gdms.driver.ReadOnlyDriver#getTypesDefinitions()
	 */
	public TypeDefinition[] getTypesDefinitions() {
		return getConversionRules();
	}

	/**
	 * @see org.gdms.data.values.ValueWriter#getNullStatementString()
	 */
	public String getNullStatementString() {
		return valueWriter.getNullStatementString();
	}

	/**
	 * @see org.gdms.data.values.ValueWriter#getStatementString(boolean)
	 */
	public String getStatementString(boolean b) {
		return valueWriter.getStatementString(b);
	}

	/**
	 * @see org.gdms.data.values.ValueWriter#getStatementString(byte[])
	 */
	public String getStatementString(byte[] binary) {
		return valueWriter.getStatementString(binary);
	}

	/**
	 * @see org.gdms.data.values.ValueWriter#getStatementString(java.sql.Date)
	 */
	public String getStatementString(Date d) {
		return valueWriter.getStatementString(d);
	}

	/**
	 * @see org.gdms.data.values.ValueWriter#getStatementString(double, int)
	 */
	public String getStatementString(double d, int sqlType) {
		return valueWriter.getStatementString(d, sqlType);
	}

	/**
	 * @see org.gdms.data.values.ValueWriter#getStatementString(com.vividsolutions.jts.geom.Geometry)
	 */
	public String getStatementString(Geometry g) {
		return valueWriter.getStatementString(g);
	}

	/**
	 * @see org.gdms.data.values.ValueWriter#getStatementString(int, int)
	 */
	public String getStatementString(int i, int sqlType) {
		return valueWriter.getStatementString(i, sqlType);
	}

	/**
	 * @see org.gdms.data.values.ValueWriter#getStatementString(long)
	 */
	public String getStatementString(long i) {
		return valueWriter.getStatementString(i);
	}

	/**
	 * @see org.gdms.data.values.ValueWriter#getStatementString(java.lang.String,
	 *      int)
	 */
	public String getStatementString(String str, int sqlType) {
		return valueWriter.getStatementString(str, sqlType);
	}

	/**
	 * @see org.gdms.data.values.ValueWriter#getStatementString(java.sql.Time)
	 */
	public String getStatementString(Time t) {
		return valueWriter.getStatementString(t);
	}

	/**
	 * @see org.gdms.data.values.ValueWriter#getStatementString(java.sql.Timestamp)
	 */
	public String getStatementString(Timestamp ts) {
		return valueWriter.getStatementString(ts);
	}

	/**
	 * @see org.gdms.driver.DBReadWriteDriver#beginTrans(java.sql.Connection)
	 */
	public void beginTrans(Connection con) throws SQLException {
		execute(con, "BEGIN;");
	}

	/**
	 * @see org.gdms.driver.DBReadWriteDriver#commitTrans(java.sql.Connection)
	 */
	public void commitTrans(Connection con) throws SQLException {
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
	 * Gets the rules used to
	 *
	 * @return
	 * @throws DriverException
	 */
	protected abstract ConversionRule[] getConversionRules();

	/**
	 * @see org.gdms.driver.DBReadWriteDriver#createSource(org.gdms.data.db.DBSource,
	 *      org.gdms.data.metadata.Metadata)
	 */
	public void createSource(DBSource source, Metadata metadata)
			throws DriverException {
		StringBuilder sql = null;
		Connection c = null;
		try {
			c = getConnection(source.getHost(), source.getPort(), source
					.getDbName(), source.getUser(), source.getPassword());
			beginTrans(c);
			sql = new StringBuilder(getCreateTableKeyWord() + " \""
					+ source.getTableName() + "\" (");
			final int fc = metadata.getFieldCount();
			String separator = "";

			for (int i = 0; i < fc; i++) {
				String fieldName = metadata.getFieldName(i);
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

			final String[] pks = MetadataUtilities.getPKNames(metadata);
			if (pks.length == 0) {
				throw new DriverException("No primary key specified");
			} else {
				sql.append(", PRIMARY KEY(").append("\"").append(pks[0])
						.append("\"");
				for (int i = 1; i < pks.length; i++) {
					sql.append(", ").append("\"").append(pks[i]).append("\"");
				}
				sql.append(')');
			}
			sql.append(");");

			sql.append(getPostCreateTableSQL(source, metadata));
			commitTrans(c);

			Statement st = c.createStatement();
			st.execute(sql.toString());
			st.close();
			c.close();
		} catch (SQLException e1) {
			if (c != null) {
				try {
					rollBackTrans(c);
				} catch (SQLException e) {
					throw new DriverException(sql.toString(), e1);
				}
			}
			throw new DriverException(sql.toString() + ": " + e1.getMessage(),
					e1);
		}
	}

	protected String getCreateTableKeyWord() {
		return "CREATE TABLE";
	}

	/**
	 * Gets the instructions to execute after a table creation
	 *
	 * @param source
	 * @param metadata
	 * @return
	 * @throws DriverException
	 */
	protected String getPostCreateTableSQL(DBSource source, Metadata metadata)
			throws DriverException {
		// Nothing by default
		return "";
	}

	/**
	 * @see org.gdms.driver.DBReadWriteDriver#execute(java.sql.Connection,
	 *      java.lang.String)
	 */
	public void execute(Connection con, String sql) throws SQLException {
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
		AutoIncrementConstraint c = (AutoIncrementConstraint) type
				.getConstraint(Constraint.AUTO_INCREMENT);
		if (c != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Gets the value to show in insert statements for autoincrement fields
	 *
	 * @return
	 */
	protected String getAutoIncrementDefaultValue() {
		return "DEFAULT";
	}

	/**
	 * @see org.gdms.driver.DBReadWriteDriver#getAddFieldSQL(java.lang.String,
	 *      java.lang.String, org.gdms.data.types.Type)
	 */
	public String getAddFieldSQL(String tableName, String fieldName,
			Type fieldType) throws DriverException {
		ConversionRule rule = getSuitableRule(fieldType);
		return "ALTER TABLE \"" + tableName + "\" ADD "
				+ rule.getSQL(fieldName, fieldType);
	}

	/**
	 * @see org.gdms.driver.DBReadWriteDriver#getChangeFieldNameSQL(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public String getChangeFieldNameSQL(String tableName, String oldName,
			String newName) throws DriverException {
		return "ALTER TABLE \"" + tableName + "\" RENAME COLUMN \"" + oldName
				+ "\" TO \"" + newName + "\"";
	}

	/**
	 * @see org.gdms.driver.DBReadWriteDriver#getDeleteFieldSQL(java.lang.String,
	 *      java.lang.String)
	 */
	public String getDeleteFieldSQL(String tableName, String fieldName)
			throws DriverException {
		return "ALTER TABLE \"" + tableName + "\" DROP COLUMN \"" + fieldName
				+ "\"";
	}

	/**
	 * @see org.gdms.driver.DBReadWriteDriver#getDeleteRecordSQL(java.lang.String,
	 *      java.lang.String[], org.gdms.data.values.Value[])
	 */
	public String getDeleteRecordSQL(String tableName, String[] names,
			Value[] pks) throws DriverException {
		// Delete sql statement
		StringBuffer sql = new StringBuffer("DELETE FROM \"").append(tableName)
				.append("\" WHERE \"").append(names[0]).append("\"=").append(
						pks[0].getStringValue(this));

		for (int i = 1; i < pks.length; i++) {
			sql.append(" AND \"").append(names[i]).append("\"=").append(
					pks[i].getStringValue(this));
		}

		return sql.toString();
	}
}
