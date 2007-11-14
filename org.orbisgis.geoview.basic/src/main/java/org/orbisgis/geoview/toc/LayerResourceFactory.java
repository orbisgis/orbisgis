package org.orbisgis.geoview.toc;

import org.orbisgis.geoview.layerModel.IGdmsLayer;
import org.orbisgis.geoview.layerModel.ILayer;

public class LayerResourceFactory {

	public static ILayerResource getLayerResource(ILayer layer) {
		if (layer.acceptsChilds()) {
			LayerCollectionResource ret = new LayerCollectionResource(layer);
			ret.setFoldersFirst(false);
			return ret;
		} else {
			if (layer instanceof IGdmsLayer){
				return new GdmsLayerResource((IGdmsLayer) layer);
			} else {
				return new LayerResource(layer);
			}
		}
	}

}
