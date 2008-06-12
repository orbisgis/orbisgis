package org.orbisgis.editors.map.actions;

import org.gdms.driver.DriverException;
import org.orbisgis.Services;
import org.orbisgis.editor.IEditor;
import org.orbisgis.editor.action.IEditorAction;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.views.documentCatalog.documents.MapDocument;

public class Redo implements IEditorAction {

	public void actionPerformed(IEditor editor) {
		MapDocument map = (MapDocument) editor.getDocument();
		ILayer activeLayer = map.getMapContext().getActiveLayer();
		try {
			activeLayer.getDataSource().redo();
		} catch (DriverException e) {
			Services.getErrorManager().error("Cannot redo", e);
		}
	}

	public boolean isEnabled(IEditor editor) {
		MapDocument map = (MapDocument) editor.getDocument();
		ILayer activeLayer = map.getMapContext().getActiveLayer();
		if (activeLayer != null) {
			return activeLayer.getDataSource().canRedo();
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
