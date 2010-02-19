package org.orbisgis.plugins.core.ui.toc;

import java.util.Observable;

import org.gdms.driver.DriverException;
import org.orbisgis.plugins.core.layerModel.ILayer;
import org.orbisgis.plugins.core.layerModel.MapContext;
import org.orbisgis.plugins.core.ui.AbstractPlugIn;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.workbench.Names;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchContext;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchFrame;

public class SetActivePlugIn extends AbstractPlugIn {

	public boolean execute(PlugInContext context) throws Exception {
		getUpdateFactory().executeLayers();
		return true;
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame().getToc();
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { Names.POPUP_TOC_ACTIVE_PATH1 },
				Names.POPUP_TOC_ACTIVE_GROUP, false,
				getIcon(Names.POPUP_TOC_ACTIVE_ICON), wbContext);
	}

	public void update(Observable o, Object arg) {
	}

	public void execute(MapContext mapContext, ILayer layer) {
		mapContext.setActiveLayer(layer);
	}

	public boolean isEnabled() {
		return true;
	}

	public boolean isVisible() {
		return getUpdateFactory().checkLayerAvailability();
	}

	public boolean accepts(MapContext mc, ILayer layer) {
		try {
			return (mc.getActiveLayer() != layer) && layer.isVectorial()
					&& layer.getDataSource().isEditable();
		} catch (DriverException e) {
			return false;
		}
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return selectionCount == 1;
	}
}
