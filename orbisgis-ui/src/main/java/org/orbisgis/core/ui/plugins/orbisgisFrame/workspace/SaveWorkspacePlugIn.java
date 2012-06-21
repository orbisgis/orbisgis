/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.core.ui.plugins.orbisgisFrame.workspace;

import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JMenuItem;

import org.orbisgis.core.Services;
import org.orbisgis.core.background.BackgroundJob;
import org.orbisgis.core.background.BackgroundManager;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.plugins.views.editor.EditorManager;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;
import org.orbisgis.core.workspace.Workspace;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.utils.I18N;

public class SaveWorkspacePlugIn extends AbstractPlugIn {

	private JMenuItem menuItem;
	private JButton btn;
	
	
	public SaveWorkspacePlugIn() {
		btn = new JButton(OrbisGISIcon.SAVE_ICON);
		btn.setToolTipText(Names.SAVE);
	}

	public boolean execute(PlugInContext context) throws Exception {
		BackgroundManager mb = Services.getService(BackgroundManager.class);
		mb.backgroundOperation(new BackgroundJob() {
			Workspace ws = (Workspace) Services.getService(Workspace.class);

			@Override
			public void run(ProgressMonitor pm) {
				try {
					ws.saveWorkspace();
				} catch (IOException e) {
					Services.getErrorManager()
							.error(I18N.getString("orbisgis.org.orbisgis.ui.saveWorkspacePlugIn.cannotSaveWorkspace"), e); //$NON-NLS-1$
				}
			}

			@Override
			public String getTaskName() {
				return I18N.getString("orbisgis.org.orbisgis.ui.saveWorkspacePlugIn.savingWorkspace"); //$NON-NLS-1$
			}
		});
		return true;
	}

	public void initialize(PlugInContext context) throws Exception {
		menuItem = context.getFeatureInstaller().addMainMenuItem(this,
				new String[] { Names.FILE }, Names.SAVE_WS, false,
				OrbisGISIcon.SAVE, null, null, context);
		WorkbenchContext wbcontext = context.getWorkbenchContext();
		wbcontext.getWorkbench().getFrame().getMainToolBar().addPlugIn(this,
				btn, context);
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
		menuItem.setEnabled(isEnabled);
		return isEnabled;
	}
}
