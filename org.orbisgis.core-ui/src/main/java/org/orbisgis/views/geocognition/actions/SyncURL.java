package org.orbisgis.views.geocognition.actions;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.orbisgis.Services;
import org.orbisgis.geocognition.Geocognition;
import org.orbisgis.geocognition.GeocognitionElement;
import org.orbisgis.ui.sif.AskValue;
import org.sif.UIFactory;

public class SyncURL extends AbstractSyncAction {
	@Override
	public void execute(Geocognition geocognition, GeocognitionElement[] elements) {
		AskValue av = new AskValue("Enter the URL", "txt is not null",
				"You must enter an URL");
		if (UIFactory.showDialog(av)) {
			try {
				URL url = new URL(av.getValue());
				showSynchronizePanel(geocognition, elements, url);
			} catch (MalformedURLException e) {
				Services.getErrorManager().error(
						"The given URL is not a valid one", e);
			} catch (IOException e) {
				Services.getErrorManager().error(
						"It was impossible to connect to the specified URL", e);
			}
		}
	}
}
