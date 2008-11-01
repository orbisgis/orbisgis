package org.orbisgis.views.geocognition.actions;

import org.orbisgis.Services;
import org.orbisgis.geocognition.Geocognition;
import org.orbisgis.geocognition.GeocognitionElement;
import org.orbisgis.geocognition.actions.GeocognitionActionElementFactory;
import org.orbisgis.views.geocognition.action.IGeocognitionAction;
import org.orbisgis.windows.mainFrame.UIManager;

public class UninstallAction implements IGeocognitionAction {

	@Override
	public boolean accepts(Geocognition geocog, GeocognitionElement element) {
		if (element.getTypeId().equals(
				GeocognitionActionElementFactory.ACTION_ID)) {
			UIManager ui = Services.getService(UIManager.class);
			return ui.getMenuName(element.getIdPath()) != null;
		}
		return false;
	}

	@Override
	public boolean acceptsSelectionCount(Geocognition geocog, int selectionCount) {
		return true;
	}

	@Override
	public void execute(Geocognition geocognition, GeocognitionElement element) {
		UIManager ui = Services.getService(UIManager.class);
		ui.uninstallMenu(element.getIdPath());
	}

}
