/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
 * or contact directly:
 * info_at_ orbisgis.org
 */

package org.orbisgis.sif.docking;

/**
 * Limited API for bibliothek.util.xml.XElement
 * @author Benjamin Sigg
 * @author Nicolas Fortin
 */
public interface XElement {

        /**
         * Gets the name of this attribute.
         * @return the name
         */
        public String getName();

        /**
         * Gets all children of this entry.
         * @return the children
         */
        public XElement[] children();

        /**
         * Adds a new attribute to this entry.
         * @param name the name of the attribute
         * @param value the value of the attribute
         * @return <code>this</code>
         */
        public XElement addByte( String name, byte value );

        /**
         * Adds a new attribute to this entry.
         * @param name the name of the attribute
         * @param value the value of the attribute
         * @return <code>this</code>
         */
        public XElement addShort( String name, short value );

        /**
         * Adds a new attribute to this entry.
         * @param name the name of the attribute
         * @param value the value of the attribute
         * @return <code>this</code>
         */
        public XElement addInt( String name, int value );

        /**
         * Adds a new attribute to this entry.
         * @param name the name of the attribute
         * @param value the value of the attribute
         * @return <code>this</code>
         */
        public XElement addLong( String name, long value );

        /**
         * Adds a new attribute to this entry.
         * @param name the name of the attribute
         * @param value the value of the attribute
         * @return <code>this</code>
         */
        public XElement addFloat( String name, float value );

        /**
         * Adds a new attribute to this entry.
         * @param name the name of the attribute
         * @param value the value of the attribute
         * @return <code>this</code>
         */
        public XElement addDouble( String name, double value );

        /**
         * Adds a new attribute to this entry.
         * @param name the name of the attribute
         * @param value the value of the attribute
         * @return <code>this</code>
         */
        public XElement addChar( String name, char value );

        /**
         * Adds a new attribute to this entry.
         * @param name the name of the attribute
         * @param value the value of the attribute
         * @return <code>this</code>
         */
        public XElement addString( String name, String value );

        /**
         * Adds a new attribute to this entry.
         * @param name the name of the attribute
         * @param value the value of the attribute
         * @return <code>this</code>
         */
        public XElement addBoolean( String name, boolean value );

        /**
         * Adds a new attribute to this entry.
         * @param name the name of the attribute
         * @param value the value of the attribute
         * @return <code>this</code>
         */
        public XElement addByteArray( String name, byte[] value );

        /**
         * Tells whether the attribute <code>name</code> exists.
         * @param name the name to search
         * @return <code>true</code> if such an attribute exists
         */
        public boolean attributeExists( String name );

        /**
         * Gets the value of an attribute.
         * @param name the name of the attribute
         * @return the value of the attribute
         * @throws RuntimeException if the attribute does not exist or if the value
         * is in the wrong format
         */
        public byte getByte( String name );

        /**
         * Gets the value of an attribute.
         * @param name the name of the attribute
         * @return the value of the attribute
         * @throws RuntimeException if the attribute does not exist or if the value
         * is in the wrong format
         */
        public short getShort( String name );

        /**
         * Gets the value of an attribute.
         * @param name the name of the attribute
         * @return the value of the attribute
         * @throws RuntimeException if the attribute does not exist or if the value
         * is in the wrong format
         */
        public int getInt( String name );

        /**
         * Gets the value of an attribute.
         * @param name the name of the attribute
         * @return the value of the attribute
         * @throws RuntimeException if the attribute does not exist or if the value
         * is in the wrong format
         */
        public long getLong( String name );

        /**
         * Gets the value of an attribute.
         * @param name the name of the attribute
         * @return the value of the attribute
         * @throws RuntimeException if the attribute does not exist or if the value
         * is in the wrong format
         */
        public float getFloat( String name );

        /**
         * Gets the value of an attribute.
         * @param name the name of the attribute
         * @return the value of the attribute
         * @throws RuntimeException if the attribute does not exist or if the value
         * is in the wrong format
         */
        public double getDouble( String name );

        /**
         * Gets the value of an attribute.
         * @param name the name of the attribute
         * @return the value of the attribute
         * @throws RuntimeException if the attribute does not exist or if the value
         * is in the wrong format
         */
        public char getChar( String name );

        /**
         * Gets the value of an attribute.
         * @param name the name of the attribute
         * @return the value of the attribute
         * @throws RuntimeException if the attribute does not exist or if the value
         * is in the wrong format
         */
        public String getString( String name );

        /**
         * Gets the value of an attribute.
         * @param name the name of the attribute
         * @return the value of the attribute
         * @throws RuntimeException if the attribute does not exist or if the value
         * is in the wrong format
         */
        public boolean getBoolean( String name );

        /**
         * Gets the value of an attribute.
         * @param name the name of the attribute
         * @return the value of the attribute
         * @throws RuntimeException if the attribute does not exist or if the value
         * is in the wrong format
         */
        public byte[] getByteArray( String name );

        /**
         * Creates and adds a new element.
         * @param name the name of the new element
         * @return the new element
         */
        public XElement addElement( String name );

        /**
         * Gets the first element with the given name.
         * @param name the name of the element
         * @return the element or <code>null</code>
         */
        public XElement getElement( String name );

        /**
         * Gets the number of children this element has.
         * @return the number of children
         */
        public int getElementCount();

        /**
         * Gets the index'th child of this element.
         * @param index the index of the child
         * @return the child
         */
        public XElement getElement( int index );

        /**
         * Gets all children with a given name.
         * @param name the name each child must have
         * @return the array of children, might be empty
         */
        public XElement[] getElements( String name );

}
