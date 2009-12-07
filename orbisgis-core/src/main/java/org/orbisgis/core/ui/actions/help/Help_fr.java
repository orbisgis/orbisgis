/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.core.ui.actions.help;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.orbisgis.core.Services;
import org.orbisgis.core.ui.action.IAction;
import org.orbisgis.errorManager.ErrorManager;

public class Help_fr implements IAction {

	private String url = "http://brehat.ec-nantes.fr/orbisgis_download/documentation/online/fr/orbisgis_help.htm";

	public void actionPerformed() {

		if (Desktop.isDesktopSupported()) {

			if (Desktop.getDesktop()
					.isSupported(java.awt.Desktop.Action.BROWSE)) {

				try {

					java.awt.Desktop.getDesktop().browse(new URI(url));

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
	}

	public boolean isEnabled() {
		return true;
	}

	public boolean isVisible() {
		return true;
	}
}