package org.orbisgis.core.renderer.se.parameter.real;

import net.opengis.se._2_0.core.InterpolateType;
import net.opengis.se._2_0.core.InterpolationPointType;
import net.opengis.se._2_0.core.ModeType;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.parameter.Interpolate;
import org.orbisgis.core.renderer.se.parameter.InterpolationPoint;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;

/**
 * Interpolate a real value to a real value. Interpolation points must be
 * instances of <code>InterpolationPoint&lt;RealParameter></code>.
 * @author alexis
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
         * <code>fid</code> in <code>sds</code>. The resulting value is obtained by
         * using the value from the <code>DataSource</code>, the 
         * interpolation points and the interpolation method.
         * @param ds
         * @param fid The index where to search in the original source.
         * @return
         * The interpolated <code>Double</code> value.
         */
        @Override
        public Double getValue(DataSource sds, long fid) throws ParameterException {

                double value = this.getLookupValue().getValue(sds, fid);

                if (getInterpolationPoint(0).getData() >= value) {
                        return getInterpolationPoint(0).getValue().getValue(sds, fid);
                }

                int numPt = getNumInterpolationPoint();
                if (getInterpolationPoint(numPt - 1).getData() <= value) {
                        return getInterpolationPoint(numPt - 1).getValue().getValue(sds, fid);
                }

                int k = getFirstIP(value);

                InterpolationPoint<RealParameter> ip1 = getInterpolationPoint(k);
                InterpolationPoint<RealParameter> ip2 = getInterpolationPoint(k + 1);

                switch (getMode()) {
                        case CUBIC:
                                return cubicInterpolation(ip1.getData(), ip2.getData(), value,
                                        ip1.getValue().getValue(sds, fid), ip2.getValue().getValue(sds, fid), -1.0, -1.0);
                        case COSINE:
                                return cosineInterpolation(ip1.getData(), ip2.getData(), value,
                                        ip1.getValue().getValue(sds, fid), ip2.getValue().getValue(sds, fid));
                        case LINEAR:
                                return linearInterpolation(ip1.getData(), ip2.getData(), value,
                                        ip1.getValue().getValue(sds, fid), ip2.getValue().getValue(sds, fid));

                }
                //as we've analyzed the three only possible cases in the switch,
                //we're not supposed to reach this point... 
                return 0.0;
        }

        /**
         * Set the default value to be returned if an input can't be processed.
         * Once set, the <code>RealParameterContext</code> of <code>l</code> is set
         * to the one of this <code>Interpolate2Real</code> instance.
         * @param fallbackValue 
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
