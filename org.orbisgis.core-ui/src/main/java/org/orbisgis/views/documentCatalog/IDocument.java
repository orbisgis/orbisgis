package org.orbisgis.views.documentCatalog;

import java.util.HashMap;

import javax.swing.Icon;

import org.orbisgis.progress.IProgressMonitor;

/**
 * Interface to implement by OrbisGIS documents. Implementations of this
 * interface need an empty constructor
 *
 * @author Fernando Gonzalez Cortes
 *
 */
public interface IDocument {

	/**
	 * Gets the number of children
	 *
	 * @return
	 */
	int getDocumentCount();

	/**
	 * Gets a specific children
	 *
	 * @param index
	 * @return
	 */
	IDocument getDocument(int index);

	/**
	 * Adds a child document to this document
	 *
	 * @param document
	 */
	void addDocument(IDocument document);

	/**
	 * Removes a child document from this document
	 *
	 * @param document
	 */
	void removeDocument(IDocument document);

	/**
	 * Gets the name of this view
	 *
	 * @return
	 */
	String getName();

	/**
	 * Sets the name of this view
	 *
	 * @param name
	 */
	void setName(String name);

	/**
	 * Gets the icon to show in the user interface
	 *
	 * @return
	 */
	Icon getIcon();

	/**
	 * Called when the document is opened
	 *
	 * @param pm
	 *            Instance to report the open status
	 *
	 * @throws DocumentException
	 *             If the document cannot be opened
	 */
	void openDocument(IProgressMonitor pm) throws DocumentException;

	/**
	 * Called when the document is closed
	 *
	 * @throws DocumentException
	 *             If the document cannot free the resources
	 */
	void closeDocument(IProgressMonitor pm) throws DocumentException;

	/**
	 * Called to save permanently the contents of this document. The tree
	 * structure is automatically saved so this method is about saving just the
	 * content of the document in the workspace
	 *
	 * @param pm
	 *            Instance to report the open status
	 *
	 * @throws DocumentException
	 *             If the document cannot be saved
	 */
	void saveDocument(IProgressMonitor pm) throws DocumentException;

	/**
	 * Returns a map of persistent properties that contain the data necessary to
	 * rebuild this object. Every implementation of this interface must provide
	 * an empty constructor
	 *
	 * @throws DocumentException
	 *             If there is a problem obtaining the properties
	 */
	HashMap<String, String> getPersistenceProperties() throws DocumentException;

	/**
	 * Sets the persistent properties that where returned in the
	 * getPeristenceProperties method
	 *
	 * @param properties
	 * @throws DocumentException
	 */
	void setPersistenceProperties(HashMap<String, String> properties)
			throws DocumentException;

	/**
	 * Returns true if this document can have children. False otherwise
	 *
	 * @return
	 */
	boolean allowsChildren();

	/**
	 * Adds a listener for document events
	 *
	 * @param listener
	 */
	void addDocumentListener(DocumentListener listener);

	/**
	 * Removes a listener for document events
	 *
	 * @param listener
	 */
	void removeDocumentListener(DocumentListener listener);
}
