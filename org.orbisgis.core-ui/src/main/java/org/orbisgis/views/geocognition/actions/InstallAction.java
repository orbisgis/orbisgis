package org.orbisgis.views.geocognition.actions;

import org.orbisgis.geocognition.Geocognition;
import org.orbisgis.geocognition.GeocognitionElement;
import org.orbisgis.geocognition.actions.GeocognitionActionElementFactory;
import org.orbisgis.views.geocognition.action.IGeocognitionAction;

public class InstallAction implements IGeocognitionAction {

	@Override
	public boolean accepts(Geocognition geocog, GeocognitionElement element) {
		return element.getTypeId().equals(GeocognitionActionElementFactory.ACTION_ID);
	}

	@Override
	public boolean acceptsSelectionCount(Geocognition geocog, int selectionCount) {
		return true;
	}

	@Override
	public void execute(Geocognition geocognition, GeocognitionElement element) {
	}

}
