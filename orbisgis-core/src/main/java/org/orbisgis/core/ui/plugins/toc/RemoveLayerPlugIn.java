package org.orbisgis.core.ui.plugins.toc;

import java.util.Observable;

import org.orbisgis.core.Services;
import org.orbisgis.core.images.IconNames;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.PlugInContext.LayerSelectionTest;
import org.orbisgis.core.ui.pluginSystem.PlugInContext.LayerTest;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;

public class RemoveLayerPlugIn extends AbstractPlugIn {

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame().getToc();
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { Names.POPUP_TOC_LAYERS_REMOVE_PATH1 },
				Names.POPUP_TOC_LAYERS_REMOVE_GROUP, false,
				getIcon(IconNames.REMOVE), wbContext);
	}

	@Override
	public boolean execute(PlugInContext context) throws Exception {
		MapContext mapContext = context.getWorkbenchContext().getWorkbench()
				.getFrame().getToc().getMapContext();
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

	public void execute(MapContext mapContext, ILayer resource) {
		try {
			resource.getParent().remove(resource);
		} catch (LayerException e) {
			Services.getErrorManager().error(
					"Cannot delete layer: " + e.getMessage(), e);
		}
	}

	public boolean isVisible() {
		return getPlugInContext().checkLayerAvailability(
				new LayerSelectionTest[] {LayerSelectionTest.SUPERIOR},
				0,
				new LayerTest[] {LayerTest.LAYER_NOT_NULL}, 
				true);
	}

	@Override
	public boolean isSelected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}

}
