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

public class ConstraintFactory {

	public static Constraint createConstraint(int type, byte[] constraintBytes) {
		Constraint c;
		switch (type) {
		case Constraint.AUTO_INCREMENT:
			c = new AutoIncrementConstraint(constraintBytes);
			break;
		case Constraint.CRS:
			c = new CRSConstraint(constraintBytes);
			break;
		case Constraint.GEOMETRY_DIMENSION:
			c = new DimensionConstraint(constraintBytes);
			break;
		case Constraint.GEOMETRY_TYPE:
			c = new GeometryConstraint(constraintBytes);
			break;
		case Constraint.LENGTH:
			c = new LengthConstraint(constraintBytes);
			break;
		case Constraint.MAX:
			c = new MaxConstraint(constraintBytes);
			break;
		case Constraint.MIN:
			c = new MinConstraint(constraintBytes);
			break;
		case Constraint.NOT_NULL:
			c = new NotNullConstraint(constraintBytes);
			break;
		case Constraint.PATTERN:
			c = new PatternConstraint(constraintBytes);
			break;
		case Constraint.PK:
			c = new PrimaryKeyConstraint(constraintBytes);
			break;
		case Constraint.PRECISION:
			c = new PrecisionConstraint(constraintBytes);
			break;
		case Constraint.RASTER_TYPE:
			c = new RasterTypeConstraint(constraintBytes);
			break;
		case Constraint.READONLY:
			c = new ReadOnlyConstraint(constraintBytes);
			break;
		case Constraint.SCALE:
			c = new ScaleConstraint(constraintBytes);
			break;
		case Constraint.UNIQUE:
			c = new UniqueConstraint(constraintBytes);
			break;
		default:
			throw new IllegalArgumentException("Unknown constraint type:"
					+ type);
		}

		return c;
	}

}
