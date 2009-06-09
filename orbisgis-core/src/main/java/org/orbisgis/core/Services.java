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

import org.orbisgis.errorManager.ErrorManager;

/**
 * Class to manage the services
 *
 * @author Fernando Gonzalez Cortes
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
	public static Class<?>[] getServices() {
		ArrayList<String> ret = new ArrayList<String>();
		Iterator<Class<?>> it = services.keySet().iterator();
		while (it.hasNext()) {
			Class<?> service = it.next();
			ret.add(service + " -> " + servicesDoc.get(service));
		}

		return ret.toArray(new Class<?>[0]);
	}

	/**
	 * Prints an human friendly list of services
	 */
	public static void printServices() {
		Class<?>[] services = getServices();
		for (Class<?> service : services) {
			System.out.println(service);
		}
	}

	public static void generateDoc(File output) throws TransformerException {
		// Create the xml with the services information
		StringBuffer xmlContent = new StringBuffer();
		xmlContent.append("<services>");
		Iterator<Class<?>> it = services.keySet().iterator();
		it = getSortedIterator(it);
		while (it.hasNext()) {
			Class<?> service = it.next();
			String description = servicesDoc.get(service);
			xmlContent.append("<service name=\"" + service + "\" interface=\""
					+ service.getName() + "\" description=\"" + description
					+ "\"/>");
		}
		xmlContent.append("</services>");

		// Use a XSTL to get a nice (well not so nice) HTML
		TransformerFactory transFact = TransformerFactory.newInstance();
		StreamSource xmlSource = new StreamSource(new ByteArrayInputStream(
				xmlContent.toString().getBytes()));
		InputStream is = Services.class
				.getResourceAsStream("/org/orbisgis/services-documentation.xsl");
		StreamSource xsltSource = new StreamSource(is);

		Transformer trans = transFact.newTransformer(xsltSource);
		new File("docs").mkdirs();
		trans.transform(xmlSource, new StreamResult(output));

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

	@SuppressWarnings("unchecked")
	public static <T> T getService(Class<T> serviceInterface) {
		T service = (T) services.get(serviceInterface);
		if (service != null) {
			return service;
		} else {
			throw new RuntimeException("Error initializating service : " + serviceInterface);

		}
	}
}
