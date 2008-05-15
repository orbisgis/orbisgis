package org.orbisgis.editors.map;

import org.orbisgis.Services;
import org.orbisgis.layerModel.MapContext;
import org.orbisgis.views.documentCatalog.IDocument;
import org.orbisgis.views.documentCatalog.documents.MapDocument;
import org.orbisgis.views.editor.EditorManager;

public class DefaultMapContextManager implements MapContextManager {

	public MapContext getActiveView() {
		IDocument doc = ((EditorManager) Services
				.getService("org.orbisgis.EditorManager")).getActiveDocument();
		if (doc instanceof MapDocument) {
			return ((MapDocument) doc).getMapContext();
		} else {
			return null;
		}
	}

}
