/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer, Maxence LAURENT,
 * computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.data.types;

import com.vividsolutions.jts.geom.Geometry;

import org.gdms.data.values.Value;

/**
 * Constraint indicating the dimension of the geometry
 * 
 */
public class GeometryDimensionConstraint extends AbstractIntConstraint {

    /**
     * Constant to use to display human constraint name for unknown geometry.
     */
    public static final String HUMAN_DIMENSION_UNKNOWN = "UNKNOWN";
    /**
     * Constant to use to display human constraint name  for point or multipoint.
     */
    public static final String HUMAN_DIMENSION_POINT = "POINT";
    /**
     * Constant to use to display human constraint name  for line or multilinestring.
     */
    public static final String HUMAN_DIMENSION_CURVE = "CURVE";
    /**
     * Constant to use to display human constraint name for polygon or multipolygon.
     */
    public static final String HUMAN_DIMENSION_SURFACE = "SURFACE";
    /**
     * Constant to use to build constraint for unknown geometry.
     */
    public static final int DIMENSION_UNKNOWN = -1;
    /**
     * Constant to use to build constraint for point or multipoint.
     */
    public static final int DIMENSION_POINT = 0;
    /**
     * Constant to use to build constraint for line or multilinestring.
     */
    public static final int DIMENSION_CURVE = 1;
    /**
     * Constant to use to build constraint for polygon or multipolygon.
     */
    public static final int DIMENSION_SURFACE = 2;

    /**
     * The dimension of the  geometries. 
     * 0 = point (point, multipoint)
     * 1 = curve (linestring, multilinestring)
     * 2 = surface (polygon, multipolygon)
     *
     * @param constraintValue
     */
    public GeometryDimensionConstraint(final int constraintValue) {
        super(constraintValue);
        if ((constraintValue < DIMENSION_POINT) || (constraintValue > DIMENSION_SURFACE)) {
            throw new IllegalArgumentException("Only 0, 1 and 2 are allowed");
        }
    }

    GeometryDimensionConstraint(byte[] constraintBytes) {
        super(constraintBytes);
    }

    @Override
    public int getConstraintCode() {
        return Constraint.DIMENSION_2D_GEOMETRY;
    }

    @Override
    public String check(Value value) {
        if (!value.isNull()) {
            final Geometry geom = value.getAsGeometry();
            if ((getDimension(geom) != constraintValue)) {
                return "Invalid dimension. " + getDimensionDescription()
                        + " expected";
            }
        }
        return null;
    }

    private String getDimensionDescription() {
        if (constraintValue == DIMENSION_POINT) {
            return HUMAN_DIMENSION_POINT;
        } else if (constraintValue == DIMENSION_CURVE) {
            return HUMAN_DIMENSION_CURVE;
        } else {
            return HUMAN_DIMENSION_SURFACE;
        }
    }

    private int getDimension(Geometry geom) {
        return geom.getDimension();
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
        return new int[]{0, 1, 2};
    }

    @Override
    public String[] getChoiceStrings() {
        return new String[]{HUMAN_DIMENSION_POINT, HUMAN_DIMENSION_CURVE, HUMAN_DIMENSION_SURFACE};
    }

    @Override
    public String getConstraintHumanValue() {
        return getDimensionDescription();
    }
}
