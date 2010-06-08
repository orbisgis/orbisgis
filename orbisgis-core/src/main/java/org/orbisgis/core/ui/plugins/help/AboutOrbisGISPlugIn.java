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
 * Copyright (C) 2009 Erwan BOCHER, Pierre-yves FADET
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
 *    Pierre-Yves.Fadet_at_ec-nantes.fr
 *    thomas.leduc _at_ cerma.archi.fr
 */

package org.orbisgis.core.ui.plugins.help;

import javax.swing.JMenuItem;

import org.orbisgis.core.ApplicationInfo;
import org.orbisgis.core.Services;
import org.orbisgis.core.sif.SIFDialog;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;

public class AboutOrbisGISPlugIn extends AbstractPlugIn {

	private JMenuItem menuItem;

	public void initialize(PlugInContext context) throws Exception {
		menuItem = context.getFeatureInstaller().addMainMenuItem(this,
				new String[] { Names.HELP }, Names.ABOUT, false, null, null,
				null, context);
	}

	public boolean execute(PlugInContext context) throws Exception {
		final SIFDialog sifDialog = UIFactory.getSimpleDialog(new HtmlViewer(
				getClass().getResource("about.html")));
		sifDialog.setSize(650, 600);
		ApplicationInfo ai = (ApplicationInfo) Services
				.getService(ApplicationInfo.class);
		sifDialog.setTitle(ai.getName() + " " + ai.getVersionNumber() + "("
				+ ai.getVersionName() + ")" + " - " + ai.getOrganization());
		sifDialog.setVisible(true);
		return true;
	}

	public boolean isEnabled() {
		menuItem.setEnabled(true);
		return true;
	}
}
