/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
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

        ValueWriter internalValueWriter = new ValueWriterImpl();

        /**
         * Gets the string of the i param as it would appear in a SQL statement
         *
         * @param i
         *            long to format
         *
         * @return String
         */
        String getStatementString(long i);

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
        String getStatementString(int i, int sqlType);

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
        String getStatementString(double d, int sqlType);

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
        String getStatementString(String str, int sqlType);

        /**
         * Gets the string of the param as it would appear in a SQL statement
         *
         * @param d
         *            Date to format
         *
         * @return String
         */
        String getStatementString(Date d);

        /**
         * Gets the string of the param as it would appear in a SQL statement
         *
         * @param t
         *            Time to format
         *
         * @return String
         */
        String getStatementString(Time t);

        /**
         * Gets the string of the param as it would appear in a SQL statement
         *
         * @param ts
         *            timestamp to format
         *
         * @return String
         */
        String getStatementString(Timestamp ts);

        /**
         * Gets the string of the binary param as it would appear in a SQL statement
         *
         * @param binary
         *            byte array to format
         *
         * @return String
         */
        String getStatementString(byte[] binary);

        /**
         * Gets the string of the binary param as it would appear in a SQL statement
         *
         * @param b
         *            byte array to format
         *
         * @return String
         */
        String getStatementString(boolean b);

        /**
         * Gets the string representation of the geometry as it would appear in a
         * SQL statement
         *
         * @param g
         *            Geometry
         *
         * @return String
         */
        String getStatementString(Geometry g);

        /**
         * Gets the string of the binary param as it would appear in a SQL statement
         *
         * @return String
         */
        String getNullStatementString();
}
