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
package org.orbisgis.core.ui.editors.map.newLayerWizards;

import java.io.File;
import java.util.ArrayList;

import org.gdms.data.InitializationException;
import org.gdms.data.SourceAlreadyExistsException;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.source.SourceManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.ui.editors.map.newLayerWizard.INewLayer;
import org.orbisgis.core.ui.wizards.OpenGdmsFilePanel;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.utils.FileUtils;

/**
 *
 */
public class NewFileLayer implements INewLayer {

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
					DataManager dataManager = (DataManager) Services
							.getService(DataManager.class);
					SourceManager sourceManager = (dataManager).getDSF()
							.getSourceManager();
					String registerName = sourceManager
							.getSourceName(fileSourceDefinition);
					if (registerName == null) {
						registerName = sourceManager.getUniqueName(FileUtils
								.getFileNameWithoutExtensionU(file));
						sourceManager.register(registerName,
								fileSourceDefinition);
					}
					ILayer layer = dataManager.createLayer(registerName);
					ret.add(layer);
				} catch (LayerException e) {
					Services.getErrorManager().error(
							"Cannot create layer for file: " + file, e);
								} catch (SourceAlreadyExistsException e) {
					Services.getErrorManager().error("Name collision: " + file,
							e);
				} catch (InitializationException e) {
					Services.getErrorManager().error(
							"Cannot initialize source: " + file, e);

				}
			}
		}

		return ret.toArray(new ILayer[0]);
	}

	public String getName() {
		return "File";
	}

}
