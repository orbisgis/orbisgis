/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALES CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALES CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.driver.driverManager;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * Para el driver manager, el driver viene determinado por un directorio dentro
 * del cual se encuentran uno o m�s jar's. La clase Driver ha de implementar la
 * interfaz Driver y su nombre debe terminar en "Driver" y tener un constructor
 * sin par�metros.
 *
 * <p>
 * Esta clase es la encargada de la carga y validaci�n de los drivers y de la
 * obtenci�n de los mismo apartir de un tipo
 * </p>
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class DriverManager {
	private static Logger logger = Logger.getLogger(DriverManager.class
			.getName());
	private DriverValidation validation;
	private HashMap<String, Class<? extends Driver>> nombreDriverClass = new HashMap<String, Class<? extends Driver>>();
	private ArrayList<Throwable> failures = new ArrayList<Throwable>();

	/**
	 * Devuelve un array con los directorios de los plugins
	 *
	 * @param dirExt
	 *            Directorio a partir del que se cuelgan los directorios de los
	 *            drivers
	 *
	 * @return Array de los subdirectorios
	 */
	private File[] getPluginDirs(File dirExt) {
		if (!dirExt.exists()) {
			return new File[0];
		}

		ArrayList<File> ret = new ArrayList<File>();
		File[] files = dirExt.listFiles();

		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				ret.add(files[i]);
			}
		}

		return ret.toArray(new File[0]);
	}

	/**
	 * Obtiene los jar's de un directorio y los devuelve en un array
	 *
	 * @param dir
	 *            Directorio del que se quieren obtener los jars
	 *
	 * @return Array de jars
	 */
	private URL[] getJars(File dir) {
		ArrayList<URL> ret = new ArrayList<URL>();
		File[] dirContent = dir.listFiles();

		for (int i = 0; i < dirContent.length; i++) {
			if (dirContent[i].getName().toLowerCase().endsWith(".jar")) {
				try {
					ret.add(new URL("file:" + dirContent[i].getAbsolutePath()));
				} catch (MalformedURLException e) {
					// No se puede dar
				}
			}
		}

		return ret.toArray(new URL[0]);
	}

	/**
	 * Carga los drivers y asocia con el tipo del driver.
	 *
	 * @param dir
	 *            Directorio ra�z de los drivers
	 */
	@SuppressWarnings("unchecked")
	public void loadDrivers(File dir) {
		try {
			if (validation == null) {
				validation = new DriverValidation() {
					public boolean validate(Driver d) {
						return true;
					}
				};
			}

			// Se obtiene la lista de directorios
			File[] dirs = getPluginDirs(dir);

			// Para cada directorio se obtienen todos sus jars
			for (int i = 0; i < dirs.length; i++) {
				logger.debug("Processing " + dirs[i] + "... ");
				URL[] jars = getJars(dirs[i]);

				// Se crea el classloader
				DriverClassLoader cl = new DriverClassLoader(jars, dirs[i]
						.getAbsolutePath(), this.getClass().getClassLoader());

				// Se obtienen los drivers
				Class<? extends Driver>[] drivers = cl.getDrivers();

				// Se asocian los drivers con su tipo si superan la validaci�n
				for (int j = 0; j < drivers.length; j++) {
					try {
						Driver driver = (Driver) drivers[j].newInstance();

						if (validation.validate(driver)) {
							if (nombreDriverClass.put(driver.getName(),
									drivers[j]) != null) {
								throw new IllegalStateException(
										"Two drivers with the same name");
							}
						}
					} catch (ClassCastException e) {
						/*
						 * No todos los que terminan en Driver son drivers de
						 * los nuestros, los ignoramos
						 */
					} catch (Throwable t) {
						/*
						 * A�n a riesgo de capturar algo que no debemos,
						 * ignoramos cualquier driver que pueda dar cualquier
						 * tipo de problema, pero continuamos
						 */
						failures.add(t);
					}
				}
			}
		} catch (ClassNotFoundException e) {
			failures.add((Throwable) e);
		} catch (IOException e) {
			failures.add((Throwable) e);
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Throwable[] getLoadFailures() {
		return failures.toArray(new Throwable[0]);
	}

	/**
	 * Obtiene el Driver asociado al tipo que se le pasa como par�metro
	 *
	 * @param name
	 *            Objeto que devolvi� alguno de los drivers en su m�todo getType
	 *
	 * @return El driver asociado o null si no se encuentra el driver
	 *
	 * @throws DriverLoadException
	 *             if this Class represents an abstract class, an interface, an
	 *             array class, a primitive type, or void; or if the class has
	 *             no nullary constructor; or if the instantiation fails for
	 *             some other reason
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

	public void registerDriver(String name, Class<? extends Driver> driverClass) {
		if (recursiveIsA(driverClass, Driver.class)) {
			nombreDriverClass.put(name, driverClass);
		} else {
			throw new RuntimeException(driverClass.getName()
					+ " is not an instance of "
					+ "com.hardcode.driverManager.Driver");
		}
	}

	/**
	 * Establece el objeto validador de los drivers. En la carga se comprobar�
	 * si cada driver es v�lido mediante el m�todo validate del objeto
	 * validation establecido con este m�todo. Pro defecto se validan todos los
	 * drivers
	 *
	 * @param validation
	 *            objeto validador
	 */
	public void setValidation(DriverValidation validation) {
		this.validation = validation;
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

	/**
	 * DOCUMENT ME!
	 *
	 * @param driverName
	 *            DOCUMENT ME!
	 * @param superClass
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws RuntimeException
	 *             DOCUMENT ME!
	 */
	public boolean isA(String driverName, Class<? extends Driver> superClass) {
		Class<? extends Driver> driverClass = nombreDriverClass.get(driverName);

		if (driverClass == null) {
			throw new RuntimeException("No such driver");
		}

		Class<? extends Object>[] interfaces = driverClass.getInterfaces();

		if (interfaces != null) {
			for (int i = 0; i < interfaces.length; i++) {
				if (interfaces[i] == superClass) {
					return true;
				} else {
					if (recursiveIsA(interfaces[i], superClass)) {
						return true;
					}
				}
			}
		}

		Class<? extends Object> class_ = driverClass.getSuperclass();

		if (class_ != null) {
			if (class_ == superClass) {
				return true;
			} else {
				if (recursiveIsA(class_, superClass)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param interface_
	 *            DOCUMENT ME!
	 * @param superInterface
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	private boolean recursiveIsA(Class<? extends Object> interface_,
			Class<? extends Object> superInterface) {
		Class<? extends Object>[] interfaces = interface_.getInterfaces();

		if (interfaces != null) {
			for (int i = 0; i < interfaces.length; i++) {
				if (interfaces[i] == superInterface) {
					return true;
				} else {
					if (recursiveIsA(interfaces[i], superInterface)) {
						return true;
					}
				}
			}
		}

		Class<? extends Object> class_ = interface_.getSuperclass();

		if (class_ != null) {
			if (class_ == superInterface) {
				return true;
			} else {
				if (recursiveIsA(class_, superInterface)) {
					return true;
				}
			}
		}

		return false;
	}
}
