/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
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
package org.gdms.geometryUtils;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * This utility class provides some useful methods related to JTS {@link Coordinate} objects.
 * 
 * @author Erwan Bocher
 */
public final class CoordinatesUtils {

        /** 
         * Interpolates a z value (linearly) between the two coordinates.
        
         * @param firstCoordinate 
         * @param lastCoordinate 
         * @param toBeInterpolated 
         * @return 
         */
        public static double interpolate(Coordinate firstCoordinate, Coordinate lastCoordinate, Coordinate toBeInterpolated) {
                if (Double.isNaN(firstCoordinate.z)) {
                        return Double.NaN;
                }
                if (Double.isNaN(lastCoordinate.z)) {
                        return Double.NaN;
                }
                return firstCoordinate.z + (lastCoordinate.z - firstCoordinate.z) * firstCoordinate.distance(toBeInterpolated)
                        / (firstCoordinate.distance(toBeInterpolated) + toBeInterpolated.distance(lastCoordinate));
        }

        public static boolean contains(Coordinate[] coords, Coordinate coord) {
                for (Coordinate coordinate : coords) {
                        if (Double.isNaN(coord.z)) {
                                return coordinate.equals(coordinate);
                        } else {
                                 return coordinate.equals3D(coordinate);
                        }
                }
                return false;
        }

        /** 
         * Checks if a coordinate array contains a specific coordinate. 
         * 
         * The equality is done only in 2D (z values are not checked).
         * 
         * @param coords 
         * @param coord 
         * @return 
         */
        public static boolean contains2D(Coordinate[] coords, Coordinate coord) {
                for (Coordinate coordinate : coords) {
                        if (coordinate.equals2D(coord)) {
                                return true;
                        }
                }
                return false;
        }

        /** 
         * Check if a coordinate array contains a specific coordinate. 
         * 
         * The equality is done in 3D (z values ARE checked).
         * 
         * @param coords 
         * @param coord 
         * @return 
         */
        public static boolean contains3D(Coordinate[] coords, Coordinate coord) {
                for (Coordinate coordinate : coords) {
                        if (coordinate.equals3D(coord)) {
                                return true;
                        }
                }
                return false;
        }

        /** Determine the min and max "z" values in an array of Coordinates.
         * @param cs The array to search.
         * @return An array of size 2, index 0 is min, index 1 is max.
         */
        public static double[] zMinMax(final Coordinate[] cs) {
                double zmin;
                double zmax;
                boolean validZFound = false;
                double[] result = new double[2];

                zmin = Double.NaN;
                zmax = Double.NaN;

                double z;

                for (int t = cs.length - 1; t >= 0; t--) {
                        z = cs[t].z;

                        if (!(Double.isNaN(z))) {
                                if (validZFound) {
                                        if (z < zmin) {
                                                zmin = z;
                                        }

                                        if (z > zmax) {
                                                zmax = z;
                                        }
                                } else {
                                        validZFound = true;
                                        zmin = z;
                                        zmax = z;
                                }
                        }
                }

                result[0] = (zmin);
                result[1] = (zmax);


                return result;
        }

        /**
         * Private constructor for utility class.
         */
        private CoordinatesUtils() {
        }
}
