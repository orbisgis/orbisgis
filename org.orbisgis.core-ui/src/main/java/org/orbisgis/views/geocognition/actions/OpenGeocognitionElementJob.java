/**
 * 
 */
package org.orbisgis.views.geocognition.actions;

import org.orbisgis.Services;
import org.orbisgis.edition.EditableElement;
import org.orbisgis.pluginManager.background.BackgroundJob;
import org.orbisgis.progress.IProgressMonitor;
import org.orbisgis.views.editor.EditorManager;

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