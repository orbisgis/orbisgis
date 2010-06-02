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
package org.gdms.data.types;

import org.gdms.data.values.Value;

import com.vividsolutions.jts.geom.Geometry;

import fr.cts.crs.CoordinateReferenceSystem;

/**
 * Constraint indicating the coordinate reference system of a spatial type
 * 
 */
public class CRSConstraint extends AbstractIntConstraint {

	private CoordinateReferenceSystem crs;

	public CRSConstraint(final int srid) {
		super(srid);
	}

	public CRSConstraint(byte[] constraintBytes) {
		super(constraintBytes);
	}

	public CRSConstraint(int srid, CoordinateReferenceSystem crs) {
		super(srid);
		this.crs = crs;
	}

	public int getConstraintCode() {
		return Constraint.CRS;
	}

	public CoordinateReferenceSystem getCRS() {
		return crs;
	}

	public String check(Value value) {
		if (value.getType() == Type.GEOMETRY) {
			Geometry geom = value.getAsGeometry();
			if (geom != null) {
				if ((constraintValue != -1)
						&& (geom.getSRID() != constraintValue)) {
					return "Invalid srid in geometry. Expected :"
							+ constraintValue;
				}
			}
		} else if (value.getType() == Type.RASTER) {
			// TODO Validate raster CRS
		}
		return null;
	}
}