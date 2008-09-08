package org.orbisgis.views.geocognition.actions;

import org.orbisgis.geocognition.Geocognition;
import org.orbisgis.geocognition.GeocognitionElement;
import org.orbisgis.views.geocognition.action.IGeocognitionAction;
import org.orbisgis.views.geocognition.wizard.EPGeocognitionWizardHelper;
import org.orbisgis.views.geocognition.wizard.NewGeocognitionObject;

public class NewElement implements IGeocognitionAction {

	@Override
	public boolean accepts(Geocognition geocog, GeocognitionElement element) {
		return element.isFolder();
	}

	@Override
	public boolean acceptsSelectionCount(Geocognition geocog, int selectionCount) {
		return selectionCount <= 1;
	}

	@Override
	public void execute(Geocognition geocognition, GeocognitionElement element) {
		EPGeocognitionWizardHelper wh = new EPGeocognitionWizardHelper();
		NewGeocognitionObject[] newElements = wh.openWizard();
		if (newElements != null) {
			if (element == null) {
				wh.addElements(newElements, "");
			} else {
				wh.addElements(newElements, element.getIdPath());
			}
		}
	}

}
