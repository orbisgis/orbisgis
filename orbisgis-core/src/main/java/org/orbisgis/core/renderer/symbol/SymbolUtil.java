/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.core.renderer.symbol;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.AllowAllRenderContext;
import org.orbisgis.core.renderer.RenderContext;
import org.orbisgis.core.ui.util.GUIUtil;

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

	/**
	 * Draw a symbol preview
	 * 
	 * @param g
	 * @param symbol
	 * @param width
	 * @param height
	 * @param simple
	 */
	public static void drawSymbolPreview(Graphics2D g, Symbol symbol,
			int width, int height, boolean simple) {

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		if (symbol == null) {
			return;
		}
		GeometryFactory gf = new GeometryFactory();
		Geometry geom = null;

		try {
			if (simple) {
				geom = getSimplePolygon(gf, width, height);
			} else {
				geom = getComplexPolygon(gf, width, height);
			}
			paintGeometry(g, geom, symbol);
			if (simple) {
				geom = getSimpleLine(gf, width, height);
			} else {
				geom = getComplexLine(gf, width, height);
			}
			paintGeometry(g, geom, symbol);
			geom = gf.createPoint(new Coordinate(width / 2, height / 2));
			paintGeometry(g, geom, symbol);
		} catch (DriverException e) {
			((Graphics2D) g).drawString("Cannot generate preview", 0, 0);
		} catch (NullPointerException e) {
			((Graphics2D) g).drawString("Cannot generate preview: ", 0, 0);
		}
	}

	private static Geometry getSimpleLine(GeometryFactory gf, int width,
			int height) {
		int widthUnit = width / 4;
		int heightUnit = height / 4;
		Coordinate[] coordsP = { new Coordinate(widthUnit, heightUnit),
				new Coordinate(3 * widthUnit, heightUnit),
				new Coordinate(3 * widthUnit, 3 * heightUnit),
				new Coordinate(widthUnit, 3 * heightUnit),
				new Coordinate(widthUnit, heightUnit) };
		CoordinateArraySequence seqP = new CoordinateArraySequence(coordsP);
		return gf.createPolygon(new LinearRing(seqP, gf), null);
	}

	private static Geometry getSimplePolygon(GeometryFactory gf, int width,
			int height) {
		return gf.createLineString(new Coordinate[] {
				new Coordinate(width / 4, height / 2),
				new Coordinate(3 * width / 4, height / 2) });
	}

	private static LineString getComplexLine(GeometryFactory gf, int width,
			int height) {
		int widthUnit = width / 4;
		int heightUnit = height / 4;
		return gf.createLineString(new Coordinate[] {
				new Coordinate(widthUnit, 3 * heightUnit),
				new Coordinate(1.5 * widthUnit, 2 * heightUnit),
				new Coordinate(2 * widthUnit, 3 * heightUnit),
				new Coordinate(3 * widthUnit, heightUnit) });
	}

	private static Geometry getComplexPolygon(GeometryFactory gf, int width,
			int height) {
		int widthUnit = width / 4;
		int heightUnit = height / 4;
		Coordinate[] coordsP = { new Coordinate(widthUnit, heightUnit),
				new Coordinate(3 * widthUnit, heightUnit),
				new Coordinate(widthUnit, 3 * heightUnit),
				new Coordinate(widthUnit, heightUnit) };
		CoordinateArraySequence seqP = new CoordinateArraySequence(coordsP);
		return gf.createPolygon(new LinearRing(seqP, gf), null);
	}

	private static void paintGeometry(Graphics g, Geometry geom, Symbol symbol)
			throws DriverException {
		RenderContext renderPermission = new AllowAllRenderContext();
		if (symbol.acceptGeometry(geom)) {
			Symbol sym = RenderUtils.buildSymbolToDraw(symbol, geom);
			sym
					.draw((Graphics2D) g, geom, new MapTransform(),
							renderPermission);
		}
	}

}
