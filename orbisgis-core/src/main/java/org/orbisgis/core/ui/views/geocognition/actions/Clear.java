package org.orbisgis.core.ui.views.geocognition.actions;

import org.orbisgis.core.geocognition.Geocognition;
import org.orbisgis.core.geocognition.GeocognitionElement;
import org.orbisgis.core.ui.views.geocognition.action.IGeocognitionAction;

public class Clear implements IGeocognitionAction {

	@Override
	public boolean accepts(Geocognition geocog, GeocognitionElement element) {
		return (element == null) || element.isFolder();
	}

	@Override
	public boolean acceptsSelectionCount(Geocognition geocog, int selectionCount) {
		return selectionCount <= 1;
	}

	@Override
	public void execute(Geocognition geocognition, GeocognitionElement element) {
		if (element == null) {
			geocognition.clear();
		} else {
			for (int i = element.getElementCount() - 1; i >= 0; i--) {
				geocognition.removeElement(element.getElement(i).getId());
			}
		}
	}
}
