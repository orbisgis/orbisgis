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
package org.gdms.driver.driverManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 
 * @author Fernando Gonzalez Cortes
 */
public class DriverManager {
	private HashMap<String, Class<? extends Driver>> nombreDriverClass = new HashMap<String, Class<? extends Driver>>();
	private ArrayList<Throwable> failures = new ArrayList<Throwable>();

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public Throwable[] getLoadFailures() {
		return failures.toArray(new Throwable[0]);
	}

	/**
	 * Get the driver by name
	 * 
	 * @param name
	 *            name of the desired driver
	 * 
	 * @return
	 * 
	 * @throws DriverLoadException
	 *             if the driver class represents an abstract class, an
	 *             interface, an array class, a primitive type, or void; or if
	 *             the class has no nullary constructor; or if the instantiation
	 *             fails for some other reason
	 */
	public Driver getDriver(String name) throws DriverLoadException {
		try {
			Class<? extends Driver> driverClass = nombreDriverClass.get(name);
			if (driverClass == null)
				throw new DriverLoadException("Driver not found: " + name);
			return (Driver) driverClass.newInstance();
		} catch (InstantiationException e) {
			throw new DriverLoadException(e);
		} catch (IllegalAccessException e) {
			throw new DriverLoadException(e);
		}
	}

	public void registerDriver(Class<? extends Driver> driverClass) {
		if (Driver.class.isAssignableFrom(driverClass)) {
			Driver driver;
			try {
				driver = driverClass.newInstance();
				nombreDriverClass.put(driver.getDriverId(), driverClass);
			} catch (InstantiationException e) {
				throw new IllegalArgumentException(
						"The driver cannot be instantiated", e);
			} catch (IllegalAccessException e) {
				throw new IllegalArgumentException(
						"The driver cannot be instantiated", e);
			}
		} else {
			throw new RuntimeException(driverClass.getName()
					+ " is not an instance of " + Driver.class.getName());
		}
	}

	/**
	 * Obtiene los tipos de todos los drivers del sistema
	 * 
	 * @return DOCUMENT ME!
	 */
	public String[] getDriverNames() {
		ArrayList<String> names = new ArrayList<String>(nombreDriverClass
				.size());

		Iterator<String> iterator = nombreDriverClass.keySet().iterator();

		while (iterator.hasNext()) {
			names.add((String) iterator.next());
		}

		return names.toArray(new String[0]);
	}

	/**
	 * Obtiene la clase del driver relacionado con el tipo que se pasa como
	 * par�metro
	 * 
	 * @param driverName
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public Class<? extends Driver> getDriverClassByName(String driverName) {
		return nombreDriverClass.get(driverName);
	}

	public Driver[] getDrivers(DriverFilter driverFilter) {
		ArrayList<Driver> drivers = new ArrayList<Driver>();

		Iterator<Class<? extends Driver>> iterator = nombreDriverClass.values()
				.iterator();

		while (iterator.hasNext()) {
			try {
				Driver driver = iterator.next().newInstance();
				if (driverFilter.acceptDriver(driver)) {
					drivers.add(driver);
				}
			} catch (InstantiationException e) {
				// ignore
			} catch (IllegalAccessException e) {
				// ignore
			}
		}

		return drivers.toArray(new Driver[0]);
	}
}
