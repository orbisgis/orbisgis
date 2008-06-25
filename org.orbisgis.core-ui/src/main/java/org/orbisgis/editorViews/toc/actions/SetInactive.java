package org.orbisgis.editorViews.toc.actions;

import javax.swing.JOptionPane;

import org.gdms.data.NonEditableDataSourceException;
import org.gdms.driver.DriverException;
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
		int option = JOptionPane.showConfirmDialog(null,
				"Do you want to save your changes", "Stop edition",
				JOptionPane.YES_NO_CANCEL_OPTION);
		if (option == JOptionPane.YES_OPTION) {
			try {
				mapContext.getActiveLayer().getDataSource().commit();
			} catch (DriverException e) {
				Services.getErrorManager().error("Cannot save layer", e);
			} catch (NonEditableDataSourceException e) {
				Services.getErrorManager().error("This layer cannot be saved",
						e);
			}
		}

		if (option != JOptionPane.CANCEL_OPTION) {
			mapContext.setActiveLayer(null);
		}
	}

}
