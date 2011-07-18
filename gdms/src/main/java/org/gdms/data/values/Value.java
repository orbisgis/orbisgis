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
package org.gdms.data.values;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

import org.gdms.data.types.IncompatibleTypesException;
import org.grap.model.GeoRaster;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Datatypes must implement this interface in order to the drivers to return
 * that datatype. The implementation can inherit from AbstractValue or must
 * implement equals and hashCode in the way explained at doEquals method javadoc
 */
public interface Value extends Comparable<Value> {

        /**
         * Gets a boolean value representing the SQL AND operation between this value and the <code>value</code>
         * parameter using three-valued logic (3VL).
         * @param value a value
         * @return a value containing either TRUE, FALSE or NULL (=UNKNOWN)
         */
        BooleanValue and(Value value);

        /**
         * Gets a boolean value representing the SQL OR operation between this value and the <code>value</code>
         * parameter using three-valued logic (3VL).
         * @param value a value
         * @return a value containing either TRUE, FALSE or NULL (=UNKNOWN)
         */
	BooleanValue or(Value value);

        /**
         * Gets a numeric value containing the product of this value and the <code>value</code> parameter.
         * @param value a value
         * @return the product of <code>this</code> and <code>value</code>
         */
	NumericValue multiply(Value value);

        /**
         * Gets a numeric value containing the sum of this value and the <code>value</code> parameter.
         * @param value a value
         * @return the sum of <code>this</code> and <code>value</code>
         */
	NumericValue sum(Value value);

	/**
	 * Reverses the current Value, or if
         * not possible.
	 * 
	 * @return the inversed value
	 * 
	 * @throws IncompatibleTypesException
	 *             if inversing the Value has no meaning.
	 */
	Value inverse();

        /**
         * Gets a boolean value representing the SQL equality between this value and the <code>value</code>
         * parameter using three-valued logic (3VL).
         * @param value a value
         * @return a value containing either TRUE, FALSE or NULL (=UNKNOWN)
         */
	BooleanValue equals(Value value);

        /**
         * Gets a boolean value representing the SQL non-equality between this value and the <code>value</code>
         * parameter using three-valued logic (3VL).
         * 
         * This is always strictly equivalent to calling <code>equals(value).not()</code>.
         * 
         * @param value a value
         * @return a value containing either TRUE, FALSE or NULL (=UNKNOWN)
         */
	BooleanValue notEquals(Value value);

	BooleanValue greater(Value value);

	BooleanValue less(Value value);

	BooleanValue greaterEqual(Value value);

	BooleanValue lessEqual(Value value);

	BooleanValue like(Value value);

	/**
	 * Gets the string representation of the value as it is defined in the
	 * specified ValueWriter
	 * 
	 * @param writer
	 *            Specifies the string representation for the values
	 * 
	 * @return String
	 */
	String getStringValue(ValueWriter writer);

	/**
	 * Gets the type of the value
	 * 
	 * @return integer
	 */
	int getType();

	/**
	 * Gets this value represented as an array of bytes
	 * 
	 * @return
	 */
	byte[] getBytes();

	/**
	 * @return true if this value is null, false otherwise
	 */
	boolean isNull();

	/**
	 * @return this value if it is a binary value or it can be converted
	 * 
	 * @throws IncompatibleTypesException
	 *             if the value is not of the required type or cannot be
	 *             converted
	 */
	byte[] getAsBinary();

	/**
	 * @return the boolean value inside this Value
         * This methods return <tt>true</tt> if the value is SQL TRUE,
         * <tt>false</tt> if the value is SQL FALSE, and <tt>null</tt> if
         * this value is SQL UNKNOWN.
         * Note that is the latter case, we still have <code>isNull() == false</code>.
	 * 
	 * @throws IncompatibleTypesException
	 *             if the value is not of the required type or cannot be
	 *             converted
	 */
	Boolean getAsBoolean();

	/**
	 * @return this value if it is a date value or it can be converted
	 * 
	 * @throws IncompatibleTypesException
	 *             if the value is not of the required type or cannot be
	 *             converted
	 */
	Date getAsDate();

	/**
	 * @return this value if it is a geometry value or it can be converted
	 * 
	 * @throws IncompatibleTypesException
	 *             if the value is not of the required type or cannot be
	 *             converted
	 */
	Geometry getAsGeometry();

	/**
	 * @return this value if it is a raster value or it can be converted
	 * 
	 * @throws IncompatibleTypesException
	 *             if the value is not of the required type or cannot be
	 *             converted
	 */
	GeoRaster getAsRaster();

	/**
	 * @return this value if it is a numeric value or it can be converted
	 * 
	 * @throws IncompatibleTypesException
	 *             if the value is not of the required type or cannot be
	 *             converted
	 */
	double getAsDouble();

	/**
	 * @return this value if it is a numeric value or it can be converted
	 * 
	 * @throws IncompatibleTypesException
	 *             if the value is not of the required type or cannot be
	 *             converted
	 */
	float getAsFloat();

	/**
	 * @return this value if it is a numeric value or it can be converted
	 * 
	 * @throws IncompatibleTypesException
	 *             if the value is not of the required type or cannot be
	 *             converted
	 */
	long getAsLong();

	/**
	 * @return this value if it is a numeric value or it can be converted
	 * 
	 * @throws IncompatibleTypesException
	 *             if the value is not of the required type or cannot be
	 *             converted
	 */
	byte getAsByte();

	/**
	 * @return this value if it is a numeric value or it can be converted
	 * 
	 * @throws IncompatibleTypesException
	 *             if the value is not of the required type or cannot be
	 *             converted
	 */
	short getAsShort();

	/**
	 * @return this value if it is a numeric value or it can be converted
	 * 
	 * @throws IncompatibleTypesException
	 *             if the value is not of the required type or cannot be
	 *             converted
	 */
	int getAsInt();

	/**
	 * @return this value if it is a string value or it can be converted
	 * 
	 * @throws IncompatibleTypesException
	 *             if the value is not of the required type or cannot be
	 *             converted
	 */
	String getAsString();

	/**
	 * @return this value if it is a timestamp value or it can be converted
	 * 
	 * @throws IncompatibleTypesException
	 *             if the value is not of the required type or cannot be
	 *             converted
	 */
	Timestamp getAsTimestamp();

	/**
	 * @return this value if it is a time value or it can be converted
	 * 
	 * @throws IncompatibleTypesException
	 *             if the value is not of the required type or cannot be
	 *             converted
	 */
	Time getAsTime();

	/**
	 * @return this value if it is a value collection
	 * 
	 * @throws IncompatibleTypesException
	 *             if the value is not of the required type or cannot be
	 *             converted
	 */
	ValueCollection getAsValueCollection();

	/**
	 * Tries to make a conversion to the specified type.
	 * 
	 * @param typeCode
	 * @return The converted type
	 * @throws IncompatibleTypesException
	 *             If the value cannot be converted
	 */
	Value toType(int typeCode);

        /**
         * Gets a boolean value representing the SQL boolean NOT of this value using three-valued logic (3VL).
         * @return a value containing either TRUE, FALSE or NULL (=UNKNOWN)
         */
        BooleanValue not();

        NumericValue opposite();

        StringValue concatWith(Value value);

        /**
         * Takes the remainder of the division of this Value by the argument value,
         * i.e. the result of 24 % 10 is 4
         *
         * @param value
         * @return The remainder.
         * @throws IncompatibleTypesException
         * If the operation is not implemented or possible between these
         * two products.
         */
        NumericValue remainder(Value value);

        /**
         * Returns a double value representing the value of this number raised
         * to the power of the argument value.
         *
         * @param value
         * @return The double result of the operation "this ^ value".
         * @throws IncompatibleTypesException
         * If the operation is not implemented or possible between these
         * two products.
         */
        DoubleValue pow(Value value);

}