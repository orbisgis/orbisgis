package org.orbisgis.actions;

import org.orbisgis.Services;
import org.orbisgis.action.IAction;
import org.orbisgis.editor.IEditor;
import org.orbisgis.errorManager.ErrorManager;
import org.orbisgis.geocognition.mapContext.GeocognitionException;
import org.orbisgis.views.editor.EditorManager;

public class Save implements IAction {

	@Override
	public void actionPerformed() {
		IEditor editor = getEditor();
		if (editor != null) {
			try {
				editor.getElement().save();
			} catch (GeocognitionException e) {
				Services.getService(ErrorManager.class).error(
						"Problem saving", e);
			}

		}
	}

	private IEditor getEditor() {
		EditorManager em = Services.getService(EditorManager.class);
		IEditor editor = em.getActiveEditor();
		return editor;
	}

	@Override
	public boolean isEnabled() {
		IEditor editor = getEditor();
		return editor != null && editor.getElement().isModified();
	}

	@Override
	public boolean isVisible() {
		return true;
	}

}
