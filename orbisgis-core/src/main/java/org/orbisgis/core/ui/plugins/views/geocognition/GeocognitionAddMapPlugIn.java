package org.orbisgis.core.ui.plugins.views.geocognition;

import java.util.Observable;

import org.orbisgis.core.geocognition.Geocognition;
import org.orbisgis.core.geocognition.GeocognitionElement;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
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
				getIcon(null), wbContext);
	}

	public void update(Observable o, Object arg) {
	}

	public boolean isEnabled() {
		return true;
	}

	public boolean isVisible() {
		return getPlugInContext().geocognitionIsVisible();
	}

	public boolean accepts(Geocognition geocog, GeocognitionElement element) {
		return element.isFolder();
	}

	public boolean acceptsSelectionCount(Geocognition geocog, int selectionCount) {
		return selectionCount <= 1;
	}
}
