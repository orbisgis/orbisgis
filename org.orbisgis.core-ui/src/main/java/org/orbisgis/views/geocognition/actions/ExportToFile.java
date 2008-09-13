package org.orbisgis.views.geocognition.actions;

import java.io.IOException;

import org.orbisgis.Services;
import org.orbisgis.geocognition.Geocognition;
import org.orbisgis.geocognition.GeocognitionElement;
import org.orbisgis.pluginManager.ui.SaveFilePanel;
import org.orbisgis.views.geocognition.sync.SyncPanel;
import org.sif.UIFactory;

public class ExportToFile extends AbstractSyncAction {
	@Override
	public void execute(Geocognition geocognition,
			GeocognitionElement[] elements) {
		SaveFilePanel fp = new SaveFilePanel(
				"org.orbisgis.geocognition.SyncFile",
				"Synchronize Geocognition");
		fp.addFilter("geocognition.xml", "Geocognition XML file");
		if (UIFactory.showDialog(fp)) {
			try {
				showSynchronizePanel(geocognition, elements, fp
						.getSelectedFile(), SyncPanel.EXPORT);
			} catch (IOException e) {
				Services.getErrorManager().error(
						"The file cannot be opened for reading", e);
			}
		}
	}
}
