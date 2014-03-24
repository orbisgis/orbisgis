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
 * Copyright (C) 2007-2014 IRSTV FR CNRS 2488
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
 * Used to keep a DataSource from being closed to many times
 *
 * @author Fernando Gonz�lez Cort�s
 */
public final class OpenCloseCounter {

        private int counter = 0;
        private String dsName;

        public OpenCloseCounter(String dsName) {
                this.dsName = dsName;
        }

        /**
         * Returns true if the DataSource has to open the driver or it's already
         * opened
         *
         * @return
         */
        public boolean start() {
                counter++;
                return counter == 1;
        }

        /**
         * Returns true if the DataSource has to close the driver or must be kept
         * open until, at least, next call to close.
         *
         * @return
         */
        public boolean stop() {
                counter--;

                if (counter == 0) {
                        return true;
                } else if (counter < 0) {
                        counter = 0;
                        throw new AlreadyClosedException(
                                "DataSource closed too many times: " + dsName);
                } else {
                        return false;
                }
        }

        /**
         * Returns true if the datasource is opened and false if it's closed
         *
         * @return
         */
        public boolean isOpen() {
                return counter > 0;
        }
}
