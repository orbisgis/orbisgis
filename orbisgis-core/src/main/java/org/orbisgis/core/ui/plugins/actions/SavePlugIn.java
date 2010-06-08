package org.orbisgis.core.ui.plugins.actions;

import java.io.IOException;

import javax.swing.JButton;

import org.orbisgis.core.Services;
import org.orbisgis.core.background.BackgroundJob;
import org.orbisgis.core.background.BackgroundManager;
import org.orbisgis.core.images.IconNames;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.plugins.views.editor.EditorManager;
import org.orbisgis.core.workspace.Workspace;
import org.orbisgis.progress.IProgressMonitor;

public class SavePlugIn extends AbstractPlugIn {

	private JButton btn;

	public SavePlugIn() {
		btn = new JButton(getIcon(IconNames.SAVE_ICON));
		btn.setToolTipText(Names.SAVE);
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbcontext = context.getWorkbenchContext();
		wbcontext.getWorkbench().getFrame().getMainToolBar().addPlugIn(this,
				btn, context);

	}

	public boolean execute(PlugInContext context) throws Exception {

		BackgroundManager mb = Services.getService(BackgroundManager.class);
		mb.backgroundOperation(new BackgroundJob() {
			Workspace ws = (Workspace) Services.getService(Workspace.class);

			@Override
			public void run(IProgressMonitor pm) {
				try {
					ws.saveWorkspace();
				} catch (IOException e) {
					Services.getErrorManager()
							.error("Cannot save workspace", e);
				}
			}

			@Override
			public String getTaskName() {
				return "Saving Workspace";
			}
		});

		return true;
	}

	private IEditor getEditor() {
		EditorManager em = Services.getService(EditorManager.class);
		IEditor editor = em.getActiveEditor();
		return editor;
	}
	
	public boolean isEnabled() {
		boolean isEnabled = false;
		IEditor editor = getEditor();
		isEnabled = editor != null && editor.getElement().isModified();
		btn.setEnabled(isEnabled);
		return isEnabled;
	}
}
