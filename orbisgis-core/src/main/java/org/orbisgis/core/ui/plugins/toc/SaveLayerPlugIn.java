package org.orbisgis.core.ui.plugins.toc;

import javax.swing.JOptionPane;

import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.core.images.IconNames;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;

public class SaveLayerPlugIn extends AbstractPlugIn{

	public boolean execute(PlugInContext context) throws Exception {		
		getPlugInContext().executeLayers();
		return true;
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame().getToc();
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { Names.POPUP_TOC_SAVE_PATH1 },
				Names.POPUP_TOC_INACTIVE_GROUP, false,
				getIcon(IconNames.SAVE), wbContext);
	}

	public boolean accepts(MapContext mc, ILayer layer) {
		SpatialDataSourceDecorator dataSource = layer.getDataSource();
		return (dataSource != null) && dataSource.isModified();
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return selectionCount == 1;
	}

	public void execute(MapContext mapContext, ILayer layer) {
		try {
			layer.getDataSource().commit();
		} catch (DriverException e) {
			Services.getErrorManager().error("Cannot save layer", e);
			return;
		} catch (NonEditableDataSourceException e) {
			Services.getErrorManager().error(
					"It is not possible to save "
							+ "this layer. Try to export "
							+ "it to another format", e);
			return;
		}
		JOptionPane.showMessageDialog(null, "The layer has been saved");
	}

	public boolean isVisible() {
		return getPlugInContext().checkLayerAvailability();
	}
	
}
