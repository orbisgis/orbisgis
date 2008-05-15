package org.orbisgis.views.documentCatalog;

/**
 * Listener for document events
 *
 * @author Fernando Gonzalez Cortes
 */
public interface DocumentListener {

	/**
	 * Called when the name of the document is changed
	 *
	 * @param newName
	 */
	void nameChanged(DocumentEvent newName);

}
