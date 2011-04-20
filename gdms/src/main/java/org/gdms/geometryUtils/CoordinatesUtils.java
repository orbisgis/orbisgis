package org.gdms.geometryUtils;

import com.vividsolutions.jts.geom.Coordinate;

public class CoordinatesUtils {

        /**
         * Interpolate a z value.
         * @param a
         * @param b
         * @param c
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

        /**
         * Check if a coordinate array contains a coord.
         * Equal done in 2D
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
         * Check if a coordinate array contains a coord.
         * Equal done in 3D
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
}
