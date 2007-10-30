package org.orbisgis.geoview.toc;

import org.orbisgis.core.resourceTree.IResource;
import org.orbisgis.geoview.layerModel.ILayer;

public interface ILayerResource extends IResource {

	public abstract ILayer getLayer();

	public void syncWithLayerModel();

}
