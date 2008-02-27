package org.orbisgis.geoview.rasterProcessing.action.utilities;

import ij.ImagePlus;

import java.io.IOException;

import org.grap.io.GeoreferencingException;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.RasterLayer;
import org.orbisgis.geoview.views.toc.ILayerAction;
import org.orbisgis.pluginManager.PluginManager;

public abstract class AbstractGray16Process implements ILayerAction {
	public boolean accepts(ILayer layer) {
		if (layer instanceof RasterLayer) {
			final RasterLayer rs = (RasterLayer) layer;
			try {
				final int type = rs.getGeoRaster().getType();
				if ((type == ImagePlus.GRAY16)) {
					return true;
				}
			} catch (IOException e) {
				PluginManager
						.error(
								"Cannot access the GeoRaster type of this raster layer",
								e);
			} catch (GeoreferencingException e) {
				PluginManager
						.error(
								"Cannot access the GeoRaster type of this raster layer",
								e);
			}
		}
		return false;
	}
}