package org.orbisgis.geoview.renderer.legend;

import java.awt.Color;

import com.vividsolutions.jts.geom.Geometry;

public class CircleVertexSymbol extends CircleSymbol{

	public CircleVertexSymbol(Color outline, Color fillColor, int size) {
		super(outline, fillColor, size);
	}

	@Override
	public boolean willDrawSimpleGeometry(Geometry geom) {
		return true;
	}

}
