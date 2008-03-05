/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geomatic team of
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
package org.orbisgis.geoview.actions.io;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.gdms.data.DataSource;
import org.gdms.data.ExecutionException;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.driver.DriverException;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.strategies.SemanticException;
import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.RasterLayer;
import org.orbisgis.geoview.layerModel.VectorLayer;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.pluginManager.ui.SaveFilePanel;
import org.sif.UIFactory;

public class SaveVectorLayerInAFile implements
		org.orbisgis.geoview.views.toc.ILayerAction {

	public boolean accepts(ILayer layer) {
		return layer instanceof VectorLayer;
	}

	public boolean acceptsAll(ILayer[] layer) {
		return true;
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return selectionCount >= 1;
	}

	public void execute(GeoView2D view, ILayer resource) {
		final String layerName = ((VectorLayer) resource).getName();

		
		
		final SaveFilePanel outfilePanel = new SaveFilePanel(
				"org.orbisgis.geoview.vectorLayer.io.saveInAFile",
				"Choose a file format");
		try {
			outfilePanel.addFilter("shp", "Esri shapefile format (*.shp)");
			

			if (UIFactory.showDialog(outfilePanel)) {
				
				File savedFile = new File(outfilePanel.getSelectedFile().getAbsolutePath());
				
				String fileName = savedFile.getName();
				
				FileSourceDefinition def = new FileSourceDefinition(savedFile);
				
				OrbisgisCore.getDSF().getSourceManager().register(fileName, def);
								
				OrbisgisCore.getDSF().saveContents(fileName, ((VectorLayer) resource).getDataSource());
				
				JOptionPane.showMessageDialog(null, "The file has been saved and added in the geocatalog.");
			}
		} catch (DriverException e) {
			PluginManager.error("Cannot saved the layer.", e);
		}

	}

	public void executeAll(GeoView2D view, ILayer[] layers) {
	}
}