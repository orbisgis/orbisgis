/**
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
package org.orbisgis.coremap.renderer.se.parameter.real;

import java.sql.ResultSet;
import java.util.Map;
import net.opengis.se._2_0.core.InterpolateType;
import net.opengis.se._2_0.core.InterpolationPointType;
import net.opengis.se._2_0.core.ModeType;


import org.orbisgis.coremap.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.coremap.renderer.se.parameter.Interpolate;
import org.orbisgis.coremap.renderer.se.parameter.InterpolationPoint;
import org.orbisgis.coremap.renderer.se.parameter.ParameterException;
import org.orbisgis.coremap.renderer.se.parameter.SeParameterFactory;

/**
 * Interpolate a real value to a real value. Interpolation points must be
 * instances of <code>InterpolationPoint&lt;RealParameter></code>.
 * @author Alexis Guéganno
 */
public final class Interpolate2Real extends Interpolate<RealParameter, RealLiteral> implements RealParameter {

        private RealParameterContext ctx;

        /**
         * Create a new <code>Interpolate2Real</code> instance, without any 
         * <code>InterpolationPoint&lt;RealParameter></code> associated with it.
         * They will have to be added before any call to <code>getValue</code>.
         * @param fallback 
         */
        public Interpolate2Real(RealLiteral fallback) {
                super(fallback);
                ctx = RealParameterContext.REAL_CONTEXT;
        }

        /**
         * Create a new <code>Interpolate2Real</code> instance. All its inner 
         * elements are computed from the <code>JAXBElement&lt;InterpolateType></code>
         * given in argument.
         * @param expr
         * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
         */
        public Interpolate2Real(InterpolateType expr) throws InvalidStyle {
                super();
                ctx = RealParameterContext.REAL_CONTEXT;

                this.setFallbackValue(new RealLiteral(expr.getFallbackValue()));
                this.setLookupValue(SeParameterFactory.createRealParameter(expr.getLookupValue()));

                if (expr.getMode() == ModeType.COSINE) {
                        this.setInterpolationMode(InterpolationMode.COSINE);
                } else if (expr.getMode() == ModeType.CUBIC) {
                        this.setInterpolationMode(InterpolationMode.CUBIC);
                } else {
                        this.setInterpolationMode(InterpolationMode.LINEAR);
                }

                for (InterpolationPointType ipt : expr.getInterpolationPoint()) {
                        InterpolationPoint<RealParameter> ip = new InterpolationPoint<RealParameter>();

                        ip.setData(ipt.getData());
                        ip.setValue(SeParameterFactory.createRealParameter(ipt.getValue()));

                        this.addInterpolationPoint(ip);
                }

        }

        /**
         * Retrieve the <code>Double</code> that must be associated to the datum at index
         * <code>fid</code> in <code>rs</code>. The resulting value is obtained by
         * using the value from the <code>DataSet</code>, the 
         * interpolation points and the interpolation method.
         * @param rs
         * @param fid The index where to search in the original source.
         * @return
         * The interpolated <code>Double</code> value.
         */
        @Override
        public Double getValue(ResultSet rs, long fid) throws ParameterException {

                double value = this.getLookupValue().getValue(rs, fid);

                if (getInterpolationPoint(0).getData() >= value) {
                        return getInterpolationPoint(0).getValue().getValue(rs, fid);
                }

                int numPt = getNumInterpolationPoint();
                if (getInterpolationPoint(numPt - 1).getData() <= value) {
                        return getInterpolationPoint(numPt - 1).getValue().getValue(rs, fid);
                }

                int k = getFirstIP(value);

                InterpolationPoint<RealParameter> ip1 = getInterpolationPoint(k);
                InterpolationPoint<RealParameter> ip2 = getInterpolationPoint(k + 1);

                switch (getMode()) {
                        case CUBIC:
                                return cubicInterpolation(ip1.getData(), ip2.getData(), value,
                                        ip1.getValue().getValue(rs, fid), ip2.getValue().getValue(rs, fid), -1.0, -1.0);
                        case COSINE:
                                return cosineInterpolation(ip1.getData(), ip2.getData(), value,
                                        ip1.getValue().getValue(rs, fid), ip2.getValue().getValue(rs, fid));
                        case LINEAR:
                                return linearInterpolation(ip1.getData(), ip2.getData(), value,
                                        ip1.getValue().getValue(rs, fid), ip2.getValue().getValue(rs, fid));

                }
                //as we've analyzed the three only possible cases in the switch,
                //we're not supposed to reach this point... 
                return 0.0;
        }

        /**
         * Retrieve the <code>Double</code> that must be associated to the datum 
         * stored in {@code map}.
         * The resulting value is obtained by using the value from the  {@code
         * DataSet}, the interpolation points and the interpolation method.
         * @param map
         * @return
         * The interpolated <code>Double</code> value.
         */
        @Override
        public Double getValue(Map<String,Object> map) throws ParameterException {

                double value = this.getLookupValue().getValue(map);

                if (getInterpolationPoint(0).getData() >= value) {
                        return getInterpolationPoint(0).getValue().getValue(map);
                }

                int numPt = getNumInterpolationPoint();
                if (getInterpolationPoint(numPt - 1).getData() <= value) {
                        return getInterpolationPoint(numPt - 1).getValue().getValue(map);
                }

                int k = getFirstIP(value);

                InterpolationPoint<RealParameter> ip1 = getInterpolationPoint(k);
                InterpolationPoint<RealParameter> ip2 = getInterpolationPoint(k + 1);

                switch (getMode()) {
                        case CUBIC:
                                return cubicInterpolation(ip1.getData(), ip2.getData(), value,
                                        ip1.getValue().getValue(map), ip2.getValue().getValue(map), -1.0, -1.0);
                        case COSINE:
                                return cosineInterpolation(ip1.getData(), ip2.getData(), value,
                                        ip1.getValue().getValue(map), ip2.getValue().getValue(map));
                        case LINEAR:
                                return linearInterpolation(ip1.getData(), ip2.getData(), value,
                                        ip1.getValue().getValue(map), ip2.getValue().getValue(map));

                }
                //as we've analyzed the three only possible cases in the switch,
                //we're not supposed to reach this point...
                return 0.0;
        }

        /**
         * Set the default value to be returned if an input can't be processed.
         * Once set, the <code>RealParameterContext</code> of <code>l</code> is set
         * to the one of this <code>Interpolate2Real</code> instance.
         * @param l
         */
        @Override
        public void setFallbackValue(RealLiteral l) {
                super.setFallbackValue(l);
                if (l != null) {
                        l.setContext(ctx);
                }
        }
        
        /**
         * Add a new interpolation point. The new point is inserted at the right 
         * place in the interpolation point list, according to its data. The 
         * <code>RealParameterContext</code> of <code>point</code> is set
         * to the one of this <code>Interpolate2Real</code> instance.
         * @param point 
         */
        @Override
        public void addInterpolationPoint(InterpolationPoint<RealParameter> point) {
                RealParameter value = point.getValue();
                value.setContext(ctx);
                super.addInterpolationPoint(point);
        }

        @Override
        public String toString() {
                return "NA";
        }

        /**
         * Set the context in which the values are processed. When using this method,
         * all the inner interpolation points of this <code>Interpolate2Real</code>
         * have their <code>RealParameterContext</code> set to <code>ctx</code>.
         * @param ctx 
         */
        @Override
        public void setContext(RealParameterContext ctx) {
                this.ctx = ctx;
                this.getFallbackValue().setContext(ctx);
                for (InterpolationPoint<RealParameter> ip : getInterpolationPoints()) {
                        RealParameter value = ip.getValue();
                        value.setContext(ctx);
                }
        }

        @Override
        public RealParameterContext getContext() {
                return ctx;
        }

        @Override
        public int compareTo(Object o) {
                return 0;
        }
}
