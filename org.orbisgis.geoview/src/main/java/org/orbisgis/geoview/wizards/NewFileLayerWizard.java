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
package org.orbisgis.geoview.wizards;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.driver.driverManager.DriverLoadException;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.core.wizards.OpenGdmsFilePanel;
import org.orbisgis.geoview.INewLayer;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.LayerFactory;
import org.orbisgis.geoview.layerModel.UnsupportedSourceException;
import org.orbisgis.pluginManager.PluginManager;
import org.sif.UIFactory;
import org.sif.UIPanel;

/**
 *
 */
public class NewFileLayerWizard implements INewLayer {

	public ILayer[] getLayers() {
		OpenGdmsFilePanel filePanel = new OpenGdmsFilePanel(
				"Select the file to add");
		ArrayList<ILayer> ret = new ArrayList<ILayer>();
		if (UIFactory.showDialog(new UIPanel[] { filePanel })) {
			File[] files = filePanel.getSelectedFiles();
			for (File file : files) {
				try {
					FileSourceDefinition fileSourceDefinition = new FileSourceDefinition(
							file);
					String registerName = OrbisgisCore.getDSF()
							.getSourceManager().getSourceName(
									fileSourceDefinition);
					if (registerName == null) {
						registerName = OrbisgisCore.registerInDSF(file
								.getName(), fileSourceDefinition);
					}
					ILayer layer = LayerFactory.createLayer(registerName);
					ret.add(layer);
				} catch (DriverLoadException e) {
					PluginManager.error("No suitable driver for file " + file,
							e);
				} catch (UnsupportedSourceException e) {
					PluginManager.error("The specified resource "
							+ "cannot be used as a layer", e);
				} catch (DataSourceCreationException e) {
					PluginManager.error(
							"Cannot instantiate data source for file " + file,
							e);
				} catch (NoSuchTableException e) {
					throw new RuntimeException("Bug!");
				} catch (IOException e) {
					PluginManager.error("Cannot access data " + file, e);
				}
			}
		}

		return ret.toArray(new ILayer[0]);
	}

	public String getName() {
		return "File";
	}

}
