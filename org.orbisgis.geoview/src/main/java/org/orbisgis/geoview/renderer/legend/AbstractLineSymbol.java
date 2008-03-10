package org.orbisgis.geoview.renderer.legend;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;

public abstract class AbstractLineSymbol extends AbstractSymbol {

	public boolean willDrawSimpleGeometry(Geometry geom) {
		return geom instanceof LineString || geom instanceof MultiLineString;
	}

}
