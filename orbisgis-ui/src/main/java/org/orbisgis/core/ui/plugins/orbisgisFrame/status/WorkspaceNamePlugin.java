/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.orbisgis.core.ui.plugins.orbisgisFrame.status;

import javax.swing.JLabel;

import org.orbisgis.core.Services;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.workspace.DefaultWorkspace;
import org.orbisgis.core.workspace.Workspace;
import org.orbisgis.utils.I18N;

public class WorkspaceNamePlugin extends AbstractPlugIn {

	private JLabel btn;

	public WorkspaceNamePlugin() {

		DefaultWorkspace workspace = (DefaultWorkspace) Services
				.getService(Workspace.class);
		StringBuffer message = new StringBuffer(I18N.getString("orbisgis.org.orbisgis.ui.workspaceNamePlugin.workspace")); //$NON-NLS-1$
		String currentWorkspaceName = workspace.getWorkspaceFolder();
		if (currentWorkspaceName.length() > 60) {
			currentWorkspaceName.substring(60);
			currentWorkspaceName = currentWorkspaceName + "..."; //$NON-NLS-1$
		}
		message.append(currentWorkspaceName);
		btn = new JLabel(message.toString());
	}

	@Override
	public boolean execute(PlugInContext context) throws Exception {
		return false;
	}

	@Override
	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbcontext = context.getWorkbenchContext();
		wbcontext.getWorkbench().getFrame().getMainStatusToolBar().addPlugIn(
				this, btn, context);

	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
