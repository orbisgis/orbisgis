/**
 *
 */
package org.orbisgis.core.ui.views.geocognition.actions;

import org.orbisgis.core.Services;
import org.orbisgis.core.edition.EditableElement;
import org.orbisgis.core.ui.views.editor.EditorManager;
import org.orbisgis.pluginManager.background.BackgroundJob;
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