package org.orbisgis.views.geocognition.actions;

import org.orbisgis.geocognition.Geocognition;
import org.orbisgis.geocognition.GeocognitionElement;
import org.orbisgis.views.geocognition.action.IGeocognitionAction;

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
