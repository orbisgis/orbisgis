package org.gdms.geometryUtils.filter;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.CoordinateSequenceFilter;

/**
 *
 * @author ebocher
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
                double firstZ = seq.getOrdinate(i, CoordinateSequence.Z);
                if (!Double.isNaN(firstZ)) {
                        double firstM = seq.getOrdinate(i, CoordinateSequence.M);
                        if (!Double.isNaN(firstM)) {
                                dimension = XYZM;
                        } else {
                                dimension = XYZ;
                        }
                } else {
                        dimension = XY;
                }
                if (dimension > lastDimen){
                        lastDimen = dimension;
                }
                if (i == seq.size() || lastDimen >= maxDim) {
                        isDone = true;
                }
        }

        public int getDimension() {
                return lastDimen;
        }

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
