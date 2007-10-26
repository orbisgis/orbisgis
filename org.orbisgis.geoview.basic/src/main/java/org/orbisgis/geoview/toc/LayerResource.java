package org.orbisgis.geoview.toc;

import org.orbisgis.core.resourceTree.BasicResource;
import org.orbisgis.core.resourceTree.IResource;
import org.orbisgis.geoview.ILayerResource;
import org.orbisgis.geoview.layerModel.ILayer;

class LayerResource extends BasicResource implements IResource, ILayerResource {

	private ILayer layer;

	public LayerResource(ILayer layer) {
		super(layer.getName());
		this.layer = layer;
	}

	/**
	 * @see org.orbisgis.geoview.ILayerResource#getLayer()
	 */
	public ILayer getLayer() {
		return layer;
	}

}
