package org.gdms.data.values;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import com.vividsolutions.jts.geom.Geometry;

/**
 * DOCUMENT ME!
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public interface ValueWriter {
	public final static ValueWriter internalValueWriter = new ValueWriterImpl();

	/**
	 * Gets the string of the i param as it would appear in a SQL statement
	 * 
	 * @param i
	 *            long to format
	 * 
	 * @return String
	 */
	public String getStatementString(long i);

	/**
	 * Gets the string of the i param as it would appear in a SQL statement
	 * 
	 * @param i
	 *            integer to format
	 * @param sqlType
	 *            SQL type of the parameter. Any of the following java.sql.Types
	 *            constants: INTEGER, SMALLINT, TINYINT
	 * 
	 * @return String
	 */
	public String getStatementString(int i, int sqlType);

	/**
	 * Gets the string of the d param as it would appear in a SQL statement
	 * 
	 * @param d
	 *            double to format
	 * @param sqlType
	 *            SQL type of the parameter. Any of the following java.sql.Types
	 *            constants: DOUBLE, FLOAT, REAL, NUMERIC, DECIMAL
	 * 
	 * @return String
	 */
	public String getStatementString(double d, int sqlType);

	/**
	 * Gets the string of the str param as it would appear in a SQL statement
	 * 
	 * @param str
	 *            string to format
	 * @param sqlType
	 *            SQL type of the parameter. Any of the following java.sql.Types
	 *            constants: CHAR, VARCHAR, LONGVARCHAR
	 * 
	 * @return String
	 */
	public String getStatementString(String str, int sqlType);

	/**
	 * Gets the string of the param as it would appear in a SQL statement
	 * 
	 * @param d
	 *            Date to format
	 * 
	 * @return String
	 */
	public String getStatementString(Date d);

	/**
	 * Gets the string of the param as it would appear in a SQL statement
	 * 
	 * @param t
	 *            Time to format
	 * 
	 * @return String
	 */
	public String getStatementString(Time t);

	/**
	 * Gets the string of the param as it would appear in a SQL statement
	 * 
	 * @param ts
	 *            timestamp to format
	 * 
	 * @return String
	 */
	public String getStatementString(Timestamp ts);

	/**
	 * Gets the string of the binary param as it would appear in a SQL statement
	 * 
	 * @param binary
	 *            byte array to format
	 * 
	 * @return String
	 */
	public String getStatementString(byte[] binary);

	/**
	 * Gets the string of the binary param as it would appear in a SQL statement
	 * 
	 * @param b
	 *            byte array to format
	 * 
	 * @return String
	 */
	public String getStatementString(boolean b);

	/**
	 * Gets the string representation of the geometry as it would appear in a
	 * SQL statement
	 * 
	 * @param g
	 *            Geometry
	 * 
	 * @return String
	 */
	public String getStatementString(Geometry g);

	/**
	 * Gets the string of the binary param as it would appear in a SQL statement
	 * 
	 * @return String
	 */
	public String getNullStatementString();
}
