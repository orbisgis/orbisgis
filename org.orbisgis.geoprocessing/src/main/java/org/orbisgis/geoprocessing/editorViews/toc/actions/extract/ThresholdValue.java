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

import ij.ImagePlus;

import java.io.IOException;

import org.gdms.driver.DriverException;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.processing.OperationException;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.geoprocessing.editorViews.toc.actions.utilities.AbstractRasterProcess;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.multiInputPanel.DoubleType;
import org.orbisgis.sif.multiInputPanel.MultiInputPanel;

public class ThresholdValue extends AbstractRasterProcess {
	public boolean accepts(MapContext mc, ILayer layer) {
		try {
			if (layer.isRaster()) {
				final int type = layer.getRaster().getType();
				if ((type == ImagePlus.GRAY8) || (type == ImagePlus.GRAY16)
						|| (type == ImagePlus.GRAY32)) {
					return true;
				}
			}
		} catch (DriverException e) {
		} catch (IOException e) {
			Services.getErrorManager().error(
					"Raster type unreadable for this layer", e);
		}
		return false;
	}

	@Override
	protected GeoRaster evaluateResult(GeoRaster geoRasterSrc)
			throws OperationException, IOException {
		final MultiInputPanel mip = new MultiInputPanel(
				"Min - Max pixel reclassification");
		mip.addInput("MinValue", "Min value", new Double(geoRasterSrc.getMin())
				.toString(), new DoubleType(12));
		mip.addInput("MaxValue", "Max value", new Float(geoRasterSrc.getMax())
				.toString(), new DoubleType(12));

		if (UIFactory.showDialog(mip)) {
			final double min = new Double(mip.getInput("MinValue"));
			final double max = new Double(mip.getInput("MaxValue"));
			final GeoRaster geoRasterResult = GeoRasterFactory.createGeoRaster(
					geoRasterSrc.getImagePlus(), geoRasterSrc.getMetadata());
			geoRasterResult.setRangeValues(min, max);
			geoRasterResult.setNodataValue((float) geoRasterSrc.getMetadata()
					.getNoDataValue());
			return geoRasterResult;
		}

		return null;
	}
}