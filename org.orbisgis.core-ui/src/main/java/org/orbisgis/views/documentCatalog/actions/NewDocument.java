package org.orbisgis.views.documentCatalog.actions;

import org.orbisgis.views.documentCatalog.DocumentCatalog;
import org.orbisgis.views.documentCatalog.EPDocumentWizardHelper;
import org.orbisgis.views.documentCatalog.IDocument;
import org.orbisgis.views.documentCatalog.IDocumentAction;

public class NewDocument implements IDocumentAction {

	public boolean accepts(DocumentCatalog catalog, IDocument document) {
		return document.allowsChildren();
	}

	public boolean acceptsSelectionCount(DocumentCatalog catalog, int count) {
		return count <= 1;
	}

	public void execute(DocumentCatalog catalog, IDocument document) {
		EPDocumentWizardHelper wh = new EPDocumentWizardHelper();
		IDocument[] newDocuments = wh.openWizard();
		if (newDocuments != null) {
			if (document == null) {
				for (IDocument newDocument : newDocuments) {
					catalog.getDocumentRoot().addDocument(newDocument);
				}
			} else {
				for (IDocument newDocument : newDocuments) {
					document.addDocument(newDocument);
				}
			}
		}
		catalog.refresh();
	}

}
