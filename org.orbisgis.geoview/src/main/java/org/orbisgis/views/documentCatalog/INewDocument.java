package org.orbisgis.views.documentCatalog;

import org.orbisgis.wizards.IWizard;

public interface INewDocument extends IWizard {

	/**
	 * Gets the document created by this wizard. Null if no document should be
	 * added
	 *
	 * @return
	 */
	IDocument getDocument();

}
