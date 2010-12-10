/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.orbisgis.core.renderer.symbol;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.util.Map;

import org.gdms.data.types.GeometryConstraint;
import org.gdms.driver.DriverException;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.RenderContext;

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
	public Envelope draw(Graphics2D g, Geometry geom, MapTransform mt,
			RenderContext permission) throws DriverException {
		Shape ls = mt.getShape(geom);
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
							current.y, size, arrowLength, fillColor, outline);
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
