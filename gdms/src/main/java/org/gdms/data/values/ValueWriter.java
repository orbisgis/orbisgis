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

import com.vividsolutions.jts.geom.Geometry;

/**
 * Interface to obtain the wrapped value of some Value instance
 *
 * @author Fernando Gonzalez Cortes
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
