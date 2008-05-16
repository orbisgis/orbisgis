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
	 * @param evt
	 */
	void nameChanged(DocumentEvent evt);

	/**
	 * Called when the document is closed
	 *
	 * @param evt
	 */
	void documentClosing(DocumentEvent evt);
}
