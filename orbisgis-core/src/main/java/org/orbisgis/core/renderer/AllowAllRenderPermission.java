package org.orbisgis.core.renderer;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public class AllowAllRenderPermission implements RenderPermission{

	@Override
	public boolean canDraw(Envelope env) {
		return true;
	}

	@Override
	public Geometry getValidGeometry(Geometry geometry, double distance) {
		return geometry;
	}

}
