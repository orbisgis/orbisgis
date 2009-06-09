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
package org.orbisgis.geoprocessing.editorViews.toc.actions.utilities;

import java.io.IOException;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.strategies.SemanticException;
import org.grap.processing.OperationException;
import org.orbisgis.core.Services;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editorViews.toc.action.ILayerAction;

public abstract class NewAbstractRasterProcess implements ILayerAction {
	public void execute(MapContext mapContext, ILayer layer) {
		try {

			final String geoRasterResult = evaluateResult(layer, mapContext);

			if (null != geoRasterResult) {
				// save the computed GeoRaster in a tempFile
				final DataSourceFactory dsf = ((DataManager) Services
						.getService(DataManager.class)).getDSF();

				DataSource dsResult = dsf.getDataSourceFromSQL(geoRasterResult);

				// populate the GeoView TOC with a new RasterLayer
				DataManager dataManager = (DataManager) Services
						.getService(DataManager.class);
				final ILayer newLayer = dataManager.createLayer(dsResult);
				mapContext.getLayerModel().insertLayer(newLayer, 0);
			}
		} catch (IOException e) {
			Services.getErrorManager().error(
					"Cannot compute " + layer.getName(), e);
		} catch (LayerException e) {
			Services.getErrorManager().error(
					"Cannot insert resulting layer based on "
							+ layer.getName(), e);
		} catch (DriverException e) {
			Services.getErrorManager().error(
					"Cannot read the raster from the layer ", e);
		} catch (DriverLoadException e) {
			Services.getErrorManager().error(
					"Cannot create the resulting layer of raster type ", e);
		} catch (OperationException e) {
			Services.getErrorManager().error(
					"Error during the raster operation", e);
		} catch (DataSourceCreationException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (SemanticException e) {
			e.printStackTrace();
		}
	}

	protected abstract String evaluateResult(ILayer layer,MapContext mapContext)
			throws OperationException, IOException, DriverException;

	public final boolean acceptsSelectionCount(int selectionCount) {
		return selectionCount >= 1;
	}

	public boolean accepts(MapContext mc, ILayer layer) {
		try {
			return layer.isRaster();
		} catch (DriverException e) {
			return false;
		}
	}
}