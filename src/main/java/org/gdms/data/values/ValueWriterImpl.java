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
package org.gdms.data.values;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import com.vividsolutions.jts.geom.Geometry;

/**
 * @author Fernando Gonzalez Cortes
 */
class ValueWriterImpl implements ValueWriter {

	private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	private DecimalFormat df = new DecimalFormat();

	public ValueWriterImpl() {
		df.setGroupingUsed(false);
		df.setMaximumFractionDigits(Integer.MAX_VALUE);
	}

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
		return df.format(d);
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

	public String getStatementString(Geometry g) {
		return "'" + g.toText() + "'";
	}

}
