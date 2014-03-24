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
package org.orbisgis.view.toc.actions.cui;

import org.gdms.data.types.Type;

/**
 * A simple representation of geometry types. We don't have as much types here
 * as in {@link Type}, and that's on purpose. Indeed, values defined here are
 * intended to be used in GUI, considergin that most of the times, we will
 * processe multipoints and points the same way, for instance.
 * @author Alexis Guéganno
 */
public class SimpleGeometryType {

        private SimpleGeometryType(){};

        public static final int POINT = 1;
        public static final int LINE = 2;
        public static final int POLYGON = 4;
        public static final int ALL = POINT | LINE | POLYGON;

        /**
         * Gets the simple representation of {@code type}.
         *
         * @param type
         * @return One of the constants defined in this class
         * @throws IllegalArgumentException If {@code type} is not a geometry
         * type.
         *
         */
        public static int getSimpleType(Type type){
                int tc = type.getTypeCode();
                switch(tc){
                        case Type.POINT:
                        case Type.MULTIPOINT:
                                return POINT;
                        case Type.LINESTRING:
                        case Type.MULTILINESTRING:
                                return LINE;
                        case Type.POLYGON:
                        case Type.MULTIPOLYGON:
                                return POLYGON;
                        case Type.GEOMETRYCOLLECTION:
                        case Type.GEOMETRY:
                                return ALL;
                        default:
                                throw new IllegalArgumentException("Can't recognize " +
                                        tc + " as a geometry type");
                }
        }
}
