package org.orbisgis.views.documentCatalog;

public interface DocumentCatalogListener {

	/**
	 * Notifies that the specified document has been added to the specified
	 * parent
	 *
	 * @param parent
	 *            Parent document where the document has been added
	 * @param document
	 *            Added document
	 */
	void documentAdded(IDocument parent, IDocument document);

	/**
	 * Notifies that the specified document has been removed from the specified
	 * parent
	 *
	 * @param parent
	 *            Parent document the document has been removed from
	 * @param document
	 *            Removed document
	 */
	void documentRemoved(IDocument parent, IDocument document);

}
