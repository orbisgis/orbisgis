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

public class OnlineHelpOrbisGISPlugIn extends AbstractPlugIn {

	private String url = "http://brehat.ec-nantes.fr/orbisgis_download/documentation/online/orbisgis_help.htm";
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
					java.awt.Desktop.getDesktop().browse(new URI(Names.ONLINE_URL));
				} catch (IOException e) {
					Services.getService(ErrorManager.class).error(
							"Serveur not available ", e);
				} catch (URISyntaxException e) {
					Services.getService(ErrorManager.class).error(
							"Bug syntaxe error ", e);
				}
			} else {
				Services.getService(ErrorManager.class).error(
						"This OS is not supported");
			}

		} else {
			Services.getService(ErrorManager.class).error(
					"This OS is not supported");
		}
		return true;
	}

	public boolean isEnabled() {
		menuItem.setEnabled(true);
		return true;
	}
}
