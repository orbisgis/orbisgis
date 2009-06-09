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
import java.util.HashMap;
import java.util.Map;

import org.gdms.data.types.GeometryConstraint;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;

public abstract class AbstractLineSymbol extends AbstractGeometrySymbol
		implements StandardLineSymbol {

	protected Color outline;
	protected int lineWidth;

	public AbstractLineSymbol(Color outline, int lineWidth) {
		this.outline = outline;
		this.lineWidth = lineWidth;
	}

	public boolean willDrawSimpleGeometry(Geometry geom) {
		return geom instanceof LineString || geom instanceof MultiLineString;
	}

	public boolean acceptGeometryType(GeometryConstraint geometryConstraint) {
		if (geometryConstraint == null) {
			return true;
		} else {
			int geometryType = geometryConstraint.getGeometryType();
			return (geometryType == GeometryConstraint.LINESTRING)
					|| (geometryType == GeometryConstraint.MULTI_LINESTRING);
		}
	}

	public int getLineWidth() {
		return lineWidth;
	}

	public Color getOutlineColor() {
		return outline;
	}

	public void setLineWidth(int value) {
		lineWidth = value;
	}

	public void setOutlineColor(Color color) {
		outline = color;
	}

	public Map<String, String> getPersistentProperties() {
		HashMap<String, String> ret = new HashMap<String, String>();
		ret.putAll(super.getPersistentProperties());
		if (outline != null) {
			ret.put("outline-color", Integer.toString(outline.getRGB()));
		}
		ret.put("line-width", Integer.toString(lineWidth));

		return ret;
	}

	public void setPersistentProperties(Map<String, String> props)  {
		super.setPersistentProperties(props);
		String outlineColor = props.get("outline-color");
		if (outlineColor != null) {
			outline = new Color(Integer.parseInt(outlineColor), true);
		} else {
			outline = null;
		}
		lineWidth = Integer.parseInt(props.get("line-width"));
	}

}
