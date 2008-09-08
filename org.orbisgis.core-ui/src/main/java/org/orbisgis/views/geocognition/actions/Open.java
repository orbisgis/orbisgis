package org.orbisgis.views.geocognition.actions;

import org.orbisgis.Services;
import org.orbisgis.geocognition.Geocognition;
import org.orbisgis.geocognition.GeocognitionElement;
import org.orbisgis.pluginManager.background.BackgroundManager;
import org.orbisgis.views.editor.EditorManager;
import org.orbisgis.views.geocognition.action.IGeocognitionAction;

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
				.getService("org.orbisgis.BackgroundManager");
		backgroundManager.backgroundOperation(new OpenGeocognitionElementJob(element));
	}

}
