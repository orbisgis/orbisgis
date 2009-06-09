package org.orbisgis.core.geocognition;

import java.util.Map;

import org.orbisgis.core.edition.EditableElement;
import org.orbisgis.core.geocognition.mapContext.GeocognitionException;

public interface GeocognitionElement extends GeocognitionCommonElement,
		EditableElement {

	/**
	 * Sets the id if this element. The id must be unique in the parent
	 * GeocognitionElement
	 * 
	 * @param id
	 * @throws IllegalArgumentException
	 *             If there is already an element with that id or the element
	 *             doesn't allow the name change
	 */
	void setId(String id) throws IllegalArgumentException;

	/**
	 * @return
	 * @throws UnsupportedOperationException
	 *             If this element is a folder
	 */
	int getElementCount() throws UnsupportedOperationException;

	/**
	 * Return true if this element is a folder, false otherwise
	 * 
	 * @return
	 */
	boolean isFolder();

	/**
	 * @param i
	 * @return
	 * @throws UnsupportedOperationException
	 *             If this element is a folder
	 */
	GeocognitionElement getElement(int i) throws UnsupportedOperationException;

	/**
	 * @param element
	 * @throws UnsupportedOperationException
	 *             If this element is a folder
	 */
	void addElement(GeocognitionElement element)
			throws UnsupportedOperationException;

	/**
	 * Removes the specified element from the geocognition
	 * 
	 * @param element
	 * @return true if the element exists and is removed, false if it doesn't
	 *         exist
	 */
	public boolean removeElement(GeocognitionElement element);

	/**
	 * Removes the specified element from the geocognition
	 * 
	 * @param element
	 * @return true if the element exists and is removed, false if it doesn't
	 *         exist
	 */
	public boolean removeElement(String elementId);

	/**
	 * Gets the element with the specified id.
	 * 
	 * @param id
	 *            The contextual id (without slashes)
	 * @return The specified element or null if there is no element with such id
	 */
	GeocognitionElement getElement(String id);

	/**
	 * Get the parent element
	 * 
	 * @return
	 */
	GeocognitionElement getParent();

	/**
	 * Gets the path formed by the '/' separated ids from the root to this
	 * element
	 * 
	 * @return
	 */
	String getIdPath();

	/**
	 * Creates a identical copy of this element. Children are cloned recursively
	 * but its parent is set to null
	 * 
	 * @return
	 * @throws GeocognitionException
	 *             If the element cannot be cloned
	 */
	GeocognitionElement cloneElement() throws GeocognitionException;

	/**
	 * Return a XML with the information in this node
	 * 
	 * @param bos
	 * @throws GeocognitionException
	 *             If the content of the element cannot be serialized
	 */
	String getXMLContent() throws GeocognitionException;

	/**
	 * Changes this node content with the specified XML information. The content
	 * of the xml is typically the return value of a previous call to
	 * getXMLContent
	 * 
	 * @param is
	 * @throws GeocognitionException
	 *             If the specified content is not valid
	 */
	void setXMLContent(String xml) throws GeocognitionException;

	/**
	 * Get the properties of the element. The names and values of the properties
	 * vary from one element implementation to another
	 * 
	 * @return
	 */
	Map<String, String> getProperties();

}
