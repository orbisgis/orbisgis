package org.orbisgis.geoview.layerModel;

import org.gdms.data.DataSource;
import org.gdms.spatial.NullCRS;
import org.grap.model.GeoRaster;

public class LayerFactory {

	public static LayerCollection createLayerCollection(String name) {
		return new LayerCollection(name);
	}

	public static VectorLayer createVectorialLayer(String registerName,
			DataSource ds) {
		return new VectorLayer(registerName, ds, NullCRS.singleton);
	}

	public static RasterLayer createRasterLayer(String registerName,
			GeoRaster georaster) {
		return new RasterLayer(registerName, NullCRS.singleton, georaster);
	}
}
