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
package org.orbisgis.geoview.rasterProcessing.action.extract;

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
import org.grap.processing.operation.extract.ExtractRGBBand;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.layerModel.CRSException;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.LayerCollection;
import org.orbisgis.geoview.layerModel.LayerException;
import org.orbisgis.geoview.layerModel.LayerFactory;
import org.orbisgis.geoview.rasterProcessing.action.utilities.AbstractColorRGBProcess;
import org.orbisgis.pluginManager.PluginManager;

public class ExtractRGBBands extends AbstractColorRGBProcess {
	@Override
	protected GeoRaster evaluateResult(GeoRaster geoRasterSrc) {
		// empty method...
		return null;
	}

	public void execute(GeoView2D view, ILayer resource) {
		try {
			final GeoRaster geoRasterSrc = resource.getDataSource()
					.getRaster(0);
			final ExtractRGBBand extractRGBBand = new ExtractRGBBand(
					geoRasterSrc);
			extractRGBBand.extractBands();

			final GeoRaster grRed = extractRGBBand.getRedBand();
			final GeoRaster grGreen = extractRGBBand.getGreenBand();
			final GeoRaster grBlue = extractRGBBand.getBlueBand();

			// save the computed GeoRaster in a tempFile
			final DataSourceFactory dsf = OrbisgisCore.getDSF();
			final String tempFileRed = dsf.getTempFile() + "red" + ".tif";
			final String tempFileGreen = dsf.getTempFile() + "green" + ".tif";
			final String tempFileBlue = dsf.getTempFile() + "blue" + ".tif";

			grRed.save(tempFileRed);
			grGreen.save(tempFileGreen);
			grBlue.save(tempFileBlue);

			// Create a layer collection and populate it
			final LayerCollection rgb = LayerFactory
					.createLayerCollection(resource.getName() + "_rgb");
			rgb.addLayer(LayerFactory.createLayer(new File(tempFileRed)));
			rgb.addLayer(LayerFactory.createLayer(new File(tempFileGreen)));
			rgb.addLayer(LayerFactory.createLayer(new File(tempFileBlue)));

			view.getViewContext().getLayerModel().insertLayer(rgb, 0);
		} catch (GeoreferencingException e) {
			PluginManager.error("Cannot compute " + resource.getName(), e);
		} catch (IOException e) {
			PluginManager.error("Cannot compute " + resource.getName(), e);
		} catch (LayerException e) {
			PluginManager.error("Cannot insert resulting layer based on "
					+ resource.getName(), e);
		} catch (CRSException e) {
			PluginManager.error(
					"Problem while trying to insert resulting layer based on "
							+ resource.getName(), e);
		} catch (DriverException e) {
			PluginManager.error("Cannot read the raster from the layer ", e);
		} catch (DriverLoadException e) {
			PluginManager.error(
					"Cannot create the resulting layer of raster type ", e);
		} catch (NoSuchTableException e) {
			PluginManager.error(
					"Cannot create the resulting layer of raster type ", e);
		} catch (DataSourceCreationException e) {
			PluginManager.error(
					"Cannot create the resulting layer of raster type ", e);
		} catch (OperationException e) {
			PluginManager.error("Error during the raster operation", e);
		}
	}
}