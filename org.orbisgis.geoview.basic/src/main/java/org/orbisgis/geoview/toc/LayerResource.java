package org.orbisgis.geoview.toc;

import org.orbisgis.core.resourceTree.BasicResource;
import org.orbisgis.geoview.layerModel.ILayer;

class LayerResource extends BasicResource implements ILayerResource {

	private ILayer layer;

	public LayerResource(ILayer layer) {
		super(layer.getName());
		this.layer = layer;
	}

	/**
	 * @see org.orbisgis.geoview.toc.ILayerResource#getLayer()
	 */
	public ILayer getLayer() {
		return layer;
	}

	@Override
	public String getName() {
		return layer.getName();
	}

	public void syncWithLayerModel() {

	}

}
