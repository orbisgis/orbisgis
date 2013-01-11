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
package org.gdms.sql.function;

/**
 * Root interface for Gdmsql functions
 * @author Antoine Gourlay
 */
public interface Function {

        /**
         * Gets some usage description.
         * @return a string describing usage of this function
         */
        String getDescription();

        /**
         * Gets all possible signatures of this function.
         * @return a non-empty array of signatures
         */
        FunctionSignature[] getFunctionSignatures();

        /**
         * Gets the name of this function. This name will be used in SQL statements.
         * @return a non-empty string
         */
        String getName();

        /**
         * Gets an example of use of this function.
         * @return a string with an example query.
         */
        String getSqlOrder();

        /**
         * Gets if this function is a scalar function.
         * @return true if it is, false if not
         */
        boolean isScalar();

        /**
         * Gets if this function is a table function.
         * @return true if it is, false if not
         */
        boolean isTable();

        /**
         * Gets if this function is a aggregate function.
         * @return true if it is, false if not
         */
        boolean isAggregate();

        /**
         * Gets if this function is an executor function.
         * @return true if it is, false if not
         */
        boolean isExecutor();
}
