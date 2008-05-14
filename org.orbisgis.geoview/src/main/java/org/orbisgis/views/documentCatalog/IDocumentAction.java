package org.orbisgis.views.documentCatalog;

public interface IDocumentAction {

	/**
	 * Executes the action on the selected document
	 *
	 * @param document
	 *            the selected document. Null if there is no selected document
	 */
	void execute(DocumentCatalog catalog, IDocument document);

	/**
	 * Returns true if the action can be executed on the specified document
	 *
	 * @param documents
	 *            A selected document
	 * @return
	 */
	boolean accepts(DocumentCatalog catalog, IDocument document);

	/**
	 * Returns true if the action can be executed on the specified number of
	 * documents
	 *
	 * @param catalog
	 * @param count
	 *            The number of selected documents
	 * @return
	 */
	boolean acceptsSelectionCount(DocumentCatalog catalog, int count);

}
