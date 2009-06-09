package org.orbisgis.core.geocognition;

import java.util.Map;

import org.orbisgis.core.geocognition.mapContext.GeocognitionException;

public interface GeocognitionExtensionElement extends GeocognitionCommonElement {

	/**
	 * Gets a JAXB object representing this element when the last call to save
	 * or open was made
	 * 
	 * @return
	 */
	Object getRevertJAXBObject();

	/**
	 * Set the listener of changes in this element.
	 * 
	 * @param listener
	 */
	void setElementListener(GeocognitionElementContentListener listener);

	/**
	 * Return if this element was modified since the last time save or open was
	 * called. An automatic check is done comparing the results of getJAXBObject
	 * and getRevertJAXBObject so it is only necessary to implement this method
	 * if such a comparison is not enough. If it is enough, just return false
	 * 
	 * @return
	 */
	boolean isModified();

	/**
	 * Makes this element change its content to the specified by the jaxb object
	 * 
	 * @param jaxbObject
	 * @throws IllegalArgumentException
	 *             If the specified JAXB content is not legal
	 * @throws GeocognitionException
	 *             If there is any other problem
	 */
	void setJAXBObject(Object jaxbObject) throws IllegalArgumentException,
			GeocognitionException;

	/**
	 * Returns the id of the element if it's fixed. Otherwise it returns null. A
	 * fixed id cannot be changed later.
	 * 
	 * @return
	 */
	String getFixedId();

	/**
	 * Called when the id of the element has been changed. Every element has an
	 * id and it is set at least once just after the creation
	 * 
	 * @param newId
	 *            new Id of the element
	 * @throws GeocognitionException
	 *             If this concrete id change cannot be performed
	 */
	void idChanged(String newId) throws GeocognitionException;

	/**
	 * Called when the element has been removed from geocognition. This method
	 * will typically be used to release resources
	 */
	void elementRemoved();

	/**
	 * Return a property map. Each implementation can have any number of
	 * properties
	 * 
	 * @return
	 */
	Map<String, String> getProperties();

}
