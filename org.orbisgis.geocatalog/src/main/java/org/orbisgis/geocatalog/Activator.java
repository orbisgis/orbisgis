/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 licence. It is produced  by the geomatic team of
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
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.geocatalog;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.gdms.source.Source;
import org.gdms.source.SourceManager;
import org.orbisgis.CollectionUtils;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.core.windows.EPWindowHelper;
import org.orbisgis.geocatalog.resources.AbstractGdmsSource;
import org.orbisgis.geocatalog.resources.IResource;
import org.orbisgis.geocatalog.resources.NodeFilter;
import org.orbisgis.pluginManager.PluginActivator;

public class Activator implements PluginActivator {

	private ArrayList<String> memoryResources;

	public boolean allowStop() {
		GeoCatalog geoCatalog = (GeoCatalog) EPWindowHelper
				.getWindows("org.orbisgis.geocatalog.Window")[0];
		Catalog catalog = geoCatalog.getCatalog();
		IResource[] res = catalog.getTreeModel().getNodes(new NodeFilter() {
			public boolean accept(IResource resource) {
				return true;
			}
		});

		memoryResources = new ArrayList<String>();
		SourceManager sm = OrbisgisCore.getDSF().getSourceManager();
		for (IResource resource : res) {
			if (resource.getResourceType() instanceof AbstractGdmsSource) {
				Source src = sm.getSource(resource.getName());
				if ((src.getType() & SourceManager.MEMORY) == SourceManager.MEMORY) {
					memoryResources.add(src.getName());
				}
			}
		}

		if (memoryResources.size() > 0) {
			String resourceList = CollectionUtils
					.getCommaSeparated(memoryResources.toArray(new String[0]));

			int exit = JOptionPane
					.showConfirmDialog(
							catalog,
							"The following resources are stored "
									+ "in memory and its content may be lost: \n"
									+ resourceList
									+ ".\nDo you want to exit"
									+ " and probably lose the content of those sources?",
							"Loose object resources?",
							JOptionPane.YES_NO_OPTION);

			return exit == JOptionPane.YES_OPTION;
		} else {
			return true;
		}
	}

	public void start() throws Exception {

	}

	public void stop() throws Exception {
	}

}
