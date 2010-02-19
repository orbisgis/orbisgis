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
package org.gdms.data.values;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

import org.gdms.sql.strategies.IncompatibleTypesException;
import org.grap.model.GeoRaster;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Datatypes must implement this interface in order to the drivers to return
 * that datatype. The implementation can inherit from AbstractValue or must
 * implement equals and hashCode in the way explained at doEquals method javadoc
 */
public interface Value {
	/**
	 * @see com.hardcode.gdbms.engine.instruction.Operations#and(com.hardcode.gdbms.engine.values.value)
	 *      ;
	 */
	public Value and(Value value) throws IncompatibleTypesException;

	/**
	 * @see com.hardcode.gdbms.engine.instruction.Operations#or(com.hardcode.gdbms.engine.values.value)
	 *      ;
	 */
	public Value or(Value value) throws IncompatibleTypesException;

	/**
	 * @see com.hardcode.gdbms.engine.instruction.Operations#producto(com.hardcode.gdbms.engine.values.value)
	 *      ;
	 */
	public Value producto(Value value) throws IncompatibleTypesException;

	/**
	 * @see com.hardcode.gdbms.engine.instruction.Operations#suma(com.hardcode.gdbms.engine.values.value)
	 *      ;
	 */
	public Value suma(Value value) throws IncompatibleTypesException;

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws IncompatibleTypesException
	 *             DOCUMENT ME!
	 */
	public Value inversa() throws IncompatibleTypesException;

	/**
	 * @see org.gdms.sql.instruction.Operations#equals(org.gdms.data.values.Value)
	 */
	public Value equals(Value value) throws IncompatibleTypesException;

	/**
	 * @see org.gdms.sql.instruction.Operations#notEquals(org.gdms.data.values.Value)
	 */
	public Value notEquals(Value value) throws IncompatibleTypesException;

	/**
	 * @see org.gdms.sql.instruction.Operations#greater(org.gdms.data.values.Value)
	 */
	public Value greater(Value value) throws IncompatibleTypesException;

	/**
	 * @see org.gdms.sql.instruction.Operations#less(org.gdms.data.values.Value)
	 */
	public Value less(Value value) throws IncompatibleTypesException;

	/**
	 * @see org.gdms.sql.instruction.Operations#greaterEqual(org.gdms.data.values.Value)
	 */
	public Value greaterEqual(Value value) throws IncompatibleTypesException;

	/**
	 * @see org.gdms.sql.instruction.Operations#lessEqual(org.gdms.data.values.Value)
	 */
	public Value lessEqual(Value value) throws IncompatibleTypesException;

	/**
	 * @see org.gdms.data.values.Operations#like(org.gdms.data.values.Value)
	 */
	public Value like(Value value) throws IncompatibleTypesException;

	/**
	 * In order to index the tables equals and hashCode must be defined.
	 * AbstractValue overrides these methods by calling doEquals and doHashCode.
	 * Any Value must inherit from abstract Value or override those methods in
	 * the same way.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean doEquals(Object obj);

	/**
	 * The hashCode implementation. Every value with the same semantic
	 * information must return the same int
	 * 
	 * @return integer
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int doHashCode();

	/**
	 * Gets the string representation of the value as it is defined in the
	 * specified ValueWriter
	 * 
	 * @param writer
	 *            Specifies the string representation for the values
	 * 
	 * @return String
	 */
	public String getStringValue(ValueWriter writer);

	/**
	 * Gets the type of the value
	 * 
	 * @return integer
	 */
	public int getType();

	/**
	 * Gets this value represented as an array of bytes
	 * 
	 * @return
	 */
	public byte[] getBytes();

	/**
	 * @return true if this value is null, false otherwise
	 */
	public boolean isNull();

	/**
	 * @return this value if it is a binary value or it can be converted
	 * 
	 * @throws IncompatibleTypesException
	 *             if the value is not of the required type or cannot be
	 *             converted
	 */
	public byte[] getAsBinary() throws IncompatibleTypesException;

	/**
	 * @return this value if it is a boolean value or it can be converted
	 * 
	 * @throws IncompatibleTypesException
	 *             if the value is not of the required type or cannot be
	 *             converted
	 */
	public boolean getAsBoolean() throws IncompatibleTypesException;

	/**
	 * @return this value if it is a date value or it can be converted
	 * 
	 * @throws IncompatibleTypesException
	 *             if the value is not of the required type or cannot be
	 *             converted
	 */
	public Date getAsDate() throws IncompatibleTypesException;

	/**
	 * @return this value if it is a geometry value or it can be converted
	 * 
	 * @throws IncompatibleTypesException
	 *             if the value is not of the required type or cannot be
	 *             converted
	 */
	public Geometry getAsGeometry() throws IncompatibleTypesException;

	/**
	 * @return this value if it is a geometry value or it can be converted
	 * 
	 * @throws IncompatibleTypesException
	 *             if the value is not of the required type or cannot be
	 *             converted
	 */
	public GeoRaster getAsRaster();

	/**
	 * @return this value if it is a numeric value or it can be converted
	 * 
	 * @throws IncompatibleTypesException
	 *             if the value is not of the required type or cannot be
	 *             converted
	 */
	public double getAsDouble() throws IncompatibleTypesException;

	/**
	 * @return this value if it is a numeric value or it can be converted
	 * 
	 * @throws IncompatibleTypesException
	 *             if the value is not of the required type or cannot be
	 *             converted
	 */
	public float getAsFloat() throws IncompatibleTypesException;

	/**
	 * @return this value if it is a numeric value or it can be converted
	 * 
	 * @throws IncompatibleTypesException
	 *             if the value is not of the required type or cannot be
	 *             converted
	 */
	public long getAsLong() throws IncompatibleTypesException;

	/**
	 * @return this value if it is a numeric value or it can be converted
	 * 
	 * @throws IncompatibleTypesException
	 *             if the value is not of the required type or cannot be
	 *             converted
	 */
	public byte getAsByte() throws IncompatibleTypesException;

	/**
	 * @return this value if it is a numeric value or it can be converted
	 * 
	 * @throws IncompatibleTypesException
	 *             if the value is not of the required type or cannot be
	 *             converted
	 */
	public short getAsShort() throws IncompatibleTypesException;

	/**
	 * @return this value if it is a numeric value or it can be converted
	 * 
	 * @throws IncompatibleTypesException
	 *             if the value is not of the required type or cannot be
	 *             converted
	 */
	public int getAsInt() throws IncompatibleTypesException;

	/**
	 * @return this value if it is a string value or it can be converted
	 * 
	 * @throws IncompatibleTypesException
	 *             if the value is not of the required type or cannot be
	 *             converted
	 */
	public String getAsString() throws IncompatibleTypesException;

	/**
	 * @return this value if it is a timestamp value or it can be converted
	 * 
	 * @throws IncompatibleTypesException
	 *             if the value is not of the required type or cannot be
	 *             converted
	 */
	public Timestamp getAsTimestamp() throws IncompatibleTypesException;

	/**
	 * @return this value if it is a time value or it can be converted
	 * 
	 * @throws IncompatibleTypesException
	 *             if the value is not of the required type or cannot be
	 *             converted
	 */
	public Time getAsTime() throws IncompatibleTypesException;

	/**
	 * @return this value if it is a value collection
	 * 
	 * @throws IncompatibleTypesException
	 *             if the value is not of the required type or cannot be
	 *             converted
	 */
	public ValueCollection getAsValueCollection()
			throws IncompatibleTypesException;

	/**
	 * Tries to make a conversion to the specified type.
	 * 
	 * @param typeCode
	 * @return The converted type
	 * @throws IncompatibleTypesException
	 *             If the value cannot be converted
	 */
	public Value toType(int typeCode) throws IncompatibleTypesException;

}