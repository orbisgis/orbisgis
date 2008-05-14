package org.orbisgis.views.documentCatalog.actions;

import org.apache.log4j.Logger;
import org.orbisgis.Services;
import org.orbisgis.editor.EditorDecorator;
import org.orbisgis.view.ViewManager;
import org.orbisgis.views.documentCatalog.DocumentCatalog;
import org.orbisgis.views.documentCatalog.IDocument;
import org.orbisgis.views.documentCatalog.DocumentException;
import org.orbisgis.views.documentCatalog.IDocumentAction;
import org.orbisgis.views.editor.EditorPanel;
import org.orbisgis.views.editor.EditorView;

public class Open implements IDocumentAction {

	private static final Logger logger = Logger.getLogger(Open.class);

	public boolean accepts(DocumentCatalog catalog, IDocument document) {
		return EPEditorHelper.getFirstEditor(document) != null;
	}

	public boolean acceptsSelectionCount(DocumentCatalog catalog, int count) {
		return count > 0;
	}

	public void execute(DocumentCatalog catalog, IDocument document) {
		EditorDecorator editor = EPEditorHelper.getFirstEditor(document);

		ViewManager vm = (ViewManager) Services
				.getService("org.orbisgis.ViewManager");
		EditorPanel ep = (EditorPanel) vm.getView(EditorView.getViewId());
		if (!ep.isBeingEdited(document, editor.getEditor().getClass())) {
			try {
				document.openDocument();
				editor.setDocument(document);
			} catch (DocumentException e) {
				logger.debug("Cannot open the document: " + document.getName(),
						e);
				editor = new EditorDecorator(new ErrorEditor(
						document.getName(), e.getMessage()), null, "");
			}
			ep.addEditor(editor);
		} else {
			ep.showEditor(document, editor.getEditor().getClass());
		}
	}

}
