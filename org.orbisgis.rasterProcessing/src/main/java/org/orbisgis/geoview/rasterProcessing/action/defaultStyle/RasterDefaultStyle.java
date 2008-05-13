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
package org.orbisgis.geoview.rasterProcessing.action.defaultStyle;

import ij.ImagePlus;

import java.io.IOException;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.grap.io.GeoreferencingException;
import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.renderer.legend.RasterLegend;
import org.orbisgis.pluginManager.PluginManager;
import org.sif.UIFactory;

public class RasterDefaultStyle implements
		org.orbisgis.geoview.views.toc.ILayerAction {

	public boolean accepts(ILayer layer) {
		try {
			if (layer.isRaster()) {
				SpatialDataSourceDecorator ds = layer.getDataSource();
				if (ds.getRaster(0).getType() != ImagePlus.COLOR_RGB) {
					return true;
				}
			}
		} catch (IOException e) {
		} catch (GeoreferencingException e) {
		} catch (DriverException e) {
		}
		return false;
	}

	public boolean acceptsAll(ILayer[] layer) {
		return true;
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return 1 == selectionCount;
	}

	public void execute(final GeoView2D view, final ILayer layer) {
		try {
			RasterLegend legend = (RasterLegend) layer.getLegend()[0];
			final RasterDefaultStyleUIPanel rasterDefaultStyleUIClass = new RasterDefaultStyleUIPanel(
					legend);

			if (UIFactory.showDialog(rasterDefaultStyleUIClass)) {
				legend.setColorModel(rasterDefaultStyleUIClass.getColorModel());
				legend.setOpacity(rasterDefaultStyleUIClass.getOpacity());
			}

		} catch (DriverException e) {
			PluginManager.error("Cannot get the legend", e);
		}
	}

	public void executeAll(GeoView2D view, ILayer[] layers) {
	}
}