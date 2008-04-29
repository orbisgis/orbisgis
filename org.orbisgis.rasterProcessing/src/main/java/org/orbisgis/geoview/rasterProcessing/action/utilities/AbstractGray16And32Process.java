package org.orbisgis.geoview.rasterProcessing.action.utilities;

import ij.ImagePlus;

import java.io.IOException;

import org.gdms.driver.DriverException;
import org.grap.io.GeoreferencingException;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.pluginManager.PluginManager;

public abstract class AbstractGray16And32Process extends AbstractRasterProcess {
	public final boolean accepts(ILayer layer) {
		try {
			if (layer.isRaster()) {
				final int type = layer.getRaster().getType();
				if ((type == ImagePlus.GRAY16) || (type == ImagePlus.GRAY32)) {
					return true;
				}
			}
		} catch (DriverException e) {
		} catch (IOException e) {
			PluginManager.error("Raster type unreadable for this layer", e);
		} catch (GeoreferencingException e) {
			PluginManager.error("Raster type unreadable for this layer", e);
		}
		return false;
	}
}