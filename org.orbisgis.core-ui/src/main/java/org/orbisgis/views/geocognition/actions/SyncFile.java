package org.orbisgis.views.geocognition.actions;

import java.io.IOException;

import org.orbisgis.Services;
import org.orbisgis.geocognition.Geocognition;
import org.orbisgis.geocognition.GeocognitionElement;
import org.orbisgis.pluginManager.ui.OpenFilePanel;
import org.sif.UIFactory;

public class SyncFile extends AbstractSyncAction {

	@Override
	public void execute(Geocognition geocognition, GeocognitionElement[] elements) {
		OpenFilePanel fp = new OpenFilePanel(
				"org.orbisgis.geocognition.SyncFile",
				"Synchronize Geocognition");
		fp.addFilter("geocognition.xml", "Geocognition XML file");
		if (UIFactory.showDialog(fp)) {
			try {
				showSynchronizePanel(geocognition, elements, fp
						.getSelectedFile());
			} catch (IOException e) {
				Services.getErrorManager().error(
						"The file cannot be opened for reading", e);
			}
		}
	}
}