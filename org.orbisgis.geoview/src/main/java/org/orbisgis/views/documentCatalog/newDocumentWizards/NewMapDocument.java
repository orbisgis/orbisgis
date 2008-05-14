package org.orbisgis.views.documentCatalog.newDocumentWizards;

import org.orbisgis.ui.sif.AskValue;
import org.orbisgis.views.documentCatalog.IDocument;
import org.orbisgis.views.documentCatalog.INewDocument;
import org.orbisgis.views.documentCatalog.documents.MapDocument;
import org.sif.UIFactory;

public class NewMapDocument implements INewDocument {

	public IDocument getDocument() {
		AskValue askValue = new AskValue("Enter map name", "txt is not null",
				"A name must be specified");
		if (UIFactory.showDialog(askValue)) {
			MapDocument mapDocument = new MapDocument();
			mapDocument.setName(askValue.getValue());
			return mapDocument;
		} else {
			return null;
		}
	}

	public String getName() {
		return "Map";
	}

}
