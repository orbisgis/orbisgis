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

import ij.ImagePlus;

import java.util.HashMap;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class ConstraintFactory {

	private static HashMap<Integer, Constraint> samples = new HashMap<Integer, Constraint>();

	static {
		samples.put(Constraint.AUTO_INCREMENT, new AutoIncrementConstraint());
		samples.put(Constraint.CRS, new CRSConstraint(
				(CoordinateReferenceSystem) null));
		samples.put(Constraint.GEOMETRY_DIMENSION, new DimensionConstraint(2));
		samples.put(Constraint.GEOMETRY_TYPE, new GeometryConstraint(
				GeometryConstraint.LINESTRING));
		samples.put(Constraint.LENGTH, new LengthConstraint(3));
		samples.put(Constraint.MAX, new MaxConstraint(3));
		samples.put(Constraint.MIN, new MinConstraint(3));
		samples.put(Constraint.NOT_NULL, new NotNullConstraint());
		samples.put(Constraint.PATTERN, new PatternConstraint(""));
		samples.put(Constraint.PK, new PrimaryKeyConstraint());
		samples.put(Constraint.PRECISION, new PrecisionConstraint(2));
		samples.put(Constraint.RASTER_TYPE, new RasterTypeConstraint(
				ImagePlus.COLOR_256));
		samples.put(Constraint.READONLY, new ReadOnlyConstraint());
		samples.put(Constraint.SCALE, new ScaleConstraint(2));
		samples.put(Constraint.UNIQUE, new UniqueConstraint());
	}

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

	/**
	 * Gets the name of the constraint by its code
	 *
	 * @param constraintCode
	 * @return
	 */
	public static String getConstraintName(int constraintCode) {
		String c;
		switch (constraintCode) {
		case Constraint.AUTO_INCREMENT:
			c = "AutoIncrement";
			break;
		case Constraint.CRS:
			c = "CRS";
			break;
		case Constraint.GEOMETRY_DIMENSION:
			c = "Dimension";
			break;
		case Constraint.GEOMETRY_TYPE:
			c = "Geometry";
			break;
		case Constraint.LENGTH:
			c = "Length";
			break;
		case Constraint.MAX:
			c = "Max";
			break;
		case Constraint.MIN:
			c = "Min";
			break;
		case Constraint.NOT_NULL:
			c = "Not Null";
			break;
		case Constraint.PATTERN:
			c = "Pattern";
			break;
		case Constraint.PK:
			c = "Primary Key";
			break;
		case Constraint.PRECISION:
			c = "Precision";
			break;
		case Constraint.RASTER_TYPE:
			c = "Raster Type";
			break;
		case Constraint.READONLY:
			c = "Read Only";
			break;
		case Constraint.SCALE:
			c = "Scale";
			break;
		case Constraint.UNIQUE:
			c = "Unique";
			break;
		default:
			throw new IllegalArgumentException("Unknown constraint type:"
					+ constraintCode);
		}

		return c;
	}

	/**
	 * Gets the type of the specified constraints
	 *
	 * @param constraintCode
	 * @return
	 */
	public static int getType(int constraintCode) {
		Constraint sampleConstraint = samples.get(constraintCode);
		return sampleConstraint.getType();
	}

	/**
	 * Returns the available choices for the CONSTRAINT_TYPE_CHOICE constraint
	 * specified as argument.
	 *
	 * @return
	 * @throws UnsupportedOperationException
	 *             If the specified constraint is not of type
	 *             CONSTRAINT_TYPE_CHOICE
	 */
	public static String[] getChoiceStrings(int constraintCode) {
		Constraint sampleConstraint = samples.get(constraintCode);
		return sampleConstraint.getChoiceStrings();
	}

	/**
	 * Returns the code of the available choices for the specified
	 * CONSTRAINT_TYPE_CHOICE constraint.
	 *
	 * @return
	 * @throws UnsupportedOperationException
	 *             If the specified constraint is not of type
	 *             CONSTRAINT_TYPE_CHOICE
	 */
	public static int[] getChoiceCodes(int constraintCode) {
		Constraint sampleConstraint = samples.get(constraintCode);
		return sampleConstraint.getChoiceCodes();
	}

	/**
	 * Creates a constraint of the specified code with the specified value
	 *
	 * @param code
	 * @param i
	 * @return
	 * @throws IllegalArgumentException
	 *             If the specified constraint doesn't accepts ints
	 */
	public static Constraint createConstraint(int code, int i)
			throws IllegalArgumentException {
		switch (code) {
		case Constraint.CRS:
			throw new UnsupportedOperationException("Not implemented yet");
		case Constraint.GEOMETRY_DIMENSION:
			return new DimensionConstraint(i);
		case Constraint.GEOMETRY_TYPE:
			return new GeometryConstraint(i);
		case Constraint.LENGTH:
			return new LengthConstraint(i);
		case Constraint.MAX:
			return new MaxConstraint(i);
		case Constraint.MIN:
			return new MinConstraint(i);
		case Constraint.PRECISION:
			return new PrecisionConstraint(i);
		case Constraint.SCALE:
			return new ScaleConstraint(i);
		default:
			throw new IllegalArgumentException(
					"This constraint does not use int:" + code);
		}
	}

	public static Constraint createConstraint(int constraintCode, String value) {
		switch (constraintCode) {
		case Constraint.PATTERN:
			return new PatternConstraint(value);
		default:
			throw new IllegalArgumentException(
					"This constraint does not use string:"
							+ getConstraintName(constraintCode));
		}
	}

}
