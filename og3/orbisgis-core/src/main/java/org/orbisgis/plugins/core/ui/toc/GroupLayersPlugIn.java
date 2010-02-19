package org.orbisgis.plugins.core.ui.toc;

import java.util.Observable;

import org.orbisgis.plugins.core.DataManager;
import org.orbisgis.plugins.core.Services;
import org.orbisgis.plugins.core.layerModel.ILayer;
import org.orbisgis.plugins.core.layerModel.LayerException;
import org.orbisgis.plugins.core.layerModel.MapContext;
import org.orbisgis.plugins.core.ui.AbstractPlugIn;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.workbench.Names;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchContext;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchFrame;

public class GroupLayersPlugIn extends AbstractPlugIn {

	public boolean execute(PlugInContext context) throws Exception {

		MapContext mapContext = context.getWorkbenchContext().getWorkbench()
				.getFrame().getToc().getMapContext();
		ILayer[] layers = mapContext.getSelectedLayers();

		DataManager dataManager = (DataManager) Services
				.getService(DataManager.class);
		ILayer col = dataManager.createLayerCollection("group"
				+ System.currentTimeMillis());
		ILayer parent = layers[0].getParent();
		try {
			parent.addLayer(col);
			for (ILayer layer : layers) {
				layer.moveTo(col);
			}
		} catch (LayerException e) {
			throw new RuntimeException("bug!");
		}

		return true;
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame().getToc();
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { Names.POPUP_TOC_LAYERS_GROUP_PATH1 },
				Names.POPUP_TOC_LAYERS_GROUP_GROUP, true,
				getIcon(Names.POPUP_TOC_LAYERS_GROUP_ICON), wbContext);
	}

	@Override
	public void update(Observable o, Object arg) {
	}

	public boolean isVisible() {
		return getUpdateFactory().IsMultipleLayer();
	}

	public boolean isEnabled() {
		return true;
	}

	public boolean acceptsAll(ILayer[] layer) {
		for (int i = 0; i < layer.length - 1; i++) {
			if (!layer[i].getParent().equals(layer[i + 1].getParent())) {
				return false;
			}
		}
		return layer.length > 0;
	}
}
