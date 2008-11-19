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
package org.orbisgis.editorViews.toc.actions;

import java.io.File;

import javax.swing.JOptionPane;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreation;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.file.FileSourceCreation;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileDriver;
import org.gdms.driver.driverManager.Driver;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.source.AndDriverFilter;
import org.gdms.source.FileDriverFilter;
import org.gdms.source.SourceManager;
import org.gdms.source.VectorialDriverFilter;
import org.gdms.source.WritableDriverFilter;
import org.orbisgis.DataManager;
import org.orbisgis.Services;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.MapContext;
import org.orbisgis.pluginManager.ui.SaveFilePanel;
import org.sif.UIFactory;

public class SaveInFile implements
		org.orbisgis.editorViews.toc.action.ILayerAction {

	public boolean accepts(MapContext mc, ILayer layer) {
		try {
			return layer.isVectorial();
		} catch (DriverException e) {
			return false;
		}
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return 1 == selectionCount;
	}

	public void execute(MapContext mapContext, ILayer resource) {
		final SaveFilePanel outfilePanel = new SaveFilePanel(
				"org.orbisgis.editorViews.toc.actions.SaveInFile",
				"Choose a file format");
		try {
			DataManager dm = Services.getService(DataManager.class);
			SourceManager sourceManager = dm.getSourceManager();
			DriverManager driverManager = sourceManager.getDriverManager();

			Driver[] filtered = driverManager.getDrivers(new AndDriverFilter(
					new FileDriverFilter(), new VectorialDriverFilter(),
					new WritableDriverFilter()));
			for (int i = 0; i < filtered.length; i++) {
				FileDriver fileDriver = (FileDriver) filtered[i];
				String[] extensions = fileDriver.getFileExtensions();
				outfilePanel.addFilter(extensions, sourceManager
						.getSourceTypeDescription(fileDriver.getType()));
			}

			if (UIFactory.showDialog(outfilePanel)) {
				final File savedFile = new File(outfilePanel.getSelectedFile()
						.getAbsolutePath());
				String fileName = savedFile.getName();
				int index = fileName.lastIndexOf('.');
				if (index != -1) {
					fileName = fileName.substring(0, index);
				}
				final FileSourceDefinition def = new FileSourceDefinition(
						savedFile);
				Services.getService(DataManager.class).getDSF()
						.getSourceManager().register(fileName, def);

				int[] selection = resource.getSelection();

				SpatialDataSourceDecorator datasource = resource
						.getDataSource();
				if (selection.length > 0) {

					int response = JOptionPane.showConfirmDialog(null,
							"Do you want to save only the selected features",
							"Export layer", JOptionPane.YES_NO_OPTION);

					if (response == JOptionPane.YES_OPTION) {

						ObjectMemoryDriver om = new ObjectMemoryDriver(
								datasource.getMetadata());

						for (int i = 0; i < selection.length; i++) {
							Value[] value = datasource.getRow(i);
							om.addValues(value);
						}

						DataSource dataResult = Services.getService(
								DataManager.class).getDSF().getDataSource(om);
						Services.getService(DataManager.class).getDSF()
								.saveContents(
										fileName,
										new SpatialDataSourceDecorator(
												dataResult));
						JOptionPane
								.showMessageDialog(null,
										"The file has been saved and added in the geocatalog.");

					} else {

						Services.getService(DataManager.class).getDSF()
								.saveContents(fileName, datasource);
						JOptionPane
								.showMessageDialog(null,
										"The file has been saved and added in the geocatalog.");

					}
				} else {

					Services.getService(DataManager.class).getDSF()
							.saveContents(fileName, datasource);
					JOptionPane
							.showMessageDialog(null,
									"The file has been saved and added in the geocatalog.");
				}
			}
		} catch (DriverException e) {
			Services.getErrorManager().error("Cannot saved the layer.", e);
		}
	}
}