package org.orbisgis;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.orbisgis.pluginManager.error.ErrorManager;

/**
 * Class to manage the services
 *
 * @author Fernando Gonzalez Cortes
 *
 */
public class Services {

	private static HashMap<String, Object> services = new HashMap<String, Object>();
	private static HashMap<String, ServiceInfo> servicesClass = new HashMap<String, ServiceInfo>();

	/**
	 * Registers the service specifying the interface to be used
	 *
	 * @param name
	 *            Name of the service
	 * @param interface_
	 *            Interface to be implemented by every instance of this service
	 *
	 * @throws IllegalArgumentException
	 *             If the class specified in the second parameter is not an
	 *             interface
	 */
	public static void registerService(String name,
			Class<? extends Object> interface_, String description) {
		if (!interface_.isInterface()) {
			throw new IllegalArgumentException("An interface "
					+ "class must be specified");
		}
		servicesClass.put(name, new ServiceInfo(description, interface_));
	}

	/**
	 * Registers the service specifying the interface to be used and setting an
	 * instance of the service
	 *
	 * @param name
	 *            Name of the service
	 * @param interface_
	 *            Interface to be implemented by every instance of this service
	 * @param instance
	 *            instance of the service
	 *
	 * @throws IllegalArgumentException
	 *             If the class specified in the second parameter is not an
	 *             interface
	 */
	public static void registerService(String name,
			Class<? extends Object> interface_, String description,
			Object instance) {
		servicesClass.put(name, new ServiceInfo(description, interface_));
		setService(name, instance);
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
	public static void setService(String name, Object serviceInstance) {
		Class<? extends Object> serviceClass = servicesClass.get(name).interface_;
		if (serviceClass == null) {
			throw new IllegalArgumentException("The service "
					+ "is not registered: " + name);
		} else if (serviceClass.isAssignableFrom(serviceInstance.getClass())) {
			services.put(name, serviceInstance);
		} else {
			throw new IllegalArgumentException("The service instance "
					+ "must be an instance of : "
					+ serviceClass.getCanonicalName());
		}
	}

	/**
	 * Gets the service instance
	 *
	 * @param name
	 *            Name of the service
	 * @return
	 */
	public static Object getService(String name) {
		return services.get(name);
	}

	/**
	 * Gets a human friendly list of services
	 *
	 * @return
	 */
	public static String[] getServices() {
		ArrayList<String> ret = new ArrayList<String>();
		Iterator<String> it = services.keySet().iterator();
		while (it.hasNext()) {
			String serviceName = it.next();
			ret.add(serviceName + " -> " + servicesClass.get(serviceName));
		}

		return ret.toArray(new String[0]);
	}

	/**
	 * Prints an human friendly list of services
	 */
	public static void printServices() {
		String[] services = getServices();
		for (String service : services) {
			System.out.println(service);
		}
	}

	public static void generateDoc(File output) throws TransformerException {
		// Create the xml with the services information
		StringBuffer xmlContent = new StringBuffer();
		xmlContent.append("<services>");
		Iterator<String> it = services.keySet().iterator();
		it = getSortedIterator(it);
		while (it.hasNext()) {
			String serviceName = it.next();
			ServiceInfo serviceInfo = servicesClass.get(serviceName);
			Class<? extends Object> interface_ = serviceInfo.interface_;
			String description = serviceInfo.doc;
			xmlContent.append("<service name=\"" + serviceName
					+ "\" interface=\"" + interface_.getName()
					+ "\" description=\"" + description + "\"/>");
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

	private static Iterator<String> getSortedIterator(Iterator<String> it) {
		TreeSet<String> orderedServices = new TreeSet<String>();
		while (it.hasNext()) {
			String elem = it.next();
			orderedServices.add(elem);
		}

		return orderedServices.iterator();
	}

	private static class ServiceInfo {
		private String doc;
		private Class<? extends Object> interface_;

		public ServiceInfo(String doc, Class<? extends Object> interface_) {
			super();
			this.doc = doc;
			this.interface_ = interface_;
		}

	}

	/**
	 * The same as '(ErrorManager)
	 * Services.getService("org.orbisgis.ErrorManager")'
	 *
	 * @return
	 */
	public static ErrorManager getErrorManager() {
		return (ErrorManager) getService("org.orbisgis.ErrorManager");
	}
}
