package org.orbisgis.views.documentCatalog.actions;

import org.orbisgis.Services;
import org.orbisgis.pluginManager.background.BackgroundJob;
import org.orbisgis.pluginManager.background.BackgroundManager;
import org.orbisgis.progress.IProgressMonitor;
import org.orbisgis.views.documentCatalog.DocumentCatalog;
import org.orbisgis.views.documentCatalog.IDocument;
import org.orbisgis.views.documentCatalog.IDocumentAction;

public class Open implements IDocumentAction {

	public boolean accepts(DocumentCatalog catalog, IDocument document) {
		return EPEditorHelper.getFirstEditor(document) != null;
	}

	public boolean acceptsSelectionCount(DocumentCatalog catalog, int count) {
		return count > 0;
	}

	public void execute(DocumentCatalog catalog, IDocument document) {
		BackgroundManager backgroundManager = (BackgroundManager) Services
				.getService("org.orbisgis.BackgroundManager");
		backgroundManager
				.backgroundOperation(new OpenJob(catalog, document));
	}

	private class OpenJob implements BackgroundJob {

		private IDocument document;
		private DocumentCatalog catalog;

		public OpenJob(DocumentCatalog catalog, IDocument document) {
			this.catalog = catalog;
			this.document = document;
		}

		public String getTaskName() {
			return "Opening " + document.getName();
		}

		public void run(IProgressMonitor pm) {
			catalog.openDocument(document);
		}

	}

}
