package org.orbisgis.views.documentCatalog.actions;

import org.orbisgis.views.documentCatalog.DocumentCatalog;
import org.orbisgis.views.documentCatalog.IDocument;
import org.orbisgis.views.documentCatalog.IDocumentAction;

public class DeleteDocument implements IDocumentAction {

	public boolean accepts(DocumentCatalog catalog, IDocument document) {
		return true;
	}

	public boolean acceptsSelectionCount(DocumentCatalog catalog, int count) {
		return count > 0;
	}

	public void execute(DocumentCatalog catalog, IDocument document) {
		catalog.removeDocument(document);
	}

}
