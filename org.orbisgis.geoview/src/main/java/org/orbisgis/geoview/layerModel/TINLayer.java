package org.orbisgis.geoview.layerModel;

import org.gdms.data.DataSource;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class TINLayer extends MeshLayer {
	public TINLayer(String name, DataSource ds,
			final CoordinateReferenceSystem coordinateReferenceSystem) {
		super(name, ds, coordinateReferenceSystem);
	}
}