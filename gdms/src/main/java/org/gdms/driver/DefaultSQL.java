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
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.gdms.data.db.DBSource;
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
import org.gdms.data.types.ScaleConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.types.UniqueConstraint;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueWriter;

import com.vividsolutions.jts.geom.Geometry;

public abstract class DefaultSQL implements DBReadWriteDriver, ValueWriter {

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

	static {
		typesDescription.put(Type.BINARY, BINARY);
		typesDescription.put(Type.BOOLEAN, BOOLEAN);
		typesDescription.put(Type.BYTE, TINYINT);
		typesDescription.put(Type.DATE, DATE);
		typesDescription.put(Type.DOUBLE, DOUBLE);
		typesDescription.put(Type.FLOAT, REAL);
		typesDescription.put(Type.INT, INTEGER);
		typesDescription.put(Type.LONG, BIGINT);
		typesDescription.put(Type.SHORT, SMALLINT);
		typesDescription.put(Type.STRING, VARCHAR);
		typesDescription.put(Type.TIME, TIME);
		typesDescription.put(Type.TIMESTAMP, TIMESTAMP);
	}

	private ValueWriter valueWriter = ValueWriter.internalValueWriter;

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
				sql.append(separator).append(getAutoIncrementDefault());
			} else {
				sql.append(separator).append(row[i].getStringValue(this));
			}
			separator = ", ";
		}

		return sql.append(")").toString();
	}

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

	public String getNullStatementString() {
		return valueWriter.getNullStatementString();
	}

	public String getStatementString(boolean b) {
		return valueWriter.getStatementString(b);
	}

	public String getStatementString(byte[] binary) {
		return valueWriter.getStatementString(binary);
	}

	public String getStatementString(Date d) {
		return valueWriter.getStatementString(d);
	}

	public String getStatementString(double d, int sqlType) {
		return valueWriter.getStatementString(d, sqlType);
	}

	public String getStatementString(Geometry g) {
		return valueWriter.getStatementString(g);
	}

	public String getStatementString(int i, int sqlType) {
		return valueWriter.getStatementString(i, sqlType);
	}

	public String getStatementString(long i) {
		return valueWriter.getStatementString(i);
	}

	public String getStatementString(String str, int sqlType) {
		return valueWriter.getStatementString(str, sqlType);
	}

	public String getStatementString(Time t) {
		return valueWriter.getStatementString(t);
	}

	public String getStatementString(Timestamp ts) {
		return valueWriter.getStatementString(ts);
	}

	public void beginTrans(Connection con) throws SQLException {
		execute(con, "BEGIN;");
	}

	public void commitTrans(Connection con) throws SQLException {
		execute(con, "COMMIT;");
	}

	protected String getSQLFieldDefinition(String fieldName, Type fieldType)
			throws DriverException {
		return "\"" + fieldName + "\"" + " "
				+ getTypeInAddColumnStatement(fieldType);
	}

	protected String getSequenceKeyword() {
		return "SERIAL";
	}

	protected String getTypeInAddColumnStatement(Type fieldType)
			throws DriverException {
		final Constraint[] constraints = fieldType.getConstraints();
		final StringBuilder tmp1 = new StringBuilder();
		final String[] tmp2 = new String[2];
		StringBuilder result = new StringBuilder(typesDescription.get(fieldType
				.getTypeCode()));

		for (Constraint c : constraints) {
			if (c instanceof NotNullConstraint) {
				tmp1.append(" NOT NULL");
			} else if (c instanceof UniqueConstraint) {
				tmp1.append(" UNIQUE");
			} else if (c instanceof AutoIncrementConstraint) {
				result = new StringBuilder(getSequenceKeyword());
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
		return result.append(tmp1).toString();
	}

	public void createSource(DBSource source, Metadata metadata)
			throws DriverException {
		StringBuilder sql = null;
		Connection c = null;
		try {
			c = getConnection(source.getHost(), source.getPort(), source
					.getDbName(), source.getUser(), source.getPassword());
			beginTrans(c);
			sql = new StringBuilder("CREATE TABLE \"" + source.getTableName()
					+ "\" (");
			final int fc = metadata.getFieldCount();
			String separator = "";

			for (int i = 0; i < fc; i++) {
				String fieldDefinition = getSQLFieldDefinition(metadata
						.getFieldName(i), metadata.getFieldType(i));
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
			throw new DriverException(sql.toString(), e1);
		}
	}

	protected String getPostCreateTableSQL(DBSource source, Metadata metadata)
			throws DriverException {
		// Nothing by default
		return "";
	}

	public void execute(Connection con, String sql) throws SQLException {
		Statement st = con.createStatement();
		st.execute(sql);
		st.close();
	}

	protected boolean isAutoNumerical(Type type) throws DriverException {
		AutoIncrementConstraint c = (AutoIncrementConstraint) type
				.getConstraint(ConstraintNames.AUTO_INCREMENT);
		if (c != null) {
			return true;
		} else {
			return false;
		}
	}

	protected String getAutoIncrementDefault() {
		return "DEFAULT";
	}

	public String getAddFieldSQL(String tableName, String fieldName,
			Type fieldType) throws DriverException {
		return "ALTER TABLE \"" + tableName + "\" ADD \"" + fieldName + "\" "
				+ getTypeInAddColumnStatement(fieldType);
	}

	public String getChangeFieldNameSQL(String tableName, String oldName,
			String newName) throws DriverException {
		return "ALTER TABLE \"" + tableName + "\" RENAME COLUMN \"" + oldName
				+ "\" TO \"" + newName + "\"";
	}

	public String getDeleteFieldSQL(String tableName, String fieldName)
			throws DriverException {
		return "ALTER TABLE \"" + tableName + "\" DROP COLUMN \"" + fieldName
				+ "\"";
	}

	public String getDeleteRecordSQL(String tableName, String[] names,
			Value[] pks) throws DriverException {
		// Delete sql statement
		StringBuffer sql = new StringBuffer("DELETE FROM \"").append(tableName)
				.append("\" WHERE \"").append(names[0]).append("\"=").append(
						pks[0].getStringValue(this));

		for (int i = 1; i < pks.length; i++) {
			sql.append(" AND ").append(names[i]).append("=").append(
					pks[i].getStringValue(this));
		}

		return sql.toString();
	}

	protected static Map<Integer, String> getTypesDescription() {
		return typesDescription;
	}

}
