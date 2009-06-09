package org.orbisgis.core.ui.actions;

import org.orbisgis.core.Services;
import org.orbisgis.core.edition.EditableElementException;
import org.orbisgis.core.ui.action.IAction;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.views.editor.EditorManager;
import org.orbisgis.errorManager.ErrorManager;
import org.orbisgis.pluginManager.background.BackgroundJob;
import org.orbisgis.pluginManager.background.BackgroundManager;
import org.orbisgis.progress.IProgressMonitor;

public class Save implements IAction {

	@Override
	public void actionPerformed() {
		final IEditor editor = getEditor();
		if (editor != null) {
			BackgroundManager mb = Services.getService(BackgroundManager.class);
			mb.backgroundOperation(new BackgroundJob() {

				@Override
				public void run(IProgressMonitor pm) {
					try {
						editor.getElement().save();
					} catch (EditableElementException e) {
						Services.getService(ErrorManager.class).error(
								"Problem saving", e);
					}
				}

				@Override
				public String getTaskName() {
					return "Saving...";
				}
			});
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
