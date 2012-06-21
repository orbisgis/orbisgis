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
package org.orbisgis.core.ui.plugins.orbisgisFrame.help;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JMenuItem;

import org.orbisgis.core.Services;
import org.orbisgis.core.errorManager.ErrorManager;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.utils.I18N;

public class OnlineHelpOrbisGISPlugIn extends AbstractPlugIn {

	private String url = I18N.getString("orbisgis.org.orbisgis.ui.help.onlineHelpOrbisGISPlugIn.serverURL"); //$NON-NLS-1$
	private JMenuItem menuItem;

	public void initialize(PlugInContext context) throws Exception {
		menuItem = context.getFeatureInstaller().addMainMenuItem(this,
				new String[] { Names.HELP }, Names.ONLINE, false, null, null,
				null, context);
	}

	public boolean execute(PlugInContext context) throws Exception {
		if (Desktop.isDesktopSupported()) {
			if (Desktop.getDesktop()
					.isSupported(java.awt.Desktop.Action.BROWSE)) {
				try {
					java.awt.Desktop.getDesktop().browse(new URI(url));
				} catch (IOException e) {
					Services.getService(ErrorManager.class).error(
							I18N.getString("orbisgis.org.orbisgis.ui.help.onlineHelpOrbisGISPlugIn.serverNotAvailable"), e); //$NON-NLS-1$
				} catch (URISyntaxException e) {
					Services.getService(ErrorManager.class).error(
							I18N.getString("orbisgis.org.orbisgis.ui.help.onlineHelpOrbisGISPlugIn.bugSyntaxeError"), e); //$NON-NLS-1$
				}
			} else {
				Services.getService(ErrorManager.class).error(
						I18N.getString("orbisgis.org.orbisgis.ui.help.onlineHelpOrbisGISPlugIn.OsUnsupported")); //$NON-NLS-1$
			}

		} else {
			Services.getService(ErrorManager.class).error(
					I18N.getString("orbisgis.org.orbisgis.ui.help.onlineHelpOrbisGISPlugIn.OsUnsupported")); //$NON-NLS-1$
		}
		return true;
	}

	public boolean isEnabled() {
		menuItem.setEnabled(true);
		return true;
	}
}
