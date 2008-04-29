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
package org.orbisgis.geoview.rasterProcessing.toolbar;

import java.io.File;
import java.io.IOException;

import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;
import org.grap.processing.OperationException;
import org.grap.processing.operation.GeoRasterCalculator;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.IGeoviewAction;
import org.orbisgis.geoview.layerModel.CRSException;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.LayerException;
import org.orbisgis.geoview.layerModel.LayerFactory;
import org.orbisgis.geoview.sif.RasterLayerCombo;
import org.orbisgis.pluginManager.PluginManager;
import org.sif.UIFactory;
import org.sif.multiInputPanel.ComboBoxChoice;
import org.sif.multiInputPanel.MultiInputPanel;

public class ImageCalculator implements IGeoviewAction {
	public static final String DIALOG_ID = "org.orbisgis.geoview.rasterProcessing.ImageCalculator";

	public void actionPerformed(GeoView2D view) {
		try {
			final MultiInputPanel mip = new MultiInputPanel(DIALOG_ID,
					"Image calculator");
			mip.addInput("source1", "Raster layer1", new RasterLayerCombo(view
					.getViewContext()));
			mip.addInput("method", "Method", new ComboBoxChoice(
					GeoRasterCalculator.operators.keySet().toArray(
							new String[0])));
			mip.addInput("source2", "Raster layer2", new RasterLayerCombo(view
					.getViewContext()));

			if (UIFactory.showDialog(mip)) {
				final ILayer raster1 = view.getViewContext().getLayerModel()
						.getLayerByName(mip.getInput("source1"));
				final ILayer raster2 = view.getViewContext().getLayerModel()
						.getLayerByName(mip.getInput("source2"));
				final String method = mip.getInput("method");

				final GeoRaster grResult = raster1.getRaster().doOperation(
						new GeoRasterCalculator(raster2.getRaster(),
								GeoRasterCalculator.operators.get(method)));

				// save the computed GeoRaster in a tempFile
				final DataSourceFactory dsf = OrbisgisCore.getDSF();
				final String tempFile = dsf.getTempFile() + ".tif";
				grResult.save(tempFile);

				// populate the GeoView TOC with a new RasterLayer
				final ILayer newLayer = LayerFactory.createLayer(new File(
						tempFile));
				view.getViewContext().getLayerModel().insertLayer(newLayer, 0);
			}

		} catch (OperationException e) {
			PluginManager.error("Cannot do the operation", e);
		} catch (GeoreferencingException e) {
			PluginManager.error("Error in " + this.getClass().getSimpleName(),
					e);
		} catch (IOException e) {
			PluginManager.error("Error in " + this.getClass().getSimpleName(),
					e);
		} catch (LayerException e) {
			PluginManager.error("Error in " + this.getClass().getSimpleName(),
					e);
		} catch (CRSException e) {
			PluginManager.error("Error in " + this.getClass().getSimpleName(),
					e);
		} catch (DriverException e) {
			PluginManager.error("Problem while accessing GeoRaster datas", e);
		} catch (DriverLoadException e) {
			PluginManager.error(
					"Cannot create the resulting GeoRaster layer", e);
		} catch (NoSuchTableException e) {
			PluginManager.error(
					"Cannot create the resulting GeoRaster layer", e);
		} catch (DataSourceCreationException e) {
			PluginManager.error(
					"Cannot create the resulting GeoRaster layer", e);
		}
	}

	public boolean isEnabled(GeoView2D geoView2D) {
		try {
			if ((geoView2D.getViewContext().getSelectedLayers().length == 1)
					&& geoView2D.getViewContext().getSelectedLayers()[0]
							.isRaster()
					&& geoView2D.getViewContext().getSelectedLayers()[0]
							.isVisible()) {
				return true;
			}
		} catch (DriverException e) {
		}
		return false;
	}

	public boolean isVisible(GeoView2D geoView2D) {
		return true;
	}
}