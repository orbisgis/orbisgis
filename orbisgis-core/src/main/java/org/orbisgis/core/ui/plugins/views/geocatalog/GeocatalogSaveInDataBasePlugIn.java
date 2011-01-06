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

package org.orbisgis.core.ui.plugins.views.geocatalog;

import org.gdms.source.Source;
import org.gdms.source.SourceManager;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.background.BackgroundManager;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.sif.UIPanel;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.plugins.toc.ExportInDatabaseOperation;
import org.orbisgis.core.ui.plugins.toc.SchemaSelectionPanel;
import org.orbisgis.core.ui.plugins.views.geocatalog.newSourceWizards.db.ConnectionPanel;

public class GeocatalogSaveInDataBasePlugIn extends AbstractPlugIn {

	public boolean execute(PlugInContext context) throws Exception {
		DataManager dm = Services.getService(DataManager.class);
		String[] res = getPlugInContext().getSelectedSources();
		if (res.length == 0) {
			execute(dm.getSourceManager(), null);
		} else {
			for (String resource : res) {
				execute(dm.getSourceManager(), resource);
			}
		}
		return true;
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame()
				.getGeocatalog();
		context.getFeatureInstaller().addPopupMenuItem(
				frame,
				this,
				new String[] { Names.POPUP_TOC_EXPORT_SAVE,
						Names.TOC_EXPORT_SAVEIN_DB },
				Names.POPUP_GEOCATALOG_EXPORT_INDB, false, null, wbContext);

	}

	public void execute(SourceManager sourceManager, String currentNode) {
		final ConnectionPanel firstPanel = new ConnectionPanel();
		final SchemaSelectionPanel schemaSelectionPanel = new SchemaSelectionPanel(
				firstPanel, currentNode);

		if (UIFactory.showDialog(new UIPanel[] { firstPanel,
				schemaSelectionPanel })) {
			DataManager dm = Services.getService(DataManager.class);
			String layerName = schemaSelectionPanel.getSourceName();
			String schemaName = schemaSelectionPanel.getSelectedSchema();
			BackgroundManager bm = Services.getService(BackgroundManager.class);
			bm.backgroundOperation(new ExportInDatabaseOperation(dm
					.getDataSourceFactory(), currentNode, layerName,
					schemaName, firstPanel.getDBSource(), firstPanel
							.getDBDriver()));

		}
	}

	public boolean isEnabled() {

		WorkbenchContext workbenchContext = getPlugInContext()
				.getWorkbenchContext();
		String[] res = workbenchContext.getWorkbench().getFrame()
				.getGeocatalog().getSelectedSources();
		DataManager dataManager = Services.getService(DataManager.class);
		SourceManager sourceManager = dataManager.getSourceManager();
		boolean acceptsAllSources = false;

		if (res.length > 0) {

			for (String src : res) {
				Source source = sourceManager.getSource(src);
				int type = source.getType();
				if ((type & SourceManager.WMS) == SourceManager.WMS) {
					acceptsAllSources = false;
				} else {
					acceptsAllSources = true;
				}
			}
		}

		return acceptsAllSources;
	}
}
