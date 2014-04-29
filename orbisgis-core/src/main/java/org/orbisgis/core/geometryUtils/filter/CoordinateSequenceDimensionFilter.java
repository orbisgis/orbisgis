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
package org.orbisgis.core.geometryUtils.filter;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.CoordinateSequenceFilter;

/**
 * Filter on the dimension of the coordinate sequence.
 *
 * @author Erwan Bocher
 */
public class CoordinateSequenceDimensionFilter implements CoordinateSequenceFilter {

        private boolean isDone = false;
        private int dimension = 0;
        private int lastDimen = 0;
        public static final int XY = 2;
        public static final int XYZ = 3;
        public static final int XYZM = 4;
        private int maxDim = XYZM;

        @Override
        public void filter(CoordinateSequence seq, int i) {
                int dim = seq.getDimension();
                // we have to check for both the dimension AND that the value is not NaN
                if (dim == XY) {
                        dimension = XY;
                } else {
                        double firstZ = seq.getOrdinate(i, CoordinateSequence.Z);
                        if (!Double.isNaN(firstZ)) {
                                if (dim == XYZ) {
                                        dimension = XYZ;
                                } else {
                                        double firstM = seq.getOrdinate(i, CoordinateSequence.M);
                                        if (!Double.isNaN(firstM)) {
                                                dimension = XYZM;
                                        } else {
                                                dimension = XYZ;
                                        }
                                }
                        } else {
                                dimension = XY;
                        }
                }
                if (dimension > lastDimen) {
                        lastDimen = dimension;
                }
                if (i == seq.size() || lastDimen >= maxDim) {
                        isDone = true;
                }
        }

        /**
         * Gets the dimension of the coordinate sequence.
         *
         * @return a integer between 2 and 4.
         */
        public int getDimension() {
                return lastDimen;
        }

        /**
         * Sets the maximum allowed dimension for the filter.
         *
         * The filter will stop after this dimension has been reached.
         * Possible values are:
         * -
         * <code>CoordinateSequenceDimensionFilter.XY</code>
         * -
         * <code>CoordinateSequenceDimensionFilter.XYZ</code>
         * -
         * <code>CoordinateSequenceDimensionFilter.XYZM</code>
         * Default value is
         * <code>CoordinateSequenceDimensionFilter.XYZM</code>.
         *
         * @param maxDim a integer dimension
         */
        public void setMAXDim(int maxDim) {
                this.maxDim = maxDim;
        }

        @Override
        public boolean isDone() {
                return isDone;
        }

        @Override
        public boolean isGeometryChanged() {
                return false;
        }
}
