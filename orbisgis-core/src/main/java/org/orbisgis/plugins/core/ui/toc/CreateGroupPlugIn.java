package org.orbisgis.plugins.core.ui.toc;

import java.util.Observable;

import org.orbisgis.plugins.core.DataManager;
import org.orbisgis.plugins.core.Services;
import org.orbisgis.plugins.core.layerModel.ILayer;
import org.orbisgis.plugins.core.layerModel.LayerException;
import org.orbisgis.plugins.core.layerModel.MapContext;
import org.orbisgis.plugins.core.ui.AbstractPlugIn;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.editors.map.MapContextManager;
import org.orbisgis.plugins.core.ui.workbench.Names;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchContext;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchFrame;

public class CreateGroupPlugIn extends AbstractPlugIn {

	public boolean execute(PlugInContext context) throws Exception {
		getUpdateFactory().executeLayers();
		return true;
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame().getToc();
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { Names.POPUP_TOC_LAYERS_CREATE_PATH1 },
				Names.POPUP_TOC_LAYERS_CREATE_GROUP, false,
				getIcon(Names.POPUP_TOC_LAYERS_CREATE_ICON), wbContext);
	}

	public void update(Observable o, Object arg) {
	}

	public void execute(MapContext mapContext, ILayer resource) {
		MapContextManager vcm = (MapContextManager) Services
				.getService(MapContextManager.class);
		DataManager dataManager = (DataManager) Services
				.getService(DataManager.class);
		if (vcm.getActiveMapContext() != null) {
			ILayer newLayerCollection = dataManager
					.createLayerCollection("group" + System.currentTimeMillis());

			if ((resource == null) || (!resource.acceptsChilds())) {
				resource = vcm.getActiveMapContext().getLayerModel();
			}
			try {
				resource.addLayer(newLayerCollection);
			} catch (LayerException e) {
				throw new RuntimeException("bug!");
			}
		}
	}

	public boolean isEnabled() {
		return true;
	}

	public boolean isVisible() {
		return getUpdateFactory().checkLayerAvailability();
	}

	public boolean accepts(MapContext mc, ILayer layer) {
		return layer.acceptsChilds();
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		MapContextManager vcm = (MapContextManager) Services
				.getService(MapContextManager.class);
		return (vcm.getActiveMapContext() != null) && (selectionCount <= 1);
	}
}
