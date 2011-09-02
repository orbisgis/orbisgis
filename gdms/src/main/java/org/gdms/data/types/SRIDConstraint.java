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

import com.vividsolutions.jts.geom.Geometry;
import org.gdms.data.values.Value;

/**
 * Indicates that the field is part of the primary key
 *
 */
public class SRIDConstraint extends AbstractIntConstraint {

        public SRIDConstraint(int constraintValue) {
                super(constraintValue);
        }

        SRIDConstraint(byte[] constraintBytes) {
                super(constraintBytes);
        }

        @Override
	public int getConstraintCode() {
		return Constraint.SRID;
	}

        @Override
	public boolean allowsFieldRemoval() {
		return true;
	}

        @Override
        public String check(Value value) {
                if ((value.getType() & Type.GEOMETRY) != 0 || value.getType() == Type.RASTER) {
                        Geometry g = value.getAsGeometry();
                        if (g.getSRID() != constraintValue) {
                                return "Expected SRID " + constraintValue + ", found " + g.getSRID();
                        }
                        return null;
                }
                return "The value is not spatial!";
        }
}