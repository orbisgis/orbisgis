package org.orbisgis.core.renderer.symbol;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.PathIterator;

import org.gdms.data.types.GeometryConstraint;
import org.gdms.driver.DriverException;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.RenderContext;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public class SelectionSymbol extends AbstractGeometrySymbol {

	private Stroke handleStroke = new BasicStroke(1);
	private Stroke lineStroke = new BasicStroke(2);
	private Stroke fillStroke = new BasicStroke(1);
	private Color handleFillColor;
	private Color lineColor;
	private Color fillColor;
	private boolean paintingHandles;
	private boolean filling;

	public SelectionSymbol(Color color, boolean paintingHandles, boolean filling) {
		handleFillColor = color;
		lineColor = color;
		fillColor = color;
		this.paintingHandles = paintingHandles;
		this.filling = filling;
	}

	@Override
	protected boolean willDrawSimpleGeometry(Geometry geom) {
		return true;
	}

	@Override
	public boolean acceptGeometryType(GeometryConstraint geometryConstraint) {
		return true;
	}

	@Override
	public Symbol cloneSymbol() {

		return null;
	}

	@Override
	public Symbol deriveSymbol(Color color) {

		return null;
	}

	@Override
	public Envelope draw(Graphics2D g, Geometry geom, MapTransform mt,
			RenderContext permission) throws DriverException {

		if ((geom.getDimension() == 1) || (!paintingHandles)) {
			SymbolUtil.paint(geom, g, mt, false, fillStroke, fillColor, true,
					lineStroke, lineColor);
		}

		else if ((geom.getDimension() == 2) || (!paintingHandles)) {
			SymbolUtil.paint(geom, g, mt, filling, fillStroke, new Color(
					fillColor.getRed(), fillColor.getGreen(), fillColor
							.getBlue(), 50), true, lineStroke, lineColor);
		}

		if (paintingHandles) {
			// LiteShape ls = new LiteShape(geom, at, false);
			Shape ls = mt.getShapeWriter().toShape(geom);
			PathIterator pi = ls.getPathIterator(null);
			double[] coords = new double[6];

			int drawingSize = 5;

			while (!pi.isDone()) {
				pi.currentSegment(coords);
				paintSquare(g, (int) coords[0], (int) coords[1], drawingSize);
				pi.next();
			}
		}
		return null;
	}

	protected void paintSquare(Graphics2D g, int x, int y, int size) {
		x = x - size / 2;
		y = y - size / 2;
		if (fillColor != null) {
			g.setPaint(new Color(fillColor.getRed(), fillColor.getGreen(),
					fillColor.getBlue(), 50));
			g.fillRect(x, y, size, size);
		}
		if (lineColor != null) {
			g.setStroke(new BasicStroke(1));
			g.setColor(lineColor);
			g.drawRect(x, y, size, size);
		}
	}

	@Override
	public String getClassName() {
		return "Selection";
	}

	@Override
	public String getId() {
		return "org.orbisgis.symbol.SelectionSymbol";
	}

}
