package org.orbisgis.editorViews.toc.actions;

import javax.swing.JOptionPane;

import org.gdms.driver.DriverException;
import org.orbisgis.Services;
import org.orbisgis.editorViews.toc.action.ILayerAction;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.LayerException;
import org.orbisgis.layerModel.MapContext;

public class Revert implements ILayerAction {

	public boolean accepts(ILayer layer) {
		return layer.getDataSource().isModified();
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return true;
	}

	public void execute(MapContext mapContext, ILayer layer) {
		try {
			layer.getDataSource().cancel();
		} catch (DriverException e) {
			Services.getErrorManager().error("Cannot revert layer", e);
			return;
		}
		try {
			layer.getDataSource().open();
		} catch (DriverException e) {
			Services.getErrorManager().error(
					"Cannot reopen layer. Layer is removed", e);
			try {
				layer.getParent().remove(layer);
				JOptionPane.showMessageDialog(null, "The layer has been reverted");
			} catch (LayerException e1) {
				Services.getErrorManager().error("Cannot remove layer", e);
			}
		}
	}

}
