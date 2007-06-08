package org.gdms.data.values;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.gdms.spatial.GeometryValue;

/**
 * @author Fernando Gonz�lez Cort�s
 */
class ValueWriterImpl implements ValueWriter {

	private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

	/**
	 * @see org.gdms.data.values.ValueWriter#getStatementString(long)
	 */
	public String getStatementString(long i) {
		return Long.toString(i);
	}

	/**
	 * @see org.gdms.data.values.ValueWriter#getStatementString(int, int)
	 */
	public String getStatementString(int i, int sqlType) {
		return Integer.toString(i);
	}

	/**
	 * @see org.gdms.data.values.ValueWriter#getStatementString(double, int)
	 */
	public String getStatementString(double d, int sqlType) {
		return Double.toString(d);
	}

	/**
	 * @see org.gdms.data.values.ValueWriter#getStatementString(java.lang.String,
	 *      int)
	 */
	public String getStatementString(String str, int sqlType) {
		return "'" + escapeString(str) + "'";
	}

	/**
	 * @see org.gdms.data.values.ValueWriter#getStatementString(java.sql.Date)
	 */
	public String getStatementString(Date d) {
		return "'" + d.toString() + "'";
	}

	/**
	 * @see org.gdms.data.values.ValueWriter#getStatementString(java.sql.Time)
	 */
	public String getStatementString(Time t) {
		return "'" + timeFormat.format(t) + "'";
	}

	/**
	 * @see org.gdms.data.values.ValueWriter#getStatementString(java.sql.Timestamp)
	 */
	public String getStatementString(Timestamp ts) {
		return "'" + ts.toString() + "'";
	}

	/**
	 * @see org.gdms.data.values.ValueWriter#getStatementString(byte[])
	 */
	public String getStatementString(byte[] binary) {
		StringBuffer sb = new StringBuffer("'");
		for (int i = 0; i < binary.length; i++) {
			int byte_ = binary[i];
			if (byte_ < 0)
				byte_ = byte_ + 256;
			String b = Integer.toHexString(byte_);
			if (b.length() == 1)
				sb.append("0").append(b);
			else
				sb.append(b);

		}
		sb.append("'");

		return sb.toString();
	}

	/**
	 * @see org.gdms.data.values.ValueWriter#getStatementString(boolean)
	 */
	public String getStatementString(boolean b) {
		return Boolean.toString(b);
	}

	/**
	 * @see org.gdms.data.values.ValueWriter#getNullStatementString()
	 */
	public String getNullStatementString() {
		return "null";
	}

	static String escapeString(String string) {
		return string.replaceAll("\\Q'\\E", "''");
	}

	public String getStatementString(GeometryValue g) {
		return "'" + g.getGeom().toText() + "'";
	}

}
