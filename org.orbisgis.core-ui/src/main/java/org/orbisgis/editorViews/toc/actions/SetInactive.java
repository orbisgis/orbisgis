package org.orbisgis.editorViews.toc.actions;

import org.orbisgis.Services;
import org.orbisgis.editorViews.toc.action.ILayerAction;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.MapContext;
import org.orbisgis.views.documentCatalog.documents.MapDocument;
import org.orbisgis.views.editor.EditorManager;

public class SetInactive implements ILayerAction {

	public boolean accepts(ILayer layer) {
		EditorManager em = (EditorManager) Services
				.getService("org.orbisgis.EditorManager");
		MapDocument md = (MapDocument) em.getActiveDocument();
		return md.getMapContext().getActiveLayer() == layer;
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return selectionCount == 1;
	}

	public void execute(MapContext mapContext, ILayer layer) {
		mapContext.setActiveLayer(null);
	}

}
