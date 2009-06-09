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
package org.orbisgis.geoprocessing.editorViews.toc.actions.extract;

import java.io.File;
import java.io.IOException;

import org.gdms.data.DataSourceFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.grap.lut.LutGenerator;
import org.grap.model.GeoRaster;
import org.grap.processing.OperationException;
import org.grap.processing.operation.extract.ExtractRGBBand;
import org.orbisgis.core.Services;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.geoprocessing.editorViews.toc.actions.utilities.AbstractColorRGBProcess;

public class ExtractRGBBands extends AbstractColorRGBProcess {
	@Override
	protected GeoRaster evaluateResult(GeoRaster geoRasterSrc) {
		// empty method...
		return null;
	}

	public void execute(MapContext mapContext, ILayer resource) {

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
			final DataSourceFactory dsf = ((DataManager) Services
					.getService(DataManager.class)).getDSF();
			final String tempFileRed = dsf.getTempFile() + "red" + ".tif";
			final String tempFileGreen = dsf.getTempFile() + "green" + ".tif";
			final String tempFileBlue = dsf.getTempFile() + "blue" + ".tif";

			grRed.getImagePlus().getProcessor().setColorModel(LutGenerator.colorModel("red"));
			grGreen.getImagePlus().getProcessor().setColorModel(LutGenerator.colorModel("green"));
			grBlue.getImagePlus().getProcessor().setColorModel(LutGenerator.colorModel("blue"));


			grRed.save(tempFileRed);
			grGreen.save(tempFileGreen);
			grBlue.save(tempFileBlue);

			// Create a layer collection and populate it
			DataManager dataManager = (DataManager) Services
					.getService(DataManager.class);
			final ILayer rgb = dataManager
					.createLayerCollection(resource.getName() + "_rgb");

			ILayer redLayer = dataManager.createLayer(new File(tempFileRed));
			redLayer.setName("Red");
			ILayer greenLayer = dataManager.createLayer(new File(tempFileGreen));
			greenLayer.setName("Green");
			ILayer blueLayer = dataManager.createLayer(new File(tempFileBlue));
			blueLayer.setName("Blue");

			rgb.addLayer(redLayer);
			rgb.addLayer(greenLayer);
			rgb.addLayer(blueLayer);

			mapContext.getLayerModel().insertLayer(rgb, 0);

		} catch (IOException e) {
			Services.getErrorManager().error(
					"Cannot compute " + resource.getName(), e);
		} catch (LayerException e) {
			Services.getErrorManager().error(
					"Cannot insert resulting layer based on "
							+ resource.getName(), e);
		} catch (DriverException e) {
			Services.getErrorManager().error(
					"Cannot read the raster from the layer ", e);
		} catch (DriverLoadException e) {
			Services.getErrorManager().error(
					"Cannot create the resulting layer of raster type ", e);
		} catch (OperationException e) {
			Services.getErrorManager().error(
					"Error during the raster operation", e);
		}
	}
}