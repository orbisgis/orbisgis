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

/**
 * Constraint indicating the dimension of the geometry: 2D or 3D
 * 
 */
public class DimensionConstraint extends AbstractIntConstraint {

	/**
	 * The dimension of the coordinates in the geometries. 2 if the geometries
	 * will not contain a Z value and 3 otherwise.
	 * 
	 * @param constraintValue
	 */
	public DimensionConstraint(final int constraintValue) {
		super(constraintValue);
		if ((constraintValue < 2) || (constraintValue > 3)) {
			throw new IllegalArgumentException("Only 2 and 3 are allowed");
		}
	}

	public DimensionConstraint(byte[] constraintBytes) {
		super(constraintBytes);
	}

	public int getConstraintCode() {
		return Constraint.GEOMETRY_DIMENSION;
	}

	public String check(Value value) {
		if (!value.isNull()) {
			final Geometry geom = value.getAsGeometry();
			if ((getDimension(geom) == 2) && (constraintValue == 3)) {
				return "Invalid dimension. " + getDimensionDescription()
						+ " expected";
			}
		}
		return null;
	}

	private String getDimensionDescription() {
		return (constraintValue == 2) ? "2D" : "2,5D";
	}

	private int getDimension(Geometry geom) {
		return Double.isNaN(geom.getCoordinate().z) ? 2 : 3;
	}

	public int getDimension() {
		return constraintValue;
	}

	@Override
	public int getType() {
		return CONSTRAINT_TYPE_CHOICE;
	}

	@Override
	public int[] getChoiceCodes() throws UnsupportedOperationException {
		return new int[] { 2, 3 };
	}

	@Override
	public String[] getChoiceStrings() throws UnsupportedOperationException {
		return new String[] { "2D", "2,5D" };
	}

	@Override
	public String getConstraintHumanValue() {
		return getDimensionDescription();
	}
}