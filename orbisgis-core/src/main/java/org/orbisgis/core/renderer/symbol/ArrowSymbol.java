package org.orbisgis.core.renderer.symbol;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.Map;

import org.gdms.data.types.GeometryConstraint;
import org.gdms.driver.DriverException;
import org.orbisgis.core.renderer.RenderPermission;
import org.orbisgis.core.renderer.liteShape.LiteShape;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;

public class ArrowSymbol extends AbstractPointSymbol implements
		StandardPointSymbol {

	private int arrowLength;

	public ArrowSymbol(int arrowSize, int arrowLength, Color fillColor,
			Color outline, int lineWidth) {
		super(outline, lineWidth, fillColor, arrowSize, false);
		this.arrowLength = arrowLength;
	}

	@Override
	public boolean acceptGeometry(Geometry geom) {
		return (geom instanceof MultiPoint) || (geom instanceof LineString)
				|| (geom instanceof MultiLineString);
	}

	@Override
	public boolean acceptGeometryType(GeometryConstraint geometryConstraint) {
		if (geometryConstraint == null) {
			return true;
		} else {
			int geometryType = geometryConstraint.getGeometryType();
			return (geometryType == GeometryConstraint.MULTI_POINT)
					|| (geometryType == GeometryConstraint.LINESTRING)
					|| (geometryType == GeometryConstraint.MULTI_LINESTRING);
		}
	}

	@Override
	public Symbol cloneSymbol() {
		return new ArrowSymbol(size, arrowLength, fillColor, outline, lineWidth);
	}

	@Override
	public Symbol deriveSymbol(Color color) {
		return new ArrowSymbol(size, arrowLength, color, outline, lineWidth);
	}

	@Override
	public Envelope draw(Graphics2D g, Geometry geom, AffineTransform at,
			RenderPermission permission) throws DriverException {
		LiteShape ls = new LiteShape(geom, at, true);
		g.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND));
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
							current.y, size, arrowLength, fillColor,
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

	public int getArrowLength() {
		return arrowLength;
	}

	public void setArrowLength(int arrowLength) {
		this.arrowLength = arrowLength;
	}

	public Map<String, String> getPersistentProperties() {
		Map<String, String> ret = super.getPersistentProperties();
		ret.put("arrow-length", Integer.toString(arrowLength));
		return ret;
	}

	@Override
	public void setPersistentProperties(Map<String, String> props) {
		super.setPersistentProperties(props);
		arrowLength = Integer.parseInt(props.get("arrow-length"));
	}
}
