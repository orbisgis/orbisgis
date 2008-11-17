package org.orbisgis.renderer.symbol;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;

import org.gdms.data.types.GeometryConstraint;
import org.gdms.driver.DriverException;
import org.orbisgis.renderer.RenderPermission;
import org.orbisgis.renderer.liteShape.LiteShape;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPoint;

public class ArrowSymbol extends AbstractLineSymbol {

	private int arrowWidth;
	private int arrowLength;
	private Color fillColor;

	public ArrowSymbol(int arrowWidth, int arrowLength, Color fillColor,
			Color outline, int lineWidth) {
		super(outline, lineWidth);
		this.arrowWidth = arrowWidth;
		this.arrowLength = arrowLength;
		this.fillColor = fillColor;
	}

	@Override
	public boolean acceptGeometry(Geometry geom) {
		return (geom instanceof MultiPoint) || super.acceptGeometry(geom);
	}

	@Override
	public boolean acceptGeometryType(GeometryConstraint geometryConstraint) {
		if (super.acceptGeometryType(geometryConstraint)) {
			return true;
		} else {
			int geometryType = geometryConstraint.getGeometryType();
			return (geometryType == GeometryConstraint.MULTI_POINT);
		}
	}

	@Override
	public Symbol cloneSymbol() {
		return new ArrowSymbol(arrowWidth, arrowLength, fillColor, outline,
				lineWidth);
	}

	@Override
	public Symbol deriveSymbol(Color color) {
		return new ArrowSymbol(arrowWidth, arrowLength, fillColor, color,
				lineWidth);
	}

	@Override
	public Envelope draw(Graphics2D g, Geometry geom, AffineTransform at,
			RenderPermission permission) throws DriverException {
		LiteShape ls = new LiteShape(geom, at, true);
		g.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND));
		g.setColor(outline);
		g.setPaint(null);

		boolean isMultipoint = geom instanceof MultiPoint;
		PathIterator pi = ls.getPathIterator(null);
		double[] coords = new double[6];
		Point lastPos = null;
		while (!pi.isDone()) {
			int type = pi.currentSegment(coords);
			Point current = new Point((int) coords[0], (int) coords[1]);
			if ((type == PathIterator.SEG_LINETO) || isMultipoint) {
				if ((lastPos != null) && !lastPos.equals(current)) {
					GraphicsUtils.drawArrow(g, lastPos.x, lastPos.y, current.x,
							current.y, arrowWidth, arrowLength, fillColor,
							outline);
				}
			}
			lastPos = current;
			pi.next();
		}

		return null;
	}

	@Override
	public String getClassName() {
		return "Arrow";
	}

	@Override
	public String getId() {
		return "org.orbisgis.symbols.Arrow";
	}

	public int getArrowWidth() {
		return arrowWidth;
	}

	public void setArrowWidth(int arrowWidth) {
		this.arrowWidth = arrowWidth;
	}

	public int getArrowLength() {
		return arrowLength;
	}

	public void setArrowLength(int arrowLength) {
		this.arrowLength = arrowLength;
	}
}
