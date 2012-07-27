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
package org.gdms.data;

/**
 * This class represents the errors that happens when the DataSourceFactory
 * is asked for a table that doesn't exists. The common mistakes are
 * typing errors, forgetting to register the source, handling of two different
 * instances of a DataSourceFactory
 *
 * @author Fernando Gonzalez Cortes
 */
public class NoSuchTableException extends Exception {

        private static final long serialVersionUID = -7461400187366540811L;

        /**
         * Creates a new NoSuchTableException object.
         */
        public NoSuchTableException() {
                super();
        }

        /**
         * Creates a new NoSuchTableException object.
         *
         * @param tableName
         */
        public NoSuchTableException(String tableName) {
                super(tableName);
        }

        /**
         * Creates a new NoSuchTableException object.
         *
         * @param arg0
         */
        public NoSuchTableException(Throwable arg0) {
                super(arg0);
        }

        /**
         * Creates a new NoSuchTableException object.
         *
         * @param tableName
         * @param arg1
         */
        public NoSuchTableException(String tableName, Throwable arg1) {
                super(tableName, arg1);
        }

        private String format(String tableName) {
                return String.format("The table %s does not exist!", tableName);
        }

        @Override
        public String getMessage() {
                return format(super.getMessage());
        }
}
