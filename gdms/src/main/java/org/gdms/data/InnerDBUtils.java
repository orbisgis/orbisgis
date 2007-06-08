package org.gdms.data;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueWriter;

/**
 * Utility class to generate SQL statements
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public class InnerDBUtils {
	/**
	 * Builds a WHERE clause with the and operation of the equality between each
	 * field name and field value
	 * 
	 * @param pks
	 *            String array with the field values
	 * @param fieldNames
	 *            String array with the field names
	 * 
	 * @return String
	 */
	public static String buildSQLWhere(String[] pks, String[] fieldNames) {
		String ret = fieldNames[0] + " = " + pks[0];

		for (int i = 1; i < pks.length; i++) {
			ret += (" AND " + fieldNames[0] + " = " + pks[0]);
		}

		return ret;
	}

	/**
	 * Creates a delete statement in the specified table in the row specified by
	 * the field names and field values
	 * 
	 * @param pks
	 *            values to specify the row to delete
	 * @param names
	 *            names of the fields
	 * @param tableName
	 *            name of the table
	 * @param vWriter
	 *            DOCUMENT ME!
	 * 
	 * @return String
	 */
	public static String createDeleteStatement(Value[] pks, String[] names,
			String tableName, ValueWriter vWriter) {
		// Delete sql statement
		StringBuffer sql = new StringBuffer("DELETE FROM ").append(tableName)
				.append(" WHERE ").append(names[0]).append("=").append(
						pks[0].getStringValue(vWriter));

		for (int i = 1; i < pks.length; i++) {
			sql.append(" AND ").append(names[i]).append("=").append(
					pks[i].getStringValue(vWriter));
		}

		return sql.toString();
	}

	/**
	 * Creates a SQL statement to create a table. Creates an aditional field
	 * called GDBMSINDEX with the autonumeric primary key constraint
	 * 
	 * @param tableName
	 *            Name of the table to be created
	 * @param names
	 *            names of the fields
	 * @param types
	 *            types of the fields. Must have the same length than names
	 * 
	 * @return SQL statement
	 */
	public static String getCreateStatementWithAutonumeric(String tableName,
			String[] names, int[] types) {
		StringBuffer sql = new StringBuffer("CREATE CACHED TABLE ");
		sql.append(tableName).append(" (");

		for (int i = 0; i < types.length; i++) {
			sql.append(names[i]).append(" ").append(getTypeString(types[i]))
					.append(", ");
		}

		sql.append("GDBMSINDEX INTEGER IDENTITY");

		sql.append(")");

		return sql.toString();
	}

	/**
	 * Creates a SQL statement to create a table.
	 * 
	 * @param tableName
	 *            Name of the table to be created
	 * @param pkNames
	 *            DOCUMENT ME!
	 * @param names
	 *            names of the fields
	 * @param types
	 *            types of the fields. Must have the same length than names
	 * 
	 * @return SQL statement
	 */
	public static String getCreateStatementWithPK(String tableName,
			String[] pkNames, String[] names, int[] types) {
		StringBuffer sql = new StringBuffer("CREATE CACHED TABLE ");
		sql.append(tableName).append(" (");

		for (int i = 0; i < types.length; i++) {
			sql.append(names[i]).append(" ").append(getTypeString(types[i]))
					.append(", ");
		}

		sql.append("PRIMARY KEY(").append(pkNames[0]);

		for (int i = 1; i < pkNames.length; i++) {
			sql.append(", ").append(pkNames[i]);
		}

		// close the instruction
		sql.append("))");

		return sql.toString();
	}

	/**
	 * Creates a create index statement
	 * 
	 * @param tableName
	 *            table where the index will be created
	 * @param pkNames
	 *            name of the fields where the index will be created
	 * 
	 * @return String
	 */
	public static String getPKIndexStatement(String tableName, String[] pkNames) {
		StringBuffer sql = new StringBuffer("CREATE INDEX ").append("index")
				.append(System.currentTimeMillis()).append(" ON ").append(
						tableName).append(" (").append(pkNames[0]);

		for (int i = 1; i < pkNames.length; i++) {
			sql.append(", ").append(pkNames[i]);
		}

		sql.append(")");

		return sql.toString();
	}

	/**
	 * Gets the name of the type to be used with the internal dbms
	 * 
	 * @param type
	 *            java.sql.Types constant
	 * 
	 * @return String
	 * 
	 * @throws RuntimeException
	 *             If the Type is not recognized
	 */
	public static String getTypeString(int type) {
		switch (type) {
		case Types.BIGINT:
			return "BIGINT";

		case Types.BIT:
		case Types.BOOLEAN:
			return "BOOLEAN";

		case Types.CHAR:
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
			return "VARCHAR";

		case Types.DATE:
			return "DATE";

		case Types.DECIMAL:
		case Types.NUMERIC:
		case Types.FLOAT:
		case Types.DOUBLE:
		case Types.REAL:
			return "DOUBLE";

		case Types.INTEGER:
			return "INTEGER";

		case Types.SMALLINT:
			return "SHORT";

		case Types.TINYINT:
			return "BYTE";

		case Types.BINARY:
		case Types.VARBINARY:
		case Types.LONGVARBINARY:
			return "BINARY";

		case Types.TIMESTAMP:
			return "TIMESTAMP";

		case Types.TIME:
			return "TIME";

		default:
			throw new RuntimeException("Cannot edit the type: " + type);
		}
	}

	/**
	 * Creates a new Insert statement in the specified table with the specified
	 * values for the corresponding field names
	 * 
	 * @param tableName
	 *            table name
	 * @param row
	 *            values to be inserted
	 * @param fieldNames
	 *            names of the fields to be inserted
	 * @param vWriter
	 *            DOCUMENT ME!
	 * 
	 * @return String
	 */
	public static String createInsertStatement(String tableName, Value[] row,
			String[] fieldNames, ValueWriter vWriter) {
		StringBuffer sql = new StringBuffer();
		sql.append("INSERT INTO ").append(tableName).append("(").append(
				fieldNames[0]);

		for (int i = 1; i < fieldNames.length; i++) {
			sql.append(", ").append(fieldNames[i]);
		}

		sql.append(") VALUES(").append(row[0].getStringValue(vWriter));

		for (int i = 1; i < row.length; i++) {
			sql.append(", ").append(row[i].getStringValue(vWriter));
		}

		return sql.append(")").toString();
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param database
	 *            DOCUMENT ME!
	 * @param sql
	 *            DOCUMENT ME!
	 * 
	 * @throws SQLException
	 *             DOCUMENT ME!
	 */
	public static void execute(String database, String sql) throws SQLException {
		Connection c = java.sql.DriverManager.getConnection("jdbc:hsqldb:file:"
				+ database);
		Statement st = c.createStatement();
		st.execute(sql);
		st.close();
		c.close();
	}

	/**
	 * creates an update statement in the specified table
	 * 
	 * @param tableName
	 *            table name
	 * @param pk
	 *            values of the primary key
	 * @param pkNames
	 *            name of the primary key fields
	 * @param fieldNames
	 *            name of all fields
	 * @param values
	 *            values to be updated
	 * @param vWriter
	 *            DOCUMENT ME!
	 * 
	 * @return String
	 */
	public static String createUpdateStatement(String tableName, Value[] pk,
			String[] pkNames, String[] fieldNames, Value[] values,
			ValueWriter vWriter) {
		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE ").append(tableName).append(" SET ").append(
				fieldNames[0]).append(" = ").append(
				values[0].getStringValue(vWriter));

		for (int i = 1; i < fieldNames.length; i++) {
			sql.append(", ").append(fieldNames[i]).append(" = ").append(
					values[i].getStringValue(vWriter));
		}

		sql.append(" WHERE ").append(pkNames[0]).append(" = ").append(
				pk[0].getStringValue(vWriter));

		for (int i = 1; i < pkNames.length; i++) {
			sql.append(" AND ").append(pkNames[0]).append(" = ").append(
					pk[0].getStringValue(vWriter));
		}

		return sql.toString();
	}
}
