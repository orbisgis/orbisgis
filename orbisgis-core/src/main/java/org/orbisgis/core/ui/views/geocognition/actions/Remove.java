package org.orbisgis.core.ui.views.geocognition.actions;

import org.orbisgis.core.geocognition.Geocognition;
import org.orbisgis.core.geocognition.GeocognitionElement;
import org.orbisgis.core.ui.views.geocognition.action.IGeocognitionAction;

public class Remove implements IGeocognitionAction {

	@Override
	public boolean accepts(Geocognition geocog, GeocognitionElement element) {
		return true;
	}

	@Override
	public boolean acceptsSelectionCount(Geocognition geocog, int selectionCount) {
		return selectionCount > 0;
	}

	@Override
	public void execute(Geocognition geocognition, GeocognitionElement element) {
		geocognition.removeElement(element.getIdPath());
	}
}
