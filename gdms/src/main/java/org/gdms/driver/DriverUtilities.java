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
package org.gdms.driver;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import java.io.File;

import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.driverManager.DriverManager;

/**
 * Utility method for the drivers
 */
public final class DriverUtilities {

        /**
         * Translates the specified code by using the translation table specified by
         * the two last arguments. If there is no translation a RuntimeException is
         * thrown.
         *
         * @param code
         *            code to translate
         * @param source
         *            keys on the translation table
         * @param target
         *            translation to the keys
         *
         * @return translated code
         */
        public static int translate(int code, int[] source, int[] target) {
                for (int i = 0; i < source.length; i++) {
                        if (code == source[i]) {
                                return target[i];
                        }
                }

                throw new IllegalArgumentException("code mismatch");
        }

        /**
         * Gets the first driver registered with the DriverManager that accepts the specified file.
         * @param dm
         * @param file
         * @return
         * @throw DriverLoadException If we can't find a driver able to manage
         * the given {@code File}.
         */
        public static FileDriver getDriver(DriverManager dm, File file) {
                return dm.getDriver(file);
        }

        /**
         * Gets the first driver registered with the DriverManager that supports the
         * specified prefix
         * @param dm
         * @param prefix
         * @return
         */
        public static DBDriver getDriver(DriverManager dm, String prefix) {
                String[] names = dm.getDriverNames();
                for (int i = 0; i < names.length; i++) {
                        Driver driver = dm.getDriver(names[i]);
                        if (driver instanceof DBDriver) {
                                DBDriver dbDriver = (DBDriver) driver;
                                String[] prefixes = dbDriver.getPrefixes();
                                for (String driverPrefix : prefixes) {
                                        if (driverPrefix.equalsIgnoreCase(prefix)) {
                                                return dbDriver;
                                        }
                                }
                        }
                }

                throw new DriverLoadException("No suitable driver for " + prefix);
        }

        /**
         * gets the full extent of a DataSet data set.
         * The extent is computed using the {@link DataSet#getScope(int) }
         * method.
         * @param r
         * @return an Envelope for the Extent, or null if it cannot be computed
         * @throws DriverException
         */
        public static Envelope getFullExtent(DataSet r) throws DriverException {
                Number[] xScope = r.getScope(DataSet.X);
                Number[] yScope = r.getScope(DataSet.Y);
                if ((xScope != null) && (yScope != null)) {
                        return new Envelope(new Coordinate(xScope[0].doubleValue(),
                                yScope[0].doubleValue()), new Coordinate(xScope[1].doubleValue(),
                                yScope[1].doubleValue()));
                } else {
                        return null;
                }
        }

        private DriverUtilities() {
        }
}
