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

package org.orbisgis.view.docking.internals;

import org.orbisgis.viewapi.util.XElement;

/**
 * Decorate DockingFrame XElement util.
 * @author Nicolas Fortin
 */
public class XElementImpl implements XElement {
        private bibliothek.util.xml.XElement intern;

        public XElementImpl(bibliothek.util.xml.XElement intern) {
                this.intern = intern;
        }

        private XElement[] getArray(bibliothek.util.xml.XElement[]  bElements) {
                XElement[] elements = new XElement[bElements.length];
                for(int i=0;i<bElements.length;i++) {
                        elements[i] = new XElementImpl(bElements[i]);
                }
                return elements;
        }

        @Override
        public String getName() {
                return intern.getName();
        }

        @Override
        public XElement[] children() {
                return getArray(intern.children());
        }

        @Override
        public XElement addByte(String name, byte value) {
                return new XElementImpl(intern.addByte(name,value));
        }

        @Override
        public XElement addShort(String name, short value) {
                return new XElementImpl(intern.addShort(name,value));
        }

        @Override
        public XElement addInt(String name, int value) {
                return new XElementImpl(intern.addInt(name,value));
        }

        @Override
        public XElement addLong(String name, long value) {
                return new XElementImpl(intern.addLong(name,value));
        }

        @Override
        public XElement addFloat(String name, float value) {
                return new XElementImpl(intern.addFloat(name,value));
        }

        @Override
        public XElement addDouble(String name, double value) {
                return new XElementImpl(intern.addDouble(name,value));
        }

        @Override
        public XElement addChar(String name, char value) {
                return new XElementImpl(intern.addChar(name,value));
        }

        @Override
        public XElement addString(String name, String value) {
                return new XElementImpl(intern.addString(name,value));
        }

        @Override
        public XElement addBoolean(String name, boolean value) {
                return new XElementImpl(intern.addBoolean(name,value));
        }

        @Override
        public XElement addByteArray(String name, byte[] value) {
                return new XElementImpl(intern.addByteArray(name,value));
        }

        @Override
        public boolean attributeExists(String name) {
                return intern.attributeExists(name);
        }

        @Override
        public byte getByte(String name) {
                return intern.getByte(name);
        }

        @Override
        public short getShort(String name) {
                return intern.getShort(name);
        }

        @Override
        public int getInt(String name) {
                return intern.getInt(name);
        }

        @Override
        public long getLong(String name) {
                return intern.getLong(name);
        }

        @Override
        public float getFloat(String name) {
                return intern.getFloat(name);
        }

        @Override
        public double getDouble(String name) {
                return intern.getDouble(name);
        }

        @Override
        public char getChar(String name) {
                return intern.getChar(name);
        }

        @Override
        public String getString(String name) {
                return intern.getString(name);
        }

        @Override
        public boolean getBoolean(String name) {
                return intern.getBoolean(name);
        }

        @Override
        public byte[] getByteArray(String name) {
                return intern.getByteArray(name);
        }

        @Override
        public XElement addElement(String name) {
                return new XElementImpl(intern.addElement(name));
        }

        @Override
        public XElement getElement(String name) {
                return new XElementImpl(intern.getElement(name));
        }

        @Override
        public int getElementCount() {
                return intern.getElementCount();
        }

        @Override
        public XElement getElement(int index) {
                return new XElementImpl(intern.getElement(index));
        }

        @Override
        public XElement[] getElements(String name) {
                return getArray(intern.getElements(name));
        }
}
