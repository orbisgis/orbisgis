package org.orbisgis.renderer.legend;

import com.vividsolutions.jts.geom.Geometry;

public abstract class AbstractGeometrySymbol extends AbstractSymbol implements Symbol {

	public boolean willDraw(Geometry geom) {
		if (geom.getGeometryType().equals("GeometryCollection")) {
			for (int i = 0; i < geom.getNumGeometries(); i++) {
				if (willDraw(geom.getGeometryN(i))) {
					return true;
				}
			}
			return false;
		} else {
			return willDrawSimpleGeometry(geom);
		}
	}

	protected abstract boolean willDrawSimpleGeometry(Geometry geom);
}