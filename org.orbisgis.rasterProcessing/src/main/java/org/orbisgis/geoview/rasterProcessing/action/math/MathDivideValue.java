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
package org.orbisgis.geoview.rasterProcessing.action.math;

import java.io.File;
import java.io.IOException;

import org.gdms.data.DataSourceFactory;
import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;
import org.grap.processing.OperationException;
import org.grap.processing.operation.math.DivideValueOperation;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.layerModel.CRSException;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.LayerException;
import org.orbisgis.geoview.layerModel.LayerFactory;
import org.orbisgis.geoview.layerModel.RasterLayer;
import org.orbisgis.pluginManager.PluginManager;
import org.sif.UIFactory;
import org.sif.multiInputPanel.DoubleType;
import org.sif.multiInputPanel.MultiInputPanel;

public class MathDivideValue implements
		org.orbisgis.geoview.views.toc.ILayerAction {

	public boolean accepts(ILayer layer) {
		return layer instanceof RasterLayer;
	}

	public boolean acceptsAll(ILayer[] layer) {
		return true;
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return selectionCount >= 1;
	}

	public void execute(GeoView2D view, ILayer resource) {
		final Double divideValue = getValueToDivideBy();

		if (null != divideValue) {
			try {
				final GeoRaster geoRasterSrc = ((RasterLayer) resource)
						.getGeoRaster();
				final GeoRaster grResult = geoRasterSrc
						.doOperation(new DivideValueOperation(divideValue));
				// save the computed GeoRaster in a tempFile
				final DataSourceFactory dsf = OrbisgisCore.getDSF();
				final String tempFile = dsf.getTempFile() + ".tif";
				grResult.save(tempFile);

				// populate the GeoView TOC with a new RasterLayer
				final ILayer newLayer = LayerFactory
						.createRasterLayer(new File(tempFile));
				view.getViewContext().getLayerModel().insertLayer(newLayer, 0);

			} catch (GeoreferencingException e) {
				PluginManager.error("Cannot compute " + getClass().getName()
						+ ": " + resource.getName(), e);
			} catch (IOException e) {
				PluginManager.error("Cannot compute " + getClass().getName()
						+ ": " + resource.getName(), e);
			} catch (OperationException e) {
				PluginManager.error("Cannot compute " + getClass().getName()
						+ ": " + resource.getName(), e);
			} catch (LayerException e) {
				PluginManager.error("Cannot compute " + getClass().getName()
						+ ": " + resource.getName(), e);
			} catch (CRSException e) {
				PluginManager.error("Cannot compute " + getClass().getName()
						+ ": " + resource.getName(), e);
			}
		}
	}

	private Double getValueToDivideBy() {
		final MultiInputPanel mip = new MultiInputPanel(
				"DivideValue initialization");
		mip
				.addInput("DivideValue", "Value to divide by", "1",
						new DoubleType());
		mip.addValidationExpression("DivideValue != 0",
				"DivideValue must not be equal to zero !");

		if (UIFactory.showDialog(mip)) {
			return new Double(mip.getInput("DivideValue"));
		} else {
			return null;
		}
	}

	public void executeAll(GeoView2D view, ILayer[] layers) {
	}
}