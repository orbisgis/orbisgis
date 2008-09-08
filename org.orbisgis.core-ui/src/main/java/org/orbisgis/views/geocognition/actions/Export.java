package org.orbisgis.views.geocognition.actions;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.orbisgis.PersistenceException;
import org.orbisgis.Services;
import org.orbisgis.geocognition.Geocognition;
import org.orbisgis.geocognition.GeocognitionElement;
import org.orbisgis.pluginManager.ui.SaveFilePanel;
import org.orbisgis.views.geocognition.action.IGeocognitionGroupAction;
import org.sif.UIFactory;

public class Export implements IGeocognitionGroupAction {

	@Override
	public boolean accepts(Geocognition geocog, GeocognitionElement[] element) {
		for (int i = 0; i < element.length; i++) {
			for (int j = 0; j < element.length; j++) {
				if (i == j) {
					continue;
				}
				if (element[i].getIdPath().startsWith(element[j].getIdPath())) {
					return false;
				}
			}
		}
		
		return true;
	}

	@Override
	public void execute(Geocognition geocognition,
			GeocognitionElement[] elements) {
		SaveFilePanel sfp = new SaveFilePanel(
				"org.orbisgis.geocognition.Export", "Export Geocognition");
		sfp.addFilter("geocognition.xml", "Geocognition XML file");
		if (UIFactory.showDialog(sfp)) {
			try {
				BufferedOutputStream os = new BufferedOutputStream(
						new FileOutputStream(sfp.getSelectedFile()));
				if (elements.length == 0) {
					elements = new GeocognitionElement[] { geocognition
							.getRoot() };
				}
				String[] ids = new String[elements.length];
				for (int i = 0; i < ids.length; i++) {
					ids[i] = elements[i].getIdPath();
				}
				geocognition.write(os, ids);

				os.close();
			} catch (IOException e) {
				Services.getErrorManager().error("Cannot export geocognition",
						e);
			} catch (IllegalArgumentException e) {
				Services.getErrorManager().error("bug!", e);
			} catch (PersistenceException e) {
				Services.getErrorManager().error("Cannot export geocognition",
						e);
			}
		}
	}

}
