package org.orbisgis.renderer.legend;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;

public abstract class AbstractPointSymbol extends AbstractGeometrySymbol {

	public boolean willDrawSimpleGeometry(Geometry geom) {
		return geom instanceof Point || geom instanceof MultiPoint;
	}
}