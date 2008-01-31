package org.orbisgis.geoview.renderer.legend;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;

public abstract class AbstractPointSymbol extends AbstractSymbol implements
		Symbol {

	public boolean willDrawSimpleGeometry(Geometry geom) {
		return geom instanceof Point || geom instanceof MultiPoint;
	}
}