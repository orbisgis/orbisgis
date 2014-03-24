/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV FR CNRS 2488
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
/*
 * The Unified Mapping Platform (JUMP) is an extensible, interactive GUI
 * for visualizing and manipulating spatial features with geometry and attributes.
 *
 * Copyright (C) 2003 Vivid Solutions
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * For more information, contact:
 *
 * Vivid Solutions
 * Suite #1A
 * 2328 Government Street
 * Victoria BC  V8T 5G5
 * Canada
 *
 * (250)385-6040
 * www.vividsolutions.com
 */
package org.gdms.geometryUtils;

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
