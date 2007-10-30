package org.orbisgis.geoview.toc;

import org.orbisgis.core.resourceTree.Folder;
import org.orbisgis.geoview.layerModel.ILayer;

public class LayerCollectionResource extends Folder implements ILayerResource {

	private ILayer layer;

	LayerCollectionResource(ILayer layer) {
		super(layer.getName());
		this.layer = layer;
		syncWithLayerModel();
	}

	public ILayer getLayer() {
		return layer;
	}

	@Override
	public String getName() {
		return layer.getName();
	}

	public void syncWithLayerModel() {
		super.clear();
		ILayer[] children = layer.getChildren();
		for (ILayer child : children) {
			this.addChild(LayerResourceFactory.getLayerResource(child));
		}
	}

}
