package org.orbisgis.editorViews.toc.actions;

import javax.swing.JOptionPane;

import org.gdms.data.FreeingResourcesException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.driver.DriverException;
import org.orbisgis.Services;
import org.orbisgis.editorViews.toc.action.ILayerAction;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.MapContext;

public class Save implements ILayerAction {

	public boolean accepts(ILayer layer) {
		return layer.getDataSource().isModified();
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return true;
	}

	public void execute(MapContext mapContext, ILayer layer) {
		try {
			layer.getDataSource().commit();
		} catch (DriverException e) {
			Services.getErrorManager().error("Cannot save layer", e);
			return;
		} catch (FreeingResourcesException e) {
		} catch (NonEditableDataSourceException e) {
			Services.getErrorManager().error(
					"It is not possible to save "
							+ "this layer. Try to export "
							+ "it to another format", e);
			return;
		}
		try {
			layer.getDataSource().open();
			JOptionPane.showMessageDialog(null, "The layer has been saved");
		} catch (DriverException e) {
			Services.getErrorManager().error("Cannot reopen saved layer", e);
		}
	}

}
