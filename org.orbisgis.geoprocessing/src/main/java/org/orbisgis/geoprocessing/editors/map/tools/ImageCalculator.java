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
package org.orbisgis.geoprocessing.editors.map.tools;

import java.io.File;
import java.io.IOException;

import org.gdms.data.DataSourceFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.grap.model.GeoRaster;
import org.grap.processing.OperationException;
import org.grap.processing.operation.GeoRasterCalculator;
import org.orbisgis.core.Services;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.components.sif.RasterLayerCombo;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editor.action.IEditorAction;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.multiInputPanel.ComboBoxChoice;
import org.orbisgis.sif.multiInputPanel.MultiInputPanel;

public class ImageCalculator implements IEditorAction {
	public static final String DIALOG_ID = "org.orbisgis.geoview.rasterProcessing.ImageCalculator";

	public void actionPerformed(IEditor editor) {
		try {
			MapContext mapContext = (MapContext) editor.getElement()
					.getObject();

			final MultiInputPanel mip = new MultiInputPanel("Image calculator");
			mip.addInput("source1", "Raster layer1", new RasterLayerCombo(
					mapContext));
			mip.addInput("method", "Method", new ComboBoxChoice(
					GeoRasterCalculator.operators.keySet().toArray(
							new String[0])));
			mip.addInput("source2", "Raster layer2", new RasterLayerCombo(
					mapContext));

			if (UIFactory.showDialog(mip)) {
				final ILayer raster1 = mapContext.getLayerModel()
						.getLayerByName(mip.getInput("source1"));
				final ILayer raster2 = mapContext.getLayerModel()
						.getLayerByName(mip.getInput("source2"));
				final String method = mip.getInput("method");

				final GeoRaster grResult = raster1.getRaster().doOperation(
						new GeoRasterCalculator(raster2.getRaster(),
								GeoRasterCalculator.operators.get(method)));

				// save the computed GeoRaster in a tempFile
				final DataSourceFactory dsf = ((DataManager) Services
						.getService(DataManager.class)).getDSF();
				final String tempFile = dsf.getTempFile() + ".tif";
				grResult.save(tempFile);

				// populate the GeoView TOC with a new RasterLayer
				DataManager dataManager = (DataManager) Services
						.getService(DataManager.class);
				final ILayer newLayer = dataManager.createLayer(new File(
						tempFile));
				mapContext.getLayerModel().insertLayer(newLayer, 0);
			}

		} catch (OperationException e) {
			Services.getErrorManager().error("Cannot do the operation", e);
		} catch (IOException e) {
			Services.getErrorManager().error(
					"Error in " + this.getClass().getSimpleName(), e);
		} catch (LayerException e) {
			Services.getErrorManager().error(
					"Error in " + this.getClass().getSimpleName(), e);
		} catch (DriverException e) {
			Services.getErrorManager().error(
					"Problem while accessing GeoRaster datas", e);
		} catch (DriverLoadException e) {
			Services.getErrorManager().error(
					"Cannot create the resulting GeoRaster layer", e);
		}
	}

	public boolean isEnabled(IEditor editor) {
		try {
			MapContext mc = (MapContext) editor.getElement().getObject();
			ILayer[] selectedLayers = mc.getSelectedLayers();
			if ((selectedLayers.length == 1) && selectedLayers[0].isRaster()
					&& selectedLayers[0].isVisible()) {
				return true;
			}
		} catch (DriverException e) {
		}
		return false;
	}

	public boolean isVisible(IEditor editor) {
		return true;
	}
}