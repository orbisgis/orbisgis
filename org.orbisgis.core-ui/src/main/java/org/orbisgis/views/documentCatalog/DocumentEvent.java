package org.orbisgis.views.documentCatalog;

public class DocumentEvent {

	private IDocument document;

	public DocumentEvent(IDocument document) {
		super();
		this.document = document;
	}

	public IDocument getDocument() {
		return document;
	}

}
