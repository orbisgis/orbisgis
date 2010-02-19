/**
 *
 */
package org.orbisgis.plugins.core.ui.views.geocognition;

import org.orbisgis.plugins.core.Services;
import org.orbisgis.plugins.core.background.BackgroundJob;
import org.orbisgis.plugins.core.edition.EditableElement;
import org.orbisgis.plugins.core.ui.views.editor.EditorManager;
import org.orbisgis.progress.IProgressMonitor;

public class OpenGeocognitionElementJob implements BackgroundJob {

	private EditableElement[] elements;

	public OpenGeocognitionElementJob(EditableElement... elements) {
		this.elements = elements;
	}

	public String getTaskName() {
		return "Opening " + elements[0].getId();
	}

	public void run(IProgressMonitor pm) {
		EditorManager em = Services.getService(EditorManager.class);
		for (EditableElement element : elements) {
			em.open(element);
		}
	}

}