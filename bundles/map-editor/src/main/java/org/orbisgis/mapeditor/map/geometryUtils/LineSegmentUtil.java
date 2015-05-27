/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.mapeditor.map.geometryUtils;

import com.vividsolutions.jts.geom.LineSegment;

/**
 * This utility class provides some functions for JTS {@link LineSegment} objects.
 * 
 * @author Erwan Bocher
 */
public final class LineSegmentUtil {

        /**
         * Computes the Hausdorff distance between two LineSegments.
         * 
         * To compute the Hausdorff distance, the distances from one segment's endpoints to the other segment are
         * computed and the maximum chosen.
         *
         * @param seg0
         * @param seg1
         * @return the Hausdorff distance between the segments
         */
        public static double hausdorffDistance(LineSegment seg0, LineSegment seg1) {
                double hausdorffDist = seg0.distance(seg1.p0);
                double dist;
                dist = seg0.distance(seg1.p1);

                if (dist > hausdorffDist) {
                        hausdorffDist = dist;
                }

                dist = seg1.distance(seg0.p0);

                if (dist > hausdorffDist) {
                        hausdorffDist = dist;
                }

                dist = seg1.distance(seg0.p1);

                if (dist > hausdorffDist) {
                        hausdorffDist = dist;
                }

                return hausdorffDist;
        }

        /**
         * Private constructor for utility class.
         */
        private LineSegmentUtil() {
        }
}
