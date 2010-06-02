package org.orbisgis.core.ui.plugins.views.geocognition;

import org.orbisgis.core.images.IconNames;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.PlugInContext.ElementAvailability;
import org.orbisgis.core.ui.pluginSystem.PlugInContext.SelectionAvailability;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.plugins.views.geocognition.wizards.NewMap;

public class GeocognitionAddMapPlugIn extends AbstractPlugIn {

	public boolean execute(PlugInContext context) throws Exception {
		getPlugInContext().executeGeocognitionElement(new NewMap());
		return true;
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame()
				.getGeocognition();
		context.getFeatureInstaller().addPopupMenuItem(
				frame,
				this,
				new String[] { Names.POPUP_GEOCOGNITION_ADD,
						Names.POPUP_GEOCOGNITION_ADD_MAP_PATH1 },
				Names.POPUP_GEOCOGNITION_ADD_MAP_GROUP, false,
				getIcon(IconNames.MAP), wbContext);
	}

	public boolean isEnabled() {
		return getPlugInContext().checkLayerAvailability(
				new SelectionAvailability[] {SelectionAvailability.INFERIOR_EQUAL},
				1,
				new ElementAvailability[] {ElementAvailability.FOLDER});	
	}
	
	public boolean isSelected() {
		// TODO Auto-generated method stub
		return false;
	}
}
