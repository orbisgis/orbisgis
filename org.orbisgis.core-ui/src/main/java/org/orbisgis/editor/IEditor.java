package org.orbisgis.editor;

import org.orbisgis.view.IView;
import org.orbisgis.views.documentCatalog.IDocument;

public interface IEditor extends IView {

	/**
	 * Sets the document to edit. This method is called just once in the
	 * lifecycle of an editor
	 * 
	 * @param doc
	 */
	void setDocument(IDocument doc);

	/**
	 * Returns true if this editor can edit the specified document
	 * 
	 * @param doc
	 * @return
	 */
	boolean acceptDocument(IDocument doc);

	/**
	 * Gets the title of the editor. Typically related to the name of the
	 * document
	 * 
	 * @return
	 */
	String getTitle();

	/**
	 * Gets the document of this editor
	 * 
	 * @return
	 */
	IDocument getDocument();

}
