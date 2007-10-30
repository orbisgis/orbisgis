package org.orbisgis.geoview.layerModel;

public class LayerCollectionEvent {
	private ILayer parent;
	private ILayer[] affected;

	public LayerCollectionEvent(ILayer parent, ILayer[] affected) {
		super();
		this.parent = parent;
		this.affected = affected;
	}

	public ILayer[] getAffected() {
		return affected;
	}

	public ILayer getParent() {
		return parent;
	}

}
