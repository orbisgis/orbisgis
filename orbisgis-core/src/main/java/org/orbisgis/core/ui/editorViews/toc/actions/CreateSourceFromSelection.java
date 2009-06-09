package org.orbisgis.core.ui.editorViews.toc.actions;

import org.gdms.driver.DriverException;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editorViews.toc.action.ILayerAction;

public class CreateSourceFromSelection implements ILayerAction {

	@Override
	public boolean accepts(MapContext mc, ILayer layer) {
		try {
			return layer.isVectorial() && layer.getSelection().length > 0;
		} catch (DriverException e) {
			return false;
		}
	}

	@Override
	public boolean acceptsSelectionCount(int selectionCount) {
		return selectionCount > 0;
	}

	@Override
	public void execute(MapContext mapContext, ILayer layer) {
		org.orbisgis.core.ui.editors.table.editorActions.CreateSourceFromSelection
				.createSourceFromSelection(layer.getDataSource(), layer
						.getSelection());
	}

}
