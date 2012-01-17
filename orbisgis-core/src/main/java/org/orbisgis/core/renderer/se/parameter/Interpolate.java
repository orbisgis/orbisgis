package org.orbisgis.core.renderer.se.parameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.*;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;

/**
 * Transformation of continuous values by a function defined on a number of nodes. 
 * This is used to adjust the value distribution of an attribute to the desired distribution 
 * of a continuous symbolization control variable (like size, width, color, etc...).</p>
 * <p><code>Interpolate</code> are defined by : 
 * <ul><li>The interpolation mode</li>
 * <li>The list of interpolation points</li>
 * <li>The <code>RealParameter</code> used to retrieve the values to interpolate</li>
 * <li>The <code>FallbackType</code> value used when a data can't be processed</li>
 * @author maxence
 * @param <ToType> One of RealParameter or ColorParameter
 * @param <FallbackType> extends ToType (the LiteralOne, please...)
 * @todo find a nice way to compute interpolation for RealParameter and ColorParameter
 *
 */
public abstract class Interpolate<ToType extends SeParameter, FallbackType extends ToType> implements SeParameter {

        private InterpolationMode mode;
        private RealParameter lookupValue;
        private FallbackType fallbackValue;
        private List<InterpolationPoint<ToType>> iPoints;

        /**
         * Supported interpolation modes.
         */
        public enum InterpolationMode {
                LINEAR, COSINE, CUBIC
        }

        /**
         * The default constructor only instanciates an empty list of 
         * <code>InterpolationPoint</code>
         */
        protected Interpolate() {
                this.iPoints = new ArrayList<InterpolationPoint<ToType>>();
        }

        /**
         * Build an <code>Interpolate</code> instance where the default value for 
         * unprocessable cases is <code>fallbackValue</code>
         * @param fallbackValue 
         */
        public Interpolate(FallbackType fallbackValue) {
                this.fallbackValue = fallbackValue;
                this.iPoints = new ArrayList<InterpolationPoint<ToType>>();
        }

        @Override
        public final HashSet<String> dependsOnFeature() {
            HashSet<String> out = this.getLookupValue().dependsOnFeature();
            for (int i = 0; i < this.getNumInterpolationPoint(); i++) {
                HashSet<String> r = this.getInterpolationPoint(i).getValue().dependsOnFeature();
                out.addAll(r);
            }
            return out;
        }

        /**
         * Retrieve the mode that is used to process the interpolation.
         * @return 
         */
        public InterpolationMode getMode() {
                return mode;
        }

        /**
         * Set the mode that must be used to process the interpolation.
         * @param mode 
         */
        public void setMode(InterpolationMode mode) {
                this.mode = mode;
        }

        /**
         * Retrieve the list of interpolation points.
         * @return 
         */
        protected List<InterpolationPoint<ToType>> getInterpolationPoints() {
                return iPoints;
        }

        /**
         * Set the default value to be returned if an input can't be processed.
         * @param fallbackValue 
         */
        public void setFallbackValue(FallbackType fallbackValue) {
                this.fallbackValue = fallbackValue;
        }

        /**
         * Retrieve the default value that is returned when an input can't be processed.
         * @return 
         */
        public FallbackType getFallbackValue() {
                return fallbackValue;
        }

        /**
         * Set the lookup value that will be used to retrieve the data to process.
         * @param lookupValue 
         */
        public void setLookupValue(RealParameter lookupValue) {
                this.lookupValue = lookupValue;
                if (this.lookupValue != null) {
                        this.lookupValue.setContext(RealParameterContext.REAL_CONTEXT);
                }
        }

        /**
         * Get the lookup value that will be used to retrieve the data to process.
         * @return 
         */
        public RealParameter getLookupValue() {
                return lookupValue;
        }

        /**
         * Return the number of classes defined within the classification. According to this number (n),
         *  available class value ID are [0;n] and ID for threshold are [0;n-1
         *
         *  @return number of defined class
         */
        public int getNumInterpolationPoint() {
                return iPoints.size();
        }

        /**
         * Add a new interpolation point. The new point is inserted at the right 
         * place in the interpolation point list, according to its data
         * @param point 
         */
        public void addInterpolationPoint(InterpolationPoint<ToType> point) {
                iPoints.add(point);
                sortInterpolationPoint();
        }

        /**
         * Get the ith <code>InterpolationPoint</code> in the list of interpolation
         * points.
         * @param i
         * @return The ith <code>InterpolationPoint</code>
         * @throws IndexOutOfBoundsException - if i is out of range 
         *      <code>(index &lt; 0 || index >= size())</code>
         */
        public InterpolationPoint<ToType> getInterpolationPoint(int i) {
                return iPoints.get(i);
        }

        /**
         * Set the <code>InterpolationMode</code> used to process the values.
         * @param mode one of the <code>Interpolate.InterpolationMode</code> values
         */
        public void setInterpolationMode(InterpolationMode mode) {
                this.mode = mode;
        }

        /**
         * Get the <code>InterpolationMode</code> used to process the values.
         * @return 
         */
        public InterpolationMode getInterpolationMode() {
                return mode;
        }

        /**
         * Sort the interpolation points.
         */
        private void sortInterpolationPoint() {
                Collections.sort(iPoints);
        }

        @Override
        public ParameterValueType getJAXBParameterValueType() {
                ParameterValueType p = new ParameterValueType();
                p.getContent().add(this.getJAXBExpressionType());
                return p;
        }

        @Override
        public JAXBElement<?> getJAXBExpressionType() {
                InterpolateType i = new InterpolateType();

                if (fallbackValue != null) {
                        i.setFallbackValue(fallbackValue.toString());
                }
                if (lookupValue != null) {
                        i.setLookupValue(lookupValue.getJAXBParameterValueType());
                }

                if (mode != null) {
                        i.setMode(ModeType.fromValue(mode.toString().toLowerCase()));
                }

                List<InterpolationPointType> ips = i.getInterpolationPoint();


                for (InterpolationPoint<ToType> ip : iPoints) {
                        InterpolationPointType ipt = new InterpolationPointType();

                        ipt.setValue(ip.getValue().getJAXBParameterValueType());
                        ipt.setData(ip.getData());
                        ips.add(ipt);
                }

                ObjectFactory of = new ObjectFactory();
                return of.createInterpolate(i);
        }

        protected int getFirstIP(double data) {
                int i = -1;
                for (InterpolationPoint ip : iPoints) {
                        if (ip.getData() > data) {
                                return i;
                        }
                        i++;
                }
                return -1;
        }

        protected double cubicInterpolation(double d1, double d2, double x,
                double v1, double v2, double v3, double v4) {
                //double mu = (x - d1) / (d2 - d1);

                return 0.0;
        }

        protected double cosineInterpolation(double d1, double d2, double x, double v1, double v2) {
                double mu = (x - d1) / (d2 - d1);
                double mu2 = (1 - Math.cos(mu * Math.PI)) * 0.5;
                return v1 + mu2 * (v2 - v1);
        }

        protected double linearInterpolation(double d1, double d2, double x, double v1, double v2) {
                return v1 + (v2 - v1) * (x - d1) / (d2 - d1);
        }
}
