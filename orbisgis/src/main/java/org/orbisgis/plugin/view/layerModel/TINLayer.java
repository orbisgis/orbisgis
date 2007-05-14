package org.orbisgis.plugin.view.layerModel;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class TINLayer extends MeshLayer {
	public TINLayer(String name,
			final CoordinateReferenceSystem coordinateReferenceSystem) {
		super(name, coordinateReferenceSystem);
	}
}