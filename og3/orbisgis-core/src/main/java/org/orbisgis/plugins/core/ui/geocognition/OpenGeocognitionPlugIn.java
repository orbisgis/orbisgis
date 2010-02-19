package org.orbisgis.plugins.core.ui.geocognition;

import java.util.Observable;

import org.orbisgis.plugins.core.Services;
import org.orbisgis.plugins.core.background.BackgroundManager;
import org.orbisgis.plugins.core.geocognition.Geocognition;
import org.orbisgis.plugins.core.geocognition.GeocognitionElement;
import org.orbisgis.plugins.core.ui.AbstractPlugIn;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.views.editor.EditorManager;
import org.orbisgis.plugins.core.ui.views.geocognition.OpenGeocognitionElementJob;
import org.orbisgis.plugins.core.ui.workbench.Names;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchContext;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchFrame;

public class OpenGeocognitionPlugIn extends AbstractPlugIn {

	public boolean execute(PlugInContext context) throws Exception {
		getUpdateFactory().executeGeocognition();
		return true;
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame()
				.getGeocognition();
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { Names.POPUP_GEOCOGNITION_OPEN_PATH1 },
				Names.POPUP_GEOCOGNITION_OPEN_GROUP, false,
				getIcon(Names.POPUP_GEOCOGNITION_OPEN_ICON), wbContext);
	}

	public void update(Observable o, Object arg) {
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
		return getUpdateFactory().geocognitionIsVIsible();
	}

	public boolean accepts(Geocognition geocog, GeocognitionElement element) {
		EditorManager em = Services.getService(EditorManager.class);
		return em.hasEditor(element);
	}

	public boolean acceptsSelectionCount(Geocognition geocog, int selectionCount) {
		return selectionCount > 0;
	}
}
