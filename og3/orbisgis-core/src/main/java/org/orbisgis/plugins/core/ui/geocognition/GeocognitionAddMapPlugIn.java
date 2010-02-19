package org.orbisgis.plugins.core.ui.geocognition;

import java.util.Observable;

import org.orbisgis.plugins.core.geocognition.Geocognition;
import org.orbisgis.plugins.core.geocognition.GeocognitionElement;
import org.orbisgis.plugins.core.ui.AbstractPlugIn;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.views.geocognition.wizards.NewMap;
import org.orbisgis.plugins.core.ui.workbench.Names;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchContext;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchFrame;

public class GeocognitionAddMapPlugIn extends AbstractPlugIn {

	public boolean execute(PlugInContext context) throws Exception {
		getUpdateFactory().executeGeocognitionElement(new NewMap());
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
				getIcon(Names.POPUP_GEOCOGNITION_ADD_MAP_ICON), wbContext);
	}

	public void update(Observable o, Object arg) {
	}

	public boolean isEnabled() {
		return true;
	}

	public boolean isVisible() {
		return getUpdateFactory().geocognitionIsVIsible();
	}

	public boolean accepts(Geocognition geocog, GeocognitionElement element) {
		return element.isFolder();
	}

	public boolean acceptsSelectionCount(Geocognition geocog, int selectionCount) {
		return selectionCount <= 1;
	}
}
