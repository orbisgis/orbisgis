package org.orbisgis.core.ui.plugins.toc;

import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.core.images.IconNames;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.PlugInContext.SelectionAvailability;
import org.orbisgis.core.ui.pluginSystem.PlugInContext.LayerAvailability;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;

public class RevertLayerPlugIn extends AbstractPlugIn {

	public boolean execute(PlugInContext context) throws Exception {		
		MapContext mapContext = getPlugInContext().getMapContext();
		ILayer[] selectedResources = mapContext.getSelectedLayers();

		if (selectedResources.length == 0) {
			execute(mapContext, null);
		} else {
			for (ILayer resource : selectedResources) {
				execute(mapContext, resource);
			}
		}
		return true;
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame().getToc();
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { Names.POPUP_TOC_REVERT_PATH1 },
				Names.POPUP_TOC_INACTIVE_GROUP, false,
				getIcon(IconNames.POPUP_TOC_REVERT_ICON), wbContext);
	}

	public void execute(MapContext mapContext, ILayer layer) {
		try {
			layer.getDataSource().syncWithSource();
		} catch (DriverException e) {
			Services.getErrorManager().error("Cannot revert layer", e);
			return;
		}
	}

	public boolean isEnabled() {
		return getPlugInContext().checkLayerAvailability(
				new SelectionAvailability[] {SelectionAvailability.EQUAL},
				1,
				new LayerAvailability[] {LayerAvailability.IS_MODIFIED});
	}
	
	public boolean isSelected() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
