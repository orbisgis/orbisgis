package org.orbisgis.core.geocognition;

import org.orbisgis.core.PersistenceException;

public interface GeocognitionElementFactory {

	/**
	 * Gets the JAXB context path (package with the generated classes) for the
	 * elements supported by this factory
	 *
	 * @return
	 */
	String getJAXBContextPath();

	/**
	 * Creates a geocognition element from the specified element
	 *
	 * @param object
	 * @return
	 */
	GeocognitionExtensionElement createGeocognitionElement(Object object);

	/**
	 * Creates a new instance of a the element supported in this factory. The
	 * instance contents are taken from the specified parameter which is the
	 * result of a JAXB unmarshallization. Returns null if the JAXB object
	 * doesn't match any of the supported elements
	 *
	 * @param jaxbObject
	 *            JAXB object
	 * @param contentTypeId
	 *            Type of the marshalled Geocognition element
	 * @return
	 * @throws PersistenceException
	 *             If the element cannot be recovered
	 */
	GeocognitionExtensionElement createElementFromXML(Object jaxbObject,
			String contentTypeId) throws PersistenceException;

	/**
	 * Returns true if this factory accepts the specified object or not. This
	 * is, if the object can be assigned to a variable of type T
	 *
	 * @return
	 */
	boolean accepts(Object object);

	/**
	 * Return true if this factory produces elements that return the specified
	 * typeID in their getTypeId() method
	 *
	 * @param typeId
	 * @return
	 */
	boolean acceptContentTypeId(String typeId);

}
