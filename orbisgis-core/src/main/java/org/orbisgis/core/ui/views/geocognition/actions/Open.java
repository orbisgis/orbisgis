package org.orbisgis.core.ui.views.geocognition.actions;

import org.orbisgis.core.Services;
import org.orbisgis.core.geocognition.Geocognition;
import org.orbisgis.core.geocognition.GeocognitionElement;
import org.orbisgis.core.ui.views.editor.EditorManager;
import org.orbisgis.core.ui.views.geocognition.action.IGeocognitionAction;
import org.orbisgis.pluginManager.background.BackgroundManager;

public class Open implements IGeocognitionAction {

	@Override
	public boolean accepts(Geocognition geocog, GeocognitionElement element) {
		EditorManager em = Services.getService(EditorManager.class);
		return em.hasEditor(element);
	}

	@Override
	public boolean acceptsSelectionCount(Geocognition geocog, int selectionCount) {
		return selectionCount > 0;
	}

	@Override
	public void execute(Geocognition geocognition, GeocognitionElement element) {
		BackgroundManager backgroundManager = (BackgroundManager) Services
				.getService(BackgroundManager.class);
		backgroundManager.backgroundOperation(new OpenGeocognitionElementJob(element));
	}

}
