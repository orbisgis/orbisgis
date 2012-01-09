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
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER,  Alexis GUEGANNO, Antoine GOURLAY, Adelin PIAU, Gwendall PETIT
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
 * info _at_ orbisgis.org
 */
package org.orbisgis.core.ui.pluginSystem.workbench;

import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.BeanShellConsoleViewPlugIn;
import org.orbisgis.core.ui.plugins.views.editor.EditorViewPlugIn;
import org.orbisgis.core.ui.plugins.views.geocatalog.GeoCatalogViewPlugIn;
import org.orbisgis.core.ui.plugins.views.geomark.GeomarkViewPlugIn;
import org.orbisgis.core.ui.plugins.views.information.InformationViewPlugIn;
import org.orbisgis.core.ui.plugins.views.memory.MemoryViewPlugIn;
import org.orbisgis.core.ui.plugins.views.output.OutputViewPlugIn;
import org.orbisgis.core.ui.plugins.views.sqlConsole.SQLConsoleViewPlugIn;
import org.orbisgis.core.ui.plugins.views.toc.TocViewPlugIn;

//all views plugins so orbisgis UI
public class OrbisGISConfiguration {

	public static void loadOrbisGISPlugIns(
			final WorkbenchContext workbenchContext) throws Exception {
		PlugInContext pluginContext = workbenchContext.createPlugInContext();

		EditorViewPlugIn editorViewPlugIn = new EditorViewPlugIn();
		editorViewPlugIn.initialize(pluginContext);

		TocViewPlugIn tocViewPlugIn = new TocViewPlugIn();
		tocViewPlugIn.initialize(pluginContext);
		
		GeoCatalogViewPlugIn catalogViewPlugIn = new GeoCatalogViewPlugIn();
		catalogViewPlugIn.initialize(pluginContext);

		SQLConsoleViewPlugIn consoleViewPlugIn = new SQLConsoleViewPlugIn();
		consoleViewPlugIn.initialize(pluginContext);

		BeanShellConsoleViewPlugIn beanShellConsoleViewPlugIn = new BeanShellConsoleViewPlugIn();
		beanShellConsoleViewPlugIn.initialize(pluginContext);

		InformationViewPlugIn informationViewPlugIn = new InformationViewPlugIn();
		informationViewPlugIn.initialize(pluginContext);

		OutputViewPlugIn outputViewPlugIn = new OutputViewPlugIn();
		outputViewPlugIn.initialize(pluginContext);

		GeomarkViewPlugIn geomarkViewPlugIn = new GeomarkViewPlugIn();
		geomarkViewPlugIn.initialize(pluginContext);

		MemoryViewPlugIn memoryViewPlugIn = new MemoryViewPlugIn();
		memoryViewPlugIn.initialize(pluginContext);

	}
}
