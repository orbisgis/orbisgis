package org.orbisgis.core.ui.plugins.views.geocognition;

import org.orbisgis.core.Services;
import org.orbisgis.core.background.BackgroundManager;
import org.orbisgis.core.geocognition.Geocognition;
import org.orbisgis.core.geocognition.GeocognitionElement;
import org.orbisgis.core.images.IconNames;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.plugins.views.editor.EditorManager;

public class OpenGeocognitionPlugIn extends AbstractPlugIn {

	public boolean execute(PlugInContext context) throws Exception {
		getPlugInContext().executeGeocognition();
		return true;
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame()
				.getGeocognition();
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { Names.POPUP_GEOCOGNITION_OPEN_PATH1 },
				Names.POPUP_GEOCOGNITION_OPEN_GROUP, false,
				getIcon(IconNames.POPUP_GEOCOGNITION_OPEN_ICON), wbContext);
	}

	public void execute(Geocognition geocognition, GeocognitionElement element) {
		BackgroundManager backgroundManager = (BackgroundManager) Services
				.getService(BackgroundManager.class);
		backgroundManager.backgroundOperation(new OpenGeocognitionElementJob(
				element));
	}

	public boolean isEnabled() {
		return true;
	}

	public boolean isVisible() {
		return getPlugInContext().geocognitionIsVisible();
	}

	public boolean accepts(Geocognition geocog, GeocognitionElement element) {
		EditorManager em = Services.getService(EditorManager.class);
		return em.hasEditor(element);
	}

	public boolean acceptsSelectionCount(Geocognition geocog, int selectionCount) {
		return selectionCount > 0;
	}

	@Override
	public boolean isSelected() {
		// TODO Auto-generated method stub
		return false;
	}
}
