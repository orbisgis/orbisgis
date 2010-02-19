package org.orbisgis.plugins.core.ui.toc;

import java.util.Observable;

import org.orbisgis.plugins.core.Services;
import org.orbisgis.plugins.core.layerModel.ILayer;
import org.orbisgis.plugins.core.layerModel.LayerException;
import org.orbisgis.plugins.core.layerModel.MapContext;
import org.orbisgis.plugins.core.ui.AbstractPlugIn;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.workbench.Names;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchContext;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchFrame;

public class RemoveLayerPlugIn extends AbstractPlugIn {

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame().getToc();
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { Names.POPUP_TOC_LAYERS_REMOVE_PATH1 },
				Names.POPUP_TOC_LAYERS_REMOVE_GROUP, true,
				getIcon(Names.POPUP_TOC_LAYERS_REMOVE_ICON), wbContext);
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

	@Override
	public void update(Observable o, Object arg) {
	}

	public void execute(MapContext mapContext, ILayer resource) {
		try {
			resource.getParent().remove(resource);
		} catch (LayerException e) {
			Services.getErrorManager().error(
					"Cannot delete layer: " + e.getMessage(), e);
		}
	}

	public boolean isEnabled() {
		return true;
	}

	public boolean isVisible() {
		return getUpdateFactory().checkLayerAvailability();
	}

	public boolean accepts(MapContext mc, ILayer layer) {
		return layer != null;
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return selectionCount > 0;
	}

}
