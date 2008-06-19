package org.orbisgis.editors.map.actions;

import java.util.Arrays;

import org.gdms.data.DataSource;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.orbisgis.Services;
import org.orbisgis.editor.IEditor;
import org.orbisgis.editor.action.IEditorAction;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.views.documentCatalog.documents.MapDocument;

public class DeleteSelection implements IEditorAction {

	public void actionPerformed(IEditor editor) {
		MapDocument map = (MapDocument) editor.getDocument();
		ILayer activeLayer = map.getMapContext().getActiveLayer();
		int[] sel = activeLayer.getSelection().clone();
		Arrays.sort(sel);
		SpatialDataSourceDecorator dataSource = activeLayer.getDataSource();
		try {
			dataSource.setDispatchingMode(DataSource.STORE);
			for (int i = sel.length - 1; i >= 0; i--) {
				dataSource.deleteRow(sel[i]);
			}
			dataSource.setDispatchingMode(DataSource.DISPATCH);
		} catch (DriverException e) {
			Services.getErrorManager().error("Cannot delete selected features",
					e);
		}
	}

	public boolean isEnabled(IEditor editor) {
		MapDocument map = (MapDocument) editor.getDocument();
		ILayer activeLayer = map.getMapContext().getActiveLayer();
		return (activeLayer != null) && activeLayer.getSelection().length > 0;
	}

	public boolean isVisible(IEditor editor) {
		MapDocument map = (MapDocument) editor.getDocument();
		return map.getMapContext().getActiveLayer() != null;
	}

}
