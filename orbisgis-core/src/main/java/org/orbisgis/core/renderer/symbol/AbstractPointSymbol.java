/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.HashMap;
import java.util.Map;


import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import org.gdms.data.types.Type;

public abstract class AbstractPointSymbol extends AbstractPolygonSymbol
		implements StandardPointSymbol {

	protected int size;
	protected boolean mapUnits;

	public AbstractPointSymbol(Color outline, int lineWidth, Color fillColor,
			int size, boolean mapUnits) {
		super(outline, lineWidth, fillColor);
		this.size = size;
		this.mapUnits = mapUnits;
	}

	public boolean willDrawSimpleGeometry(Geometry geom) {
		return geom instanceof Point || geom instanceof MultiPoint;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int value) {
		this.size = value;
	}

	public Color getFillColor() {
		return fillColor;
	}

	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
	}

	public boolean acceptGeometryType(Type geometrytype) {
		if (geometrytype== null || geometrytype.getTypeCode() == Type.NULL) {
			return false;
		} else {
			int geometryType = geometrytype.getTypeCode();
			return (geometryType == Type.POINT)
					|| (geometryType == geometrytype.MULTIPOINT);
		}
	}

	public Map<String, String> getPersistentProperties() {
		HashMap<String, String> ret = new HashMap<String, String>();
		ret.putAll(super.getPersistentProperties());
		ret.put("size", Integer.toString(size));
		ret.put("map-units", Boolean.toString(mapUnits));

		return ret;
	}

	protected double toPixelUnits(int size, AffineTransform at)
			throws NoninvertibleTransformException {
		double ret = at.getScaleX() * size;
		return ret;
	}

	@Override
	public void setPersistentProperties(Map<String, String> props) {
		super.setPersistentProperties(props);
		size = Integer.parseInt(props.get("size"));
		String mapUnitsProp = props.get("map-units");
		if (mapUnitsProp == null) {
			mapUnits = false;
		} else {
			mapUnits = Boolean.parseBoolean(mapUnitsProp);
		}
	}

	public boolean isMapUnits() {
		return mapUnits;
	}

	public void setMapUnits(boolean mapUnits) {
		this.mapUnits = mapUnits;
	}
}