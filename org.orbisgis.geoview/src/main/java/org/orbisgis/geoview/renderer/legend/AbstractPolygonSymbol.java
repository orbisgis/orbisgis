package org.orbisgis.geoview.renderer.legend;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

public abstract class AbstractPolygonSymbol extends AbstractSymbol {

	public boolean willDrawSimpleGeometry(Geometry geom) {
		return geom instanceof Polygon || geom instanceof MultiPolygon;
	}

}
