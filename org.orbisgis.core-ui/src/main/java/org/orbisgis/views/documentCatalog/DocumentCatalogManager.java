package org.orbisgis.views.documentCatalog;


/**
 * Service to manage the documents in the application
 *
 * @author Fernando Gonzalez Cortes
 */
public interface DocumentCatalogManager {

	/**
	 * Adds the document to the catalog and opens it in the editor by default
	 *
	 * @param document
	 */
	public void addDocument(IDocument document);

	/**
	 * Adds the specified document to the specified existing document in the
	 * catalog and opens it in the editor by default
	 *
	 * @param parent
	 *            Document where the document will be added
	 * @param document
	 *            Document to add
	 */
	public void addDocument(IDocument parent, IDocument document);

	/**
	 * Returns true if the document catalog is empty, false otherwise
	 *
	 * @return
	 */
	public boolean isEmpty();

	/**
	 * Opens the document using the default editor
	 *
	 * @param mapDocument
	 */
	public void openDocument(IDocument document);

}
