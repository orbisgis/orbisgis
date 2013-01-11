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
 * Interface to obtain the wrapped value of some Value instance.
 *
 * @author Fernando Gonzalez Cortes
 */
public interface ValueWriter {

        ValueWriter DEFAULTWRITER = new ValueWriterImpl();

        /**
         * Gets the string representation of a long as it would appear in a SQL statement.
         *
         * @param i long to format
         * @return SQL representation
         */
        String getStatementString(long i);

        /**
         * Gets the string representation of an integer as it would appear in a SQL statement
         *
         * @param i integer to format
         * @param sqlType type of integer (one of the following java.sql.Types
         * constants: INTEGER, SMALLINT, TINYINT)
         * @return SQL representation
         */
        String getStatementString(int i, int sqlType);

        /**
         * Gets the string representation of a double as it would appear in a SQL statement
         *
         * @param d double to format
         * @param sqlType type of the parameter (one of the following java.sql.Types
         * constants: DOUBLE, FLOAT, REAL, NUMERIC, DECIMAL)
         * @return SQL representation
         */
        String getStatementString(double d, int sqlType);

        /**
         * Gets the string representation of some text as it would appear in a SQL statement.
         *
         * @param str string to format
         * @param sqlType type of the parameter (one of the following java.sql.Types
         * constants: CHAR, VARCHAR, LONGVARCHAR)
         * @return SQL representation
         */
        String getStatementString(CharSequence str, int sqlType);

        /**
         * Gets the string representation of a date as it would appear in a SQL statement.
         *
         * @param d Date to format
         * @return SQL representation
         */
        String getStatementString(Date d);

        /**
         * Gets the string representation of a time as it would appear in a SQL statement.
         *
         * @param t Time to format
         * @return SQL representation
         */
        String getStatementString(Time t);

        /**
         * Gets the string representation of a timestamp as it would appear in a SQL statement.
         *
         * @param ts timestamp to format
         * @return SQL representation
         */
        String getStatementString(Timestamp ts);

        /**
         * Gets the string representation of a binary as it would appear in a SQL statement.
         *
         * @param binary byte array to format
         * @return SQL representation
         */
        String getStatementString(byte[] binary);

        /**
         * Gets the string representation of a boolean as it would appear in a SQL statement.
         *
         * @param b boolean to format
         *
         * @return SQL representation
         */
        String getStatementString(boolean b);

        /**
         * Gets the string representation of a geometry as it would appear in a
         * SQL statement.
         *
         * @param g geometry to format
         * @return SQL representation
         */
        String getStatementString(Geometry g);

        /**
         * Gets the string representation of a null value as it would appear in a SQL statement.
         * @return SQL representation
         */
        String getNullStatementString();
}
