package org.orbisgis.rasterProcessing.action.utilities;

import ij.ImagePlus;

import java.io.IOException;

import org.gdms.driver.DriverException;
import org.orbisgis.Services;
import org.orbisgis.layerModel.ILayer;

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
			Services.getErrorManager().error("Raster type unreadable for this layer", e);
		
			Services.getErrorManager().error("Raster type unreadable for this layer", e);
		}
		return false;
	}
}