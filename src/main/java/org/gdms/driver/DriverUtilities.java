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

import java.io.File;

import org.gdms.driver.driverManager.Driver;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.driverManager.DriverManager;

/**
 * Utility method for the drivers
 */
public class DriverUtilities {

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

		throw new RuntimeException("code mismatch");
	}

	public static ReadOnlyDriver getDriver(DriverManager dm, File file) {
		String[] names = dm.getDriverNames();
		for (int i = 0; i < names.length; i++) {
			Driver driver = dm.getDriver(names[i]);
			if (driver instanceof FileDriver) {
				FileDriver fileDriver = (FileDriver) driver;
				String[] extensions = fileDriver.getFileExtensions();
				for (String extension : extensions) {
					if (file.getAbsolutePath().toLowerCase().endsWith(
							extension.toLowerCase())) {
						return fileDriver;
					}
				}
			}
		}

		throw new DriverLoadException("No suitable driver for "
				+ file.getAbsolutePath());
	}

	public static ReadOnlyDriver getDriver(DriverManager dm, String prefix) {
		String[] names = dm.getDriverNames();
		for (int i = 0; i < names.length; i++) {
			Driver driver = dm.getDriver(names[i]);
			if (driver instanceof DBDriver) {
				DBDriver dbDriver = (DBDriver) driver;
				String[] prefixes = dbDriver.getPrefixes();
				for (String driverPrefix : prefixes) {
					if (driverPrefix.toLowerCase().equals(prefix.toLowerCase())) {
						return dbDriver;
					}
				}
			}
		}

		throw new DriverLoadException("No suitable driver for " + prefix);
	}

}
