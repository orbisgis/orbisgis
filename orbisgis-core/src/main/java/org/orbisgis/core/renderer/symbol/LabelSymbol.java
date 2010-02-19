/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.core.renderer.symbol;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

import org.gdms.data.types.GeometryConstraint;
import org.gdms.driver.DriverException;
import org.orbisgis.core.renderer.RenderPermission;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

public class LabelSymbol extends AbstractSymbol implements Symbol {

	private int fontSize;
	private String text;
	private boolean smartPlacing;

	LabelSymbol(String text, int fontSize, boolean smartPlacing) {
		this.text = text;
		this.fontSize = fontSize;
		this.smartPlacing = smartPlacing;
		setName("Label symbol");
	}

	public Envelope draw(Graphics2D g, Geometry geom, AffineTransform at,
			RenderPermission permission) throws DriverException {
		Font font = g.getFont();
		g.setFont(font.deriveFont((float) fontSize));
		FontMetrics metrics = g.getFontMetrics(g.getFont());
		// get the height of a line of text in this font and render context
		int hgt = metrics.getHeight();
		// get the advance of my text in this font and render context
		int adv = metrics.stringWidth(text);
		// calculate the size of a box to hold the text with some padding.
		Dimension size = new Dimension(adv + 2, hgt + 2);

		// Get text size in map units
		AffineTransform inv = new AffineTransform(at);
		try {
			inv.invert();
		} catch (NoninvertibleTransformException e) {
			throw new RuntimeException("bug!");
		}
		Point2D mapPoint = inv.transform(new Point2D.Double(0, 0), null);
		Point2D mapPoint2 = inv.transform(new Point2D.Double(size.width,
				size.height), null);
		double[] mapSize = new double[] {
				Math.abs(mapPoint2.getX() - mapPoint.getX()),
				Math.abs(mapPoint2.getY() - mapPoint.getY()) };
		double halfWidth = mapSize[0] / 2;
		double halfHeight = mapSize[1] / 2;

		// Obtain geometry to place label
		if (smartPlacing) {
			geom = permission.getValidGeometry(geom, Math.max(halfWidth,
					halfHeight));

			if (geom == null) {
				return null;

			}
		}
		Point interiorPoint = geom.getCentroid();
		Envelope textMapArea = new Envelope(interiorPoint.getX() - halfWidth,
				interiorPoint.getX() + halfWidth, interiorPoint.getY()
						- halfHeight, interiorPoint.getY() + halfHeight);
		if (!smartPlacing && !permission.canDraw(textMapArea)) {
			return null;
		}

		// Draw label
		Point2D p = new Point2D.Double(interiorPoint.getX(), interiorPoint
				.getY());
		p = at.transform(p, null);
		double width = size.getWidth();
		double x = p.getX() - width / 2;
		double height = size.getHeight();
		double y = p.getY() + height / 2;
		Envelope area = new Envelope(new Coordinate(x, y), new Coordinate(x
				+ width, y + height));
		if (permission.canDraw(area)) {
			g.setColor(Color.black);
			g.drawString(text, (int) x, (int) y);
		} else {
			area = null;
		}
		g.setFont(font);
		return textMapArea;
	}

	public boolean acceptGeometry(Geometry geom) {
		return true;
	}

	public String getId() {
		return "org.orbisgis.symbol.Label";
	}

	public Map<String, String> getPersistentProperties() {
		HashMap<String, String> ret = new HashMap<String, String>();
		ret.putAll(super.getPersistentProperties());
		ret.put("font-size", Integer.toString(fontSize));
		ret.put("smart-placing", Boolean.toString(smartPlacing));

		return ret;
	}

	public void setPersistentProperties(Map<String, String> props) {
		super.setPersistentProperties(props);
		fontSize = Integer.parseInt(props.get("font-size"));
		smartPlacing = Boolean.parseBoolean(props.get("smart-placing"));
	}

	public Symbol cloneSymbol() {
		return new LabelSymbol(text, fontSize, smartPlacing);
	}

	public String getClassName() {
		return "Label";
	}

	public boolean acceptGeometryType(GeometryConstraint geometryConstraint) {
		return true;
	}

	@Override
	public Symbol deriveSymbol(Color color) {
		return null;
	}
}
