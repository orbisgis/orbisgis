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
package org.orbisgis.core.ui.plugins.toc;

import java.io.File;

import org.gdms.data.DataSourceFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileDriver;
import org.gdms.driver.driverManager.Driver;
import org.gdms.driver.driverManager.DriverFilter;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.source.AndDriverFilter;
import org.gdms.source.CSVFileDriverFilter;
import org.gdms.source.FileDriverFilter;
import org.gdms.source.OrDriverFilter;
import org.gdms.source.RasterDriverFilter;
import org.gdms.source.SourceManager;
import org.gdms.source.VectorialDriverFilter;
import org.gdms.source.WritableDriverFilter;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.background.BackgroundManager;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.sif.SaveFilePanel;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.PlugInContext.LayerAvailability;
import org.orbisgis.core.ui.pluginSystem.PlugInContext.SelectionAvailability;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.utils.I18N;

public class SaveInFilePlugIn extends AbstractPlugIn {

	public boolean execute(PlugInContext context) throws DriverException {
		MapContext mapContext = getPlugInContext().getMapContext();
		ILayer[] selectedResources = mapContext.getSelectedLayers();
		for (ILayer resource : selectedResources) {
			if (resource.isWMS()) {
				continue;
			}
			final SaveFilePanel outfilePanel = new SaveFilePanel(
					"org.orbisgis.core.ui.editorViews.toc.actions.SaveInFile",
					I18N.getText("orbisgis.core.file.chooseFileFormat"));

			DataManager dm = Services.getService(DataManager.class);
			final DataSourceFactory dsf = dm.getDataSourceFactory();
			SourceManager sourceManager = dm.getSourceManager();
			DriverManager driverManager = sourceManager.getDriverManager();
			DriverFilter filter = null;
			if (resource.isRaster()) {
				filter = new RasterDriverFilter();
			} else if (resource.isVectorial()) {
				// no other choice but to add CSV here
				// because of CSVStringDriver implementation
				filter = new OrDriverFilter(new VectorialDriverFilter(),
						new CSVFileDriverFilter());
			}
			Driver[] filtered = driverManager
					.getDrivers(new AndDriverFilter(new FileDriverFilter(),
							filter, new WritableDriverFilter()));
			for (int i = 0; i < filtered.length; i++) {
				FileDriver fileDriver = (FileDriver) filtered[i];
				String[] extensions = fileDriver.getFileExtensions();
				outfilePanel.addFilter(extensions, fileDriver
						.getTypeDescription());
			}

			if (UIFactory.showDialog(outfilePanel)) {
				final File savedFile = new File(outfilePanel.getSelectedFile()
						.getAbsolutePath());
				BackgroundManager bm = Services
						.getService(BackgroundManager.class);
				bm.backgroundOperation(new ExportInFileOperation(dsf, resource
						.getName(), savedFile, null));

			}
		}
		return true;
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame().getToc();
		context.getFeatureInstaller().addPopupMenuItem(
				frame,
				this,
				new String[] { Names.POPUP_TOC_EXPORT_SAVE,
						Names.TOC_EXPORT_SAVEIN_FILE },
				Names.POPUP_TOC_EXPORT_GROUP, false, null, wbContext);
	}

	public boolean isEnabled() {
		return getPlugInContext().checkLayerAvailability(
				new SelectionAvailability[] { SelectionAvailability.SUPERIOR },
				0, new LayerAvailability[] { LayerAvailability.EXPORTABLE });
	}
}
