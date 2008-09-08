package org.orbisgis.views.geocognition.actions;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import org.orbisgis.PersistenceException;
import org.orbisgis.Services;
import org.orbisgis.geocognition.Geocognition;
import org.orbisgis.geocognition.GeocognitionElement;
import org.orbisgis.pluginManager.ui.OpenFilePanel;
import org.orbisgis.views.geocognition.action.IGeocognitionAction;
import org.sif.UIFactory;

public class Import implements IGeocognitionAction {

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
		OpenFilePanel sfp = new OpenFilePanel(
				"org.orbisgis.geocognition.Import", "Import Geocognition");
		sfp.addFilter("geocognition.xml", "Geocognition XML file");
		if (UIFactory.showDialog(sfp)) {
			try {
				BufferedInputStream is = new BufferedInputStream(
						new FileInputStream(sfp.getSelectedFile()));
				GeocognitionElement newElement = geocognition.createTree(is);
				is.close();
				if (element == null) {
					element = geocognition.getRoot();
				}
				String id = geocognition.getUniqueId(element.getIdPath()
						+ "/import");
				geocognition.addGeocognitionElement(id, newElement);
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
