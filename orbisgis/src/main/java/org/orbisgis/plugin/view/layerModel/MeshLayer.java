package org.orbisgis.plugin.view.layerModel;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class MeshLayer extends VectorLayer {
	public MeshLayer(String name,final CoordinateReferenceSystem coordinateReferenceSystem) {
		super(name, coordinateReferenceSystem);
	}
}