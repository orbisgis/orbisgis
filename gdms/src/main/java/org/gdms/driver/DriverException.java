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
/*
 * Created on 17-oct-2004
 */
package org.gdms.driver;

/**
 * Exception thrown when the operation with the DataSource cannot be done. It
 * can be due to the backend failure (the file has been removed, the data base
 * doesn't allow the connection) or to an internal error like IOException when
 * managing the internal buffers for the different operations.
 *
 * @author Fernando Gonzalez Cortes
 */
public class DriverException extends Exception {

        private static final long serialVersionUID = -1342514583124227518L;

        /**
         * Creates a new StartException object.
         */
        public DriverException() {
                super();
        }

        /**
         * Creates a new DriverException object.
         *
         * @param arg0
         */
        public DriverException(String arg0) {
                super(arg0);
        }

        /**
         * Creates a new DriverException object.
         *
         * @param arg0
         * @param arg1
         */
        public DriverException(String arg0, Throwable arg1) {
                super(arg0, arg1);
        }

        /**
         * Creates a new DriverException object.
         *
         * @param arg0
         */
        public DriverException(Throwable arg0) {
                super(arg0);
        }
}
