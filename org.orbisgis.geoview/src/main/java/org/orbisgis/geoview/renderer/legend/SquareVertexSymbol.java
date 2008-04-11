package org.orbisgis.geoview.renderer.legend;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import org.gdms.driver.DriverException;
import org.orbisgis.geoview.renderer.RenderPermission;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public class SquareVertexSymbol extends SquareSymbol {

	public SquareVertexSymbol(Color outline, Color fillColor, int size) {
		super(outline, fillColor, size);
		
	}

	@Override
	public boolean willDrawSimpleGeometry(Geometry geom) {
		return true;
	}
	

}
