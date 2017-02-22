/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
