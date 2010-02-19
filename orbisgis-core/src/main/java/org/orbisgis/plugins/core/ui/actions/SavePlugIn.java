package org.orbisgis.plugins.core.ui.actions;

import java.util.Observable;

import javax.swing.JButton;
import javax.swing.JMenuItem;

import org.orbisgis.plugins.core.Services;
import org.orbisgis.plugins.core.background.BackgroundJob;
import org.orbisgis.plugins.core.background.BackgroundManager;
import org.orbisgis.plugins.core.edition.EditableElementException;
import org.orbisgis.plugins.core.ui.AbstractPlugIn;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.editor.IEditor;
import org.orbisgis.plugins.core.ui.views.editor.EditorManager;
import org.orbisgis.plugins.core.ui.workbench.Names;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchContext;
import org.orbisgis.plugins.errorManager.ErrorManager;
import org.orbisgis.progress.IProgressMonitor;

public class SavePlugIn extends AbstractPlugIn {

	private JButton btn;
	private JMenuItem menuItem;

	public SavePlugIn() {
		btn = new JButton(getIcon(Names.SAVE_ICON));
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbcontext = context.getWorkbenchContext();
		wbcontext.getWorkbench().getFrame().getMainToolBar().addPlugIn(this,
				btn);

		menuItem = context.getFeatureInstaller().addMainMenuItem(this,
				new String[] { Names.FILE }, Names.SAVE, false,
				getIcon(Names.SAVE_ICON), null, null, null, null,wbcontext);

	}

	public boolean execute(PlugInContext context) throws Exception {

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
		return true;
	}

	private IEditor getEditor() {
		EditorManager em = Services.getService(EditorManager.class);
		IEditor editor = em.getActiveEditor();
		return editor;
	}

	@Override
	public void update(Observable o, Object arg) {
		btn.setEnabled(isEnabled());
		btn.setVisible(isVisible());

		menuItem.setEnabled(isEnabled());
		menuItem.setVisible(isVisible());

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
