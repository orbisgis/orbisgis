/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer,
Thomas LEDUC, scientific researcher, Fernando GONZALEZ
 * CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.orbisgis.core;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.orbisgis.core.errorManager.ErrorManager;
import org.orbisgis.core.ui.plugins.views.output.OutputManager;

/**
 * 
 * An OrbisGIS service is just a java instance that is accessible by a name and that implements a specified interface. 
 * From a practical point of view, services will be the entry point to access the different functionalities in OrbisGIS
 * Class to manage the services
 * 
 */

public class Services {

	private static HashMap<Class<?>, Object> services = new HashMap<Class<?>, Object>();
	private static HashMap<Class<?>, String> servicesDoc = new HashMap<Class<?>, String>();

	/**
	 * Registers an interface as a service
	 * 
	 * @param interface_
	 *            Interface to be implemented by every instance of this service
	 * 
	 * @throws IllegalArgumentException
	 *             If the class specified in the second parameter is not an
	 *             interface
	 */
	public static void registerService(Class<? extends Object> interface_,
			String description) {
		if (!interface_.isInterface()) {
			throw new IllegalArgumentException("An interface "
					+ "class must be specified");
		}
		servicesDoc.put(interface_, description);
	}

	/**
	 * Registers an interface as a service setting an initial service instance
	 * 
	 * @param interface_
	 *            Interface to be implemented by every instance of this service
	 * @param instance
	 *            instance of the service
	 * 
	 * @throws IllegalArgumentException
	 *             If the class specified in the second parameter is not an
	 *             interface
	 */
	public static void registerService(Class<? extends Object> interface_,
			String description, Object instance) {
		servicesDoc.put(interface_, description);
		setService(interface_, instance);
	}

	/**
	 * Sets the instance of the specified service
	 * 
	 * @param name
	 *            Name of the service
	 * @param serviceInstance
	 *            Instance of the service
	 * @throws IllegalArgumentException
	 *             If the instance is not an implementation of the service
	 *             interface or there is no service registered under that name
	 */
	public static void setService(Class<?> interface_, Object serviceInstance) {
		if (!servicesDoc.containsKey(interface_)) {
			throw new IllegalArgumentException("The service "
					+ "is not registered: " + interface_);
		} else if (interface_.isAssignableFrom(serviceInstance.getClass())) {
			services.put(interface_, serviceInstance);
		} else {
			throw new IllegalArgumentException("The service instance "
					+ "must be an instance of : "
					+ interface_.getCanonicalName());
		}
	}

	/**
	 * Gets a human friendly list of services
	 * 
	 * @return
	 */
	public static Object[] getServices() {
		ArrayList<String> ret = new ArrayList<String>();
		Iterator<Class<?>> it = services.keySet().iterator();
		while (it.hasNext()) {
			Class<?> service = it.next();
			ret.add(service + " -> " + servicesDoc.get(service));
		}

		return ret.toArray(new Object[ret.size()]);
	}

	/**
	 * Prints an human friendly list of services
	 */
	public static void printServices() {
		Object[] services = getServices();
		for (Object service : services) {
			System.out.println(service);
		}
	}

	private static Iterator<Class<?>> getSortedIterator(Iterator<Class<?>> it) {
		TreeSet<Class<?>> orderedServices = new TreeSet<Class<?>>(
				new Comparator<Class<?>>() {

					@Override
					public int compare(Class<?> o1, Class<?> o2) {
						return o1.getName().compareTo(o2.getName());
					}
				});
		while (it.hasNext()) {
			orderedServices.add(it.next());
		}

		return orderedServices.iterator();
	}

	/**
	 * The same as 'Services.getService(ErrorManager.class)'
	 * 
	 * @return
	 */
	public static ErrorManager getErrorManager() {
		return getService(ErrorManager.class);
	}
	
	/**
	 * A method to get the outputmanager 
	 * @return {@link OutputManager}
	 */
	public static OutputManager getOutputManager(){
		return Services.getService(OutputManager.class);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getService(Class<T> serviceInterface) {
		T service = (T) services.get(serviceInterface);
		if (service != null) {
			return service;
		} else {
			throw new RuntimeException("Error initializating service : "
					+ serviceInterface);

		}
	}
}
