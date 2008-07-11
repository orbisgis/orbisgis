package org.orbisgis.renderer.symbol;

import java.awt.Color;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

public class PolygonCentroidSquareSymbol extends SquarePointSymbol {

	public PolygonCentroidSquareSymbol(Color outline, int lineWidth, Color fillColor,
			int size) {
		super(outline, lineWidth, fillColor, size);
	}

	@Override
	public boolean willDrawSimpleGeometry(Geometry geom) {
		return geom instanceof Polygon || geom instanceof MultiPolygon;
	}

	@Override
	public String getId() {
		return "org.orbisgis.symbol.polygon.centroid.Square";
	}

}
