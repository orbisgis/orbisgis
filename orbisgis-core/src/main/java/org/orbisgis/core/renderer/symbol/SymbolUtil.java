package org.orbisgis.core.renderer.symbol;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import org.orbisgis.core.Services;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.ui.util.GUIUtil;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;

public class SymbolUtil {

	/**
	 * Smart enough to not fill LineStrings.
	 */
	public static void paint(Geometry geometry, Graphics2D g, MapTransform mt,
			boolean renderingFill, Stroke fillStroke, Paint fillPaint,
			boolean renderingLine, Stroke lineStroke, Color lineColor) {
		if (geometry instanceof GeometryCollection) {
			paintGeometryCollection((GeometryCollection) geometry, g, mt,
					renderingFill, fillStroke, fillPaint, renderingLine,
					lineStroke, lineColor);

			return;
		}

		Shape shape = mt.getShapeWriter().toShape(geometry);
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
			Graphics2D g, MapTransform mt, boolean renderingFill,
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
			paint(collection.getGeometryN(i), g, mt, renderingFill, fillStroke,
					fillPaint, renderingLine, lineStroke, lineColor);
		}
	}

	/**
	 * @param millisecondDelay
	 *            the GUI will be unresponsive for this length of time, so keep
	 *            it short!
	 */
	public static void flash(final Shape shape, final Graphics2D graphics,
			Color color, Stroke stroke, final int millisecondDelay) {
		graphics.setColor(color);
		graphics.setXORMode(Color.white);
		graphics.setStroke(stroke);

		try {
			GUIUtil.invokeOnEventThread(new Runnable() {
				public void run() {
					try {
						graphics.draw(shape);

						// Use sleep rather than Timer (which could allow a
						// third party to paint
						// the panel between my XOR draws, messing up the XOR).
						// Hopefully the user
						// won't Alt-Tab away and back! [Jon Aquino]
						Thread.sleep(millisecondDelay);
						graphics.draw(shape);
					} catch (Throwable t) {
						Services.getErrorManager().error(
								"Cannot draw the shape", t);
					}
				}
			});
		} catch (Throwable t) {
			Services.getErrorManager().error("Cannot draw the shape", t);
		}
	}

	public static void flash(final Geometry geometry, Graphics2D graphics,
			MapTransform mt, final int millisecondDelay) {
		flash(mt.getShapeWriter().toShape(geometry), graphics, Color.red,
				new BasicStroke(5, BasicStroke.CAP_ROUND,
						BasicStroke.JOIN_ROUND), millisecondDelay);
	}

	public static void flash(final Geometry geometry, Graphics2D graphics,
			MapTransform mt) {
		flash(mt.getShapeWriter().toShape(geometry), graphics, Color.red,
				new BasicStroke(5, BasicStroke.CAP_ROUND,
						BasicStroke.JOIN_ROUND), 100);
	}

	public static void flashPoint(final Geometry geometry, Graphics2D graphics,
			MapTransform mt) {

		Point2D p = new Point2D.Double(geometry.getCoordinate().x, geometry
				.getCoordinate().y);
		p = mt.getAffineTransform().transform(p, null);

		int IND_CIRCLE_RADIUS = 20;

		Ellipse2D.Double circle = new Ellipse2D.Double(p.getX()
				- (IND_CIRCLE_RADIUS / 2), p.getY() - (IND_CIRCLE_RADIUS / 2),
				IND_CIRCLE_RADIUS, IND_CIRCLE_RADIUS);

		flash(circle, graphics, Color.red, new BasicStroke(1,
				BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND), 200);
	}
}
