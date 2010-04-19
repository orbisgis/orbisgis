package org.orbisgis.core.renderer.symbol;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import org.orbisgis.core.renderer.liteShape.LiteShape;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;

public class SymbolUtil {

	/**
	 * Smart enough to not fill LineStrings.
	 */
	public static void paint(Geometry geometry, Graphics2D g,
			AffineTransform at, boolean renderingFill, Stroke fillStroke,
			Paint fillPaint, boolean renderingLine, Stroke lineStroke,
			Color lineColor) {
		if (geometry instanceof GeometryCollection) {
			paintGeometryCollection((GeometryCollection) geometry, g, at,
					renderingFill, fillStroke, fillPaint, renderingLine,
					lineStroke, lineColor);

			return;
		}

		Shape shape = new LiteShape(geometry, at, false);
		if (!(shape instanceof GeneralPath) && renderingFill) {
			g.setStroke(fillStroke);
			g.setPaint(fillPaint);
			g.fill(shape);
		}
		if (renderingLine) {
			g.setStroke(lineStroke);
			g.setColor(lineColor);
			g.draw(shape);
		}
	}

	private static void paintGeometryCollection(GeometryCollection collection,
			Graphics2D g, AffineTransform at, boolean renderingFill,
			Stroke fillStroke, Paint fillPaint, boolean renderingLine,
			Stroke lineStroke, Color lineColor) {
		// For GeometryCollections, render each element separately. Otherwise,
		// for example, if you pass in a GeometryCollection containing a ring
		// and a
		// disk, you cannot render them as such: if you use Graphics.fill,
		// you'll get
		// two disks, and if you use Graphics.draw, you'll get two rings. [Jon
		// Aquino]
		for (int i = 0; i < collection.getNumGeometries(); i++) {
			paint(collection.getGeometryN(i), g, at, renderingFill, fillStroke,
					fillPaint, renderingLine, lineStroke, lineColor);
		}
	}
}
