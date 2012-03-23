/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC, 
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
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
 * info@orbisgis.org
 */
package org.gdms.data.types;

import org.gdms.data.values.Value;

import com.vividsolutions.jts.geom.Geometry;
import org.gdms.geometryUtils.GeometryTypeUtil;

/**
 * Constraint indicating the dimension of the geometry: 2D or 3D. A Dimension3DConstraint
 * created with the {@code DIMENSION_2D} value will describe geometries that have 
 * x and y coordinates. Dimension3DConstraint created with the {@code DIMENSION_3D} 
 * value will describe geometries that have x, y and z coordinates. 
 * 
 */
public class Dimension3DConstraint extends AbstractIntConstraint {
        
        /**
         * Constant to use to build constraint for objects that have only x and y coordinates.
         */
        public static int DIMENSION_2D = 2;
        /**
         * Constant to use to build constraint for objects that have x, y and z coordinates.
         */
        public static int DIMENSION_3D = 3;

	/**
	 * The dimension of the coordinates in the geometries. 2 if the geometries
	 * will not contain a Z value and 3 otherwise.
	 * 
	 * @param constraintValue
	 */
	public Dimension3DConstraint(final int constraintValue) {
		super(constraintValue);
		if ((constraintValue < 2) || (constraintValue > 3)) {
			throw new IllegalArgumentException("Only 2 and 3 are allowed");
		}
	}

	Dimension3DConstraint(byte[] constraintBytes) {
		super(constraintBytes);
	}

        @Override
	public int getConstraintCode() {
		return Constraint.DIMENSION_3D_GEOMETRY;
	}

        @Override
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
		return GeometryTypeUtil.is25Geometry(geom) ? 3 : 2;
	}

	public int getDimension() {
		return constraintValue;
	}

	@Override
	public int getType() {
		return CONSTRAINT_TYPE_CHOICE;
	}

	@Override
	public int[] getChoiceCodes() {
		return new int[] { 2, 3 };
	}

	@Override
	public String[] getChoiceStrings() {
		return new String[] { "2D", "2,5D" };
	}

	@Override
	public String getConstraintHumanValue() {
		return getDimensionDescription();
	}
}