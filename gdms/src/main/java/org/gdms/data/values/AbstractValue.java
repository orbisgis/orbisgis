/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.gdms.data.values;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.grap.model.GeoRaster;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Parent wrapper. This class provides very basic implementation of the
 * operations that can be made with values. Most of the methods are supposed to
 * be overriden by children. If not, they will throw
 * <code>IncompatibleTypeException</code>s
 * 
 */
public abstract class AbstractValue implements Value {

	/**
	 * Compute a logical AND between this and the parameter value, if possible.
	 * Must be overriden by children.
	 * 
	 * @param value
	 * @return The result of the AND operation
	 * @throws IncompatibleTypesException
	 *             If the operation is not implemented. Default behaviour.
	 */
	public Value and(Value value) throws IncompatibleTypesException {
		throw new IncompatibleTypesException("Cannot operate with " + value
				+ " and " + this);
	}

	/**
	 * Compute a logical OR between this and the parameter value. Must be
	 * overriden by children.
	 * 
	 * @param value
	 * @return The result of the or operation.
	 * @throws IncompatibleTypesException
	 *             If the operation is not implemented or possible between these
	 *             two products.
	 */
	public Value or(Value value) throws IncompatibleTypesException {
		throw new IncompatibleTypesException("Cannot operate with " + value
				+ " and " + this);
	}

	/**
	 * Compute the product between this and the parameter value. Must be
	 * overriden by children.
	 * 
	 * @param value
	 * @return The result of the product.
	 * @throws IncompatibleTypesException
	 *             If the operation is not implemented or possible between these
	 *             two products.
	 */
	public Value multiply(Value value) throws IncompatibleTypesException {
		throw new IncompatibleTypesException("Cannot operate with " + value
				+ " and " + this);
	}

	/**
	 * Compute a sum between this and the parameter value. Must be overriden by
	 * children.
	 * 
	 * @param value
	 * @return The result of the sum.
	 * @throws IncompatibleTypesException
	 *             If the operation is not implemented or possible between these
	 *             two products.
	 */
	public Value sum(Value value) throws IncompatibleTypesException {
		throw new IncompatibleTypesException("Cannot operate with " + value
				+ " and " + this);
	}

	/**
	 * Compute the inverse of this value. Must be overriden by children.
	 * 
	 * @param value
	 * @return The result of the inverse operation.
	 * @throws IncompatibleTypesException
	 *             If the operation is not implemented.
	 */
	public Value inversa() throws IncompatibleTypesException {
		throw new IncompatibleTypesException(this
				+ " does not have inverse value");
	}

	/**
	 * Test if this and value are equal. Must be overriden by children.
	 * 
	 * @param value
	 * @return A Value which determines if this and value are equals, for
	 *         instance a BooleanValue.
	 * @throws IncompatibleTypesException
	 *             If the operation is not implemented or possible between these
	 *             two products.
	 */
	public Value equals(Value value) throws IncompatibleTypesException {
		throw new IncompatibleTypesException("Cannot operate with " + value
				+ " and " + this);
	}

	/**
	 * Test if this and value are not equals. Must be overriden by children.
	 * 
	 * @param value
	 * @return A Value which determines if this and value are not equals, for
	 *         instance a BooleanValue.
	 * @throws IncompatibleTypesException
	 *             If the operation is not implemented or possible between these
	 *             two products.
	 */
	public Value notEquals(Value value) throws IncompatibleTypesException {
		throw new IncompatibleTypesException("Cannot operate with " + value
				+ " and " + this);
	}

	/**
	 * Test if this is strictly greater than value. Must be overriden by
	 * children.
	 * 
	 * @param value
	 * @return A value (preferably a BooleanValue) which determines which one is
	 *         greater.
	 * @throws IncompatibleTypesException
	 *             If the operation is not implemented or possible between these
	 *             two products.
	 */
	public Value greater(Value value) throws IncompatibleTypesException {
		throw new IncompatibleTypesException("Cannot operate with " + value
				+ " and " + this);
	}

	/**
	 * Test if this is strictly smaller than value. Must be overriden by
	 * children.
	 * 
	 * @param
	 * @return A value (preferably a BooleanValue) which determines if this is
	 *         smmaller than value.
	 * @throws IncompatibleTypesException
	 *             If the operation is not implemented or possible between these
	 *             two products.
	 */
	public Value less(Value value) throws IncompatibleTypesException {
		throw new IncompatibleTypesException("Cannot operate with " + value
				+ " and " + this);
	}

	/**
	 * Test if this is greater or equal than value. Must be overriden by
	 * children.
	 * 
	 * @param value
	 * @return A value (preferably a BooleanValue) which determines which one is
	 *         greater.
	 * @throws IncompatibleTypesException
	 *             If the operation is not implemented or possible between these
	 *             two products.
	 */
	public Value greaterEqual(Value value) throws IncompatibleTypesException {
		throw new IncompatibleTypesException("Cannot operate with " + value
				+ " and " + this);
	}

	/**
	 * Test if this is smaller than or equal to value. Must be overriden by
	 * children.
	 * 
	 * @param value
	 * @return A value (preferably a BooleanValue) which determines which one is
	 *         smaller.
	 * @throws IncompatibleTypesException
	 *             If the operation is not implemented or possible between these
	 *             two products.
	 */
	public Value lessEqual(Value value) throws IncompatibleTypesException {
		throw new IncompatibleTypesException("Cannot operate with " + value
				+ " and " + this);
	}

	/**
	 * @see org.gdms.data.values.Operations#like(org.gdms.data.values.Value)
	 */
	public Value like(Value value) throws IncompatibleTypesException {
		throw new IncompatibleTypesException("Cannot operate with " + value
				+ " and " + this);
	}

	/**
	 * Check if this is equal to an arbitrary Object obj
	 * 
	 * @param obj
	 * @return true if so.
	 */
	public boolean doEquals(Object obj) {
		if (obj instanceof Value) {
			try {
				return ((BooleanValue) this.equals((Value) obj)).getValue();
			} catch (IncompatibleTypesException e) {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Check if this is equal to an arbitrary Object obj
	 * 
	 * @param obj
	 * @return true if so.
	 */
	public boolean equals(Object obj) {
		return doEquals(obj);
	}

	/**
	 * Compute a hash code for this value.
	 * 
	 * @return The hashcode as an int.
	 */
	public int hashCode() {
		return doHashCode();
	}

	/**
	 * return a representation of this Value as a String.
	 * 
	 * @return
	 */
	@Override
	public String toString() {
		return getStringValue(ValueWriter.internalValueWriter);
	}

	/**
	 * Return this value as an array of bytes, if possible. Must be overriden by
	 * children.
	 * 
	 * @return
	 * @throws IncompatibleTypesException
	 * 
	 */
	public byte[] getAsBinary() throws IncompatibleTypesException {
		if (isNull()) {
			return null;
		}
		throw new IncompatibleTypesException("This value is not binary: "
				+ toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
	}

	/**
	 * try to retrieve this Value as a boolean. Must be overriden by children,
	 * when such an operation is possible..
	 * 
	 * @return
	 * @throws IncompatibleTypesException
	 *             If the cast can't be made.
	 */
	public boolean getAsBoolean() throws IncompatibleTypesException {
		if (isNull()) {
			return false;
		}
		throw new IncompatibleTypesException("This value is not boolean: "
				+ toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
	}

	/**
	 * Try to retrieve this Value as a Byte. Must be overriden by children, when
	 * such an operation is possible..
	 * 
	 * @return
	 * @throws IncompatibleTypesException
	 *             If the cast can't be made.
	 */
	public byte getAsByte() throws IncompatibleTypesException {
		if (isNull()) {
			return 0;
		}
		throw new IncompatibleTypesException("This value is not byte: "
				+ toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
	}

	/**
	 * Try to retrieve this Value as a Date. Must be overriden by children, when
	 * such an operation is possible..
	 * 
	 * @return
	 * @throws IncompatibleTypesException
	 *             If the cast can't be made.
	 */
	public Date getAsDate() throws IncompatibleTypesException {
		if (isNull()) {
			return null;
		}
		throw new IncompatibleTypesException("This value is not date: "
				+ toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
	}

	/**
	 * Try to retrieve this Value as a Double. Must be overriden by children,
	 * when such an operation is possible..
	 * 
	 * @return
	 * @throws IncompatibleTypesException
	 *             If the cast can't be made.
	 */
	public double getAsDouble() throws IncompatibleTypesException {
		if (isNull()) {
			return 0;
		}
		throw new IncompatibleTypesException("This value is not double: "
				+ toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
	}

	/**
	 * Try to retrieve this Value as a Float. Must be overriden by children,
	 * when such an operation is possible..
	 * 
	 * @return
	 * @throws IncompatibleTypesException
	 *             If the cast can't be made.
	 */
	public float getAsFloat() throws IncompatibleTypesException {
		if (isNull()) {
			return 0;
		}
		throw new IncompatibleTypesException("This value is not float: "
				+ toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
	}

	/**
	 * Try to retrieve this Value as a Geometry. Must be overriden by children,
	 * when such an operation is possible..
	 * 
	 * @return
	 * @throws IncompatibleTypesException
	 *             If the cast can't be made.
	 */
	public Geometry getAsGeometry() throws IncompatibleTypesException {
		if (isNull()) {
			return null;
		}
		throw new IncompatibleTypesException("This value is not geometry: "
				+ toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
	}

	/**
	 * Try to retrieve this Value as a Raster. Must be overriden by children,
	 * when such an operation is possible..
	 * 
	 * @return
	 * @throws IncompatibleTypesException
	 *             If the cast can't be made.
	 */
	public GeoRaster getAsRaster() throws IncompatibleTypesException {
		if (isNull()) {
			return null;
		}
		throw new IncompatibleTypesException("This value is not a raster: "
				+ toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
	}

	/**
	 * Try to retrieve this Value as an int. Must be overriden by children, when
	 * such an operation is possible..
	 * 
	 * @return
	 * @throws IncompatibleTypesException
	 *             If the cast can't be made.
	 */
	public int getAsInt() throws IncompatibleTypesException {
		if (isNull()) {
			return 0;
		}
		throw new IncompatibleTypesException("This value is not integer: "
				+ toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
	}

	/**
	 * Try to retrieve this Value as a long. Must be overriden by children, when
	 * such an operation is possible..
	 * 
	 * @return
	 * @throws IncompatibleTypesException
	 *             If the cast can't be made.
	 */
	public long getAsLong() throws IncompatibleTypesException {
		if (isNull()) {
			return 0;
		}
		throw new IncompatibleTypesException("This value is not long: "
				+ toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
	}

	/**
	 * Try to retrieve this Value as a short. Must be overriden by children,
	 * when such an operation is possible..
	 * 
	 * @return
	 * @throws IncompatibleTypesException
	 *             If the cast can't be made.
	 */
	public short getAsShort() throws IncompatibleTypesException {
		if (isNull()) {
			return 0;
		}
		throw new IncompatibleTypesException("This value is not short: "
				+ toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
	}

	/**
	 * Try to retrieve this Value as a String. Must be overriden by children,
	 * when such an operation is possible..
	 * 
	 * @return
	 * @throws IncompatibleTypesException
	 *             If the cast can't be made.
	 */
	public String getAsString() throws IncompatibleTypesException {
		if (isNull()) {
			return null;
		}
		throw new IncompatibleTypesException("This value is not string: "
				+ toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
	}

	/**
	 * Try to retrieve this Value as a Time. Must be overriden by children, when
	 * such an operation is possible..
	 * 
	 * @return
	 * @throws IncompatibleTypesException
	 *             If the cast can't be made.
	 */
	public Time getAsTime() throws IncompatibleTypesException {
		if (isNull()) {
			return null;
		}
		throw new IncompatibleTypesException("This value is not time: "
				+ toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
	}

	/**
	 * Try to retrieve this Value as a TimeStamp. Must be overriden by children,
	 * when such an operation is possible..
	 * 
	 * @return
	 * @throws IncompatibleTypesException
	 *             If the cast can't be made.
	 */
	public Timestamp getAsTimestamp() throws IncompatibleTypesException {
		if (isNull()) {
			return null;
		}
		throw new IncompatibleTypesException("This value is not timestamp: "
				+ toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
	}

	/**
	 * Try to retrieve this Value as a ValueCollection. Must be overriden by
	 * children, when such an operation is possible..
	 * 
	 * @return
	 * @throws IncompatibleTypesException
	 *             If the cast can't be made.
	 */
	public ValueCollection getAsValueCollection()
			throws IncompatibleTypesException {
		if (isNull()) {
			return null;
		}
		throw new IncompatibleTypesException(
				"This value is not a value collection: " + toString() + "("
						+ TypeFactory.getTypeName(getType()) + ")");
	}

	/**
	 * Check if this Value is null
	 * 
	 * @return true if it is.
	 */
	public boolean isNull() {
		return false;
	}

	/**
	 * Try to cast this Value to the type identified by typeCode. Must be
	 * overriden by children.
	 * 
	 * @param typeCode
	 * @return The casted Value
	 * @throws IncompatibleTypesException
	 *             If the opoeration is not possible
	 */
	@Override
	public Value toType(int typeCode) throws IncompatibleTypesException {
		if (typeCode == getType()) {
			return this;
		} else if (typeCode == Type.STRING) {
			return ValueFactory.createValue(toString());
		} else {
			throw new IncompatibleTypesException("Cannot cast '" + toString()
					+ "' to type " + TypeFactory.getTypeName(typeCode));
		}
	}

}
