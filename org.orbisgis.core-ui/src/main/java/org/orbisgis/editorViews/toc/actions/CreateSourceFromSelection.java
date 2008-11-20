package org.orbisgis.editorViews.toc.actions;

import org.gdms.driver.DriverException;
import org.orbisgis.editorViews.toc.action.ILayerAction;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.MapContext;

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
		return true;
	}

	@Override
	public void execute(MapContext mapContext, ILayer layer) {
		org.orbisgis.editors.table.editorActions.CreateSourceFromSelection
				.createSourceFromSelection(layer.getDataSource(), layer
						.getSelection());
	}

}
