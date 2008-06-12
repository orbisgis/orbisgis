package org.orbisgis.editors.map.actions;

import org.gdms.driver.DriverException;
import org.orbisgis.Services;
import org.orbisgis.editor.IEditor;
import org.orbisgis.editor.action.IEditorAction;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.views.documentCatalog.documents.MapDocument;

public class Undo implements IEditorAction {

	public void actionPerformed(IEditor editor) {
		MapDocument map = (MapDocument) editor.getDocument();
		ILayer activeLayer = map.getMapContext().getActiveLayer();
		try {
			activeLayer.getDataSource().undo();
		} catch (DriverException e) {
			Services.getErrorManager().error("Cannot undo", e);
		}
	}

	public boolean isEnabled(IEditor editor) {
		MapDocument map = (MapDocument) editor.getDocument();
		ILayer activeLayer = map.getMapContext().getActiveLayer();
		if (activeLayer != null) {
			return activeLayer.getDataSource().canUndo();
		} else {
			return false;
		}
	}

	public boolean isVisible(IEditor editor) {
		MapDocument map = (MapDocument) editor.getDocument();
		ILayer activeLayer = map.getMapContext().getActiveLayer();
		return activeLayer != null;
	}

}
