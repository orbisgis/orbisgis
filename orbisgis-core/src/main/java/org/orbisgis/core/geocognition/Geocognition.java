package org.orbisgis.core.geocognition;

import java.io.InputStream;
import java.io.OutputStream;

import org.orbisgis.core.PersistenceException;

/**
 * Organizes the resources hierarchically giving serialization capabilities.
 * Elements are identified by a slash-separated string containing the path to
 * the given element.
 *
 * @author Fernando Gonzalez Cortes
 *
 */
public interface Geocognition {

	/**
	 * Writes the geocognition in the specified stream
	 *
	 * @param os
	 * @throws PersistenceException
	 *             If the write operation fails
	 */
	void write(OutputStream os) throws PersistenceException;

	/**
	 * Writes a folder containing all the specified elements in the specified
	 * stream. If only a folder id is specified it is written directly in the
	 * stream (in this case the id of this top element will not be stored in the
	 * stream)
	 *
	 * @param os
	 * @param ids
	 *            the ids of the nodes to write
	 * @throws PersistenceException
	 *             If the write operation fails
	 * @throws IllegalArgumentException
	 *             If any of the specified ids does not point to an existing
	 *             node
	 */
	void write(OutputStream os, String... ids) throws PersistenceException,
			IllegalArgumentException;

	/**
	 * Writes the subtree starting in the specified node in the specified
	 * stream. It's not necessary that the node belongs to the tree
	 *
	 * @param elem
	 *            element to write
	 * @param os
	 * @throws PersistenceException
	 *             If the write operation fails
	 */
	void write(GeocognitionElement elem, OutputStream os)
			throws PersistenceException;

	/**
	 * Removes all the content from the geocognition
	 */
	void clear();

	/**
	 * Clears the geocognition and populates it with the contents in the stream
	 *
	 * @param is
	 * @throws PersistenceException
	 *             If the read operation fails
	 */
	void read(InputStream is) throws PersistenceException;

	/**
	 * Creates a geocognition tree from the specified input stream xml content
	 *
	 * @param is
	 * @throws PersistenceException
	 *             If the read operation fails
	 */
	GeocognitionElement createTree(InputStream is) throws PersistenceException;

	/**
	 * Creates a folder in geocognition
	 *
	 * @param id
	 * @return
	 */
	GeocognitionElement createFolder(String id);

	/**
	 * Gets the geocognition element with the specified id and of the specified
	 * type
	 *
	 * @param <T>
	 *            Type of the element
	 * @param id
	 *            id of the element
	 * @param c
	 *            Class of the element
	 * @return
	 */
	<T> T getElement(String id, Class<T> c);

	/**
	 * Adds an element in the geocognition. Folders will be added if the id
	 * specifies non existing ones
	 *
	 * @param id
	 *            unique id of the added element. It can't collide with any
	 *            existing id in the parent. If the id contain slashes they
	 *            indicate the full path of the element. Otherwise it is placed
	 *            in the root folder
	 * @param element
	 *            element to add
	 * @throws IllegalArgumentException
	 *             If the id already exists, the element type is not supported,
	 *             the id contains a non folder element
	 */
	void addElement(String id, Object element) throws IllegalArgumentException;

	/**
	 * Adds the specified element with the specified id
	 *
	 * @param id
	 * @param element
	 *            GeocognitionElement to be added
	 * @throws IllegalArgumentException
	 *             If the id already exists or the id contains a non folder
	 *             element
	 */
	void addGeocognitionElement(String id, GeocognitionElement element);

	/**
	 * Removes the element with the specified id from the geocatalog
	 *
	 * @param id
	 * @return The removed element or null if there is no element with that id
	 *         or the removal was not possible
	 */
	GeocognitionElement removeElement(String id);

	void addElementFactory(GeocognitionElementFactory factory);

	GeocognitionElement getRoot();

	/**
	 * Gets the geocognition element with the specified id
	 *
	 * @param id
	 * @return
	 */
	GeocognitionElement getGeocognitionElement(String id);

	/**
	 * Adds a listener of the events in geocognition
	 *
	 * @param listener
	 */
	void addGeocognitionListener(GeocognitionListener listener);

	/**
	 * Removes a listener of the events in geocognition
	 *
	 * @param listener
	 * @return
	 */
	boolean removeGeocognitionListener(GeocognitionListener listener);

	/**
	 * Adds a new folder in the Geocognition with the specified id. Folders will
	 * be added if the id specifies non existing ones
	 *
	 * @param id
	 *            unique id of the added element. It can't collide with any
	 *            existing id in the parent. If the id contain slashes they
	 *            indicate the full path of the element. Otherwise it is placed
	 *            in the root folder
	 * @throws IllegalArgumentException
	 *             If the id already exists or it contains a non folder element
	 *             in it
	 */
	void addFolder(String id) throws IllegalArgumentException;

	/**
	 * Gets a new unique id path with the specified prefix
	 *
	 * @param prefix
	 *            The prefix can have path separators
	 * @return
	 */
	String getUniqueIdPath(String prefix);

	/**
	 * Gets a new unique id. There are no element with the returned id in the
	 * geocognition
	 *
	 * @param prefix
	 *            The prefix cannot have path separators
	 * @return
	 */
	String getUniqueId(String prefix);

	/**
	 * Moves the specified element to the specified position
	 *
	 * @param elem
	 *            Id of the element to move
	 * @param newParent
	 *            Id of the element to move to
	 * @throws IllegalArgumentException
	 *             If almost one of the specified ids does not point to an
	 *             existing elements
	 * @throws UnsupportedOperationException
	 *             If the element cannot be moved to the destination
	 */
	void move(String id, String newParent) throws IllegalArgumentException,
			UnsupportedOperationException;

	/**
	 * Gets the elements that are accepted by the filter
	 *
	 * @param geocognitionFilter
	 * @return
	 */
	GeocognitionElement[] getElements(GeocognitionFilter geocognitionFilter);
}
