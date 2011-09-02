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


import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.GeometryDimensionConstraint;
import org.gdms.data.types.Type;

public abstract class AbstractPolygonSymbol extends AbstractLineSymbol
		implements StandardPolygonSymbol {

	protected Color fillColor;

	public AbstractPolygonSymbol(Color outline, int lineWidth, Color fillColor) {
		super(outline, lineWidth, false);
		this.fillColor = fillColor;
	}

        @Override
	public boolean willDrawSimpleGeometry(Geometry geom) {
		return geom instanceof Polygon || geom instanceof MultiPolygon;
	}

        @Override
	public boolean acceptGeometryType(Type geomType) {
		if (geomType == null || geomType.getTypeCode() == Type.NULL) {
			return true;
		} else {
			int geometryType = geomType.getTypeCode();
			boolean valid = geometryType == Type.POLYGON
					|| geometryType == Type.MULTIPOLYGON;
                        if(!valid && (geometryType == Type.GEOMETRY || geometryType == Type.GEOMETRYCOLLECTION)){
                                //We can still check the generic geometries
                                GeometryDimensionConstraint gdc = 
                                        (GeometryDimensionConstraint) geomType.getConstraint(Constraint.DIMENSION_2D_GEOMETRY);
                                valid = gdc.getDimension() == GeometryDimensionConstraint.DIMENSION_POLYGON;
                        }
                        return valid;
		}
	}

        @Override
	public Color getFillColor() {
		return fillColor;
	}

        @Override
	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
	}

        @Override
	public Map<String, String> getPersistentProperties() {
		HashMap<String, String> ret = new HashMap<String, String>();
		ret.putAll(super.getPersistentProperties());
		if (fillColor != null) {
			ret.put("fill-color", Integer.toString(fillColor.getRGB()));
		}

		return ret;
	}

	@Override
	public void setPersistentProperties(Map<String, String> props) {
		super.setPersistentProperties(props);
		String fill = props.get("fill-color");
		if (fill != null) {
			fillColor = new Color(Integer.parseInt(fill), true);
		} else {
			fillColor = null;
		}
	}
}
