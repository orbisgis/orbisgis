package org.orbisgis.core.renderer.se.parameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;
import net.opengis.fes._2.ExpressionType;
import net.opengis.se._2_0.core.CategorizeType;
import org.gdms.data.DataSource;
import net.opengis.se._2_0.core.ExtensionParameterType;
import net.opengis.se._2_0.core.ExtensionType;
import net.opengis.se._2_0.core.ObjectFactory;
import net.opengis.se._2_0.core.ParameterValueType;
import net.opengis.se._2_0.core.ThresholdBelongsToType;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;

import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;

/**
 * Categorization is defined as the "transformation of continuous values to distinct values.
 * This is for example needed to generate  choropleth maps from continuous attributes".</p>
 * <p>A <code>Categorize</code> instance is built from many objects : 
 * <ul>
 * <li>A <code>RealParameter</code> used to retrieve the value to calssify</li>
 * <li>A fallback value, whose type (<code>FallBackType</code>) is compatible with
 *      the values of the classes.</li>
 * <li>The list of class values</li>
 * <li>The list of thresholds between successive classes.</li>
 * </ul>
 * </p>
 * <p>To build a categorization with n intervals, (n-1) thresholds are needed. The
 * first interval ranges from <i>-Infinity</i> to the first threshold.</p>
 * <p>It is also possible to determine whether the threshold values are associated to their 
 * preceding or succeeding interval.
 * @param <ToType> One of ColorParameter, RealParameter, StringParameter
 * @param <FallbackType> the Literal implementation of <ToType>. It is needed to store 
 * a default value, when an analyzed input can't be placed in any category.
 * @author maxence, alexis
 *
 */
public abstract class Categorize<ToType extends SeParameter, FallbackType extends ToType> implements SeParameter, LiteralListener {

    private static final String SD_FACTOR_KEY = "SdFactor";
    private static final String METHOD_KEY = "method";

    private CategorizeMethod method;
    /**
     * If set to true
     */
    private boolean succeeding = true;
    /**
     * Gives the ability to retrieve the value that needs to be classified. The 
     * RealParameter embeded all the needed informations (particularly the name of
     * the column where to search).
     */
    private RealParameter lookupValue;
    private FallbackType fallbackValue;
    private ToType firstClass;
    private double sdFactor;
    private List<ToType> classValues;
    private List<RealLiteral> thresholds;
    private List<CategorizeListener> listeners;

    /**
     * Describes the methods that can be used to build a categorization.
     */
    public enum CategorizeMethod {

        MANUAL, NATURAL_BREAKS, QUANTILE, EQUAL_INTERVAL, STANDARD_DEVIATION
    }

    /**
     * Build a {@code Categorize} objects with empty class and threshold values.
     */
    protected Categorize() {
        this.classValues = new ArrayList<ToType>();
        this.thresholds = new ArrayList<RealLiteral>();
        this.listeners = new ArrayList<CategorizeListener>();
        this.sdFactor = 1.0;
    }

    /**
     * Build a {@code Categorize} with the given parameters. The method is registered
     * as {@code MANUAL}. Note that thresholds are leaved empty (Consequently, there is only
     * one category, from negative infinity to positive infinity).
     * @param firstClassValue
     *  The value of the first class used in this categorization.
     * @param fallbackValue
     *  The fallback value used if an input can't be processed.
     * @param lookupValue 
     *  The {@link RealParameter} used to retrieve the values to classify.
     */
    public Categorize(ToType firstClassValue, FallbackType fallbackValue, RealParameter lookupValue) {
        this();
        setClassValue(0, firstClassValue);
        setFallbackValue(fallbackValue);
        setLookupValue(lookupValue);
        this.method = CategorizeMethod.MANUAL;
    }

    @Override
    public final String dependsOnFeature() {
        StringBuilder result = new StringBuilder();

        String lookup = this.getLookupValue().dependsOnFeature();
        if (lookup != null && !lookup.isEmpty()) {
            result.append(lookup);
        }

        int i;
        for (i = 0; i < this.getNumClasses(); i++) {
            String r = this.getClassValue(i).dependsOnFeature();
            if (r != null && !r.isEmpty()) {
                result.append(" ");
                result.append(r);
            }
        }

        String res = result.toString();
        return res.trim();
    }

    /**
     * Set the fall bacj value that is returned when a value can't be processed.
     * @param fallbackValue 
     */
    public void setFallbackValue(FallbackType fallbackValue) {
        this.fallbackValue = fallbackValue;
    }

    /**
     * Get the value that is returned when an input can't be processed.
     * @return 
     */
    public final FallbackType getFallbackValue() {
        return fallbackValue;
    }

    /**
     * Set the lookup value. After using this methods, attributes to be processed in this
     * categorization will be retrieved using <code>lookupValue</code>
     * @param lookupValue 
     */
    public final void setLookupValue(RealParameter lookupValue) {
        this.lookupValue = lookupValue;
        if (lookupValue != null) {
            lookupValue.setContext(RealParameterContext.REAL_CONTEXT);
        }
    }

    /**
     * Get the current lookup value.
     * @return 
     */
    public final RealParameter getLookupValue() {
        return lookupValue;
    }

    /**
     * Return the number of classes defined within the classification. According to this number (n),
     *  available class IDs are [0;n] and IDs for threshold are [0;n-1]
     *
     *  @return number of defined class
     */
    public final int getNumClasses() {
        return classValues.size() + 1;
    }

    /**
     * the new class begin from the specified threshold, up to the next one.
     * The class is inserted at the right place
     * @param threshold
     * @param value
     */
    public void addClass(RealLiteral threshold, ToType value) {
        thresholds.add(threshold);
        threshold.setContext(RealParameterContext.REAL_CONTEXT);
        if (threshold instanceof RealLiteral) {
            ((RealLiteral) threshold).register(this);
        }

        int tIndex = thresholds.indexOf(threshold);
        classValues.add(tIndex, value);
        this.method = CategorizeMethod.MANUAL;
        fireClassAdded(tIndex + 1);
    }

    /**
     * Remove class number i in the categorization.
     * @param i
     * @return 
     */
    public boolean removeClass(int i) {
        if (getNumClasses() > 1 && i < getNumClasses() && i >= 0) {
                if (i == 0) {
                    // when the first class is removed, the second one takes its place.
                    firstClass = classValues.remove(0);
                    thresholds.remove(0);
                } else {
                    classValues.remove(i - 1);
                    thresholds.remove(i - 1);
                }
                this.method = CategorizeMethod.MANUAL;
                fireClassRemoved(i);
                return true;
        }
        return false;
    }

    /**
     * Gets the value associated to class number i.
     * @param i
     * @return 
     */
    public ToType getClassValue(int i) {
        if (i == 0) {
            return firstClass;
        } else {
            return classValues.get(i - 1);
        }
    }

    /**
     * Sets the value associated to class number i (if any) to <code>val</code>.
     * @param i
     * @param val 
     */
    public void setClassValue(int i, ToType val) {
        int n = i;
        if (n == 0) {
            firstClass = val;
        } else if (n > 0 && n < getNumClasses() - 1) {
            //classes.get(i - 1).setClassValue(value);
            n--; // first class in not in the list
            classValues.remove(n);
            classValues.add(n, val);
        } else {
            // TODO throw
        }
    }

    /**
     * Replace the ith threshold value with the parameter threshold.
     * @param i
     * @param threshold 
     */
    public void setThresholdValue(int i, RealLiteral threshold) {
        if (i >= 0 && i < getNumClasses() - 1) {
            RealParameter remove = thresholds.remove(i);
            thresholds.add(i, threshold);
            threshold.setContext(RealParameterContext.REAL_CONTEXT);

            if (threshold instanceof RealLiteral) {
                ((RealLiteral) threshold).register(this);
            }

            if (! remove.equals(threshold)) {
                sortClasses();
            }
        } else {
            // TODO throw
        }
        this.method = CategorizeMethod.MANUAL;
    }

    @Override
    public void literalChanged() {
        sortClasses();
    }

    /**
     * Get the ith threshold value of this categorization.
     * @param i
     * @return 
     */
    public RealParameter getThresholdValue(int i) {
        return thresholds.get(i);
    }

    /**
     * After using this method, threshold values will be associated to their 
     * succeeding interval.
     */
    public void setThresholdsSucceeding() {
        succeeding = true;
    }

    /**
     * Returns true if, when a value is equal to a threshold, it falls into the interval
     * that comes right after this threshold.
     * @return 
     */
    public boolean areThresholdsSucceeding() {
        return succeeding;
    }

    /**
     * After using this method, threshold values will be associated to their 
     * preceeding interval.
     */
    public void setThresholdsPreceding() {
        succeeding = false;
    }

    /**
     * Returns true if, when a value is equal to a threshold, it falls into the interval
     * that comes right before this threshold.
     * @return 
     */
    public boolean areThresholdsPreceding() {
        return (!succeeding);
    }

    /**
     * sort the threshold values.
     */
    private void sortClasses() {
        Collections.sort(thresholds);
        fireNewThresoldsOrder();
    }

    protected ToType getParameter(DataSource sds, long fid) {
        try {
            if (getNumClasses() > 1) {
                double value = lookupValue.getValue(sds, fid);
                Iterator<ToType> cIt = classValues.iterator();
                Iterator<RealLiteral> tIt = thresholds.iterator();
                ToType classValue = this.firstClass;
                while (cIt.hasNext()) {
                    double threshold = tIt.next().getValue(sds, fid);

                    if ((!succeeding && value <= threshold) || ((value < threshold))) {
                        return classValue;
                    }
                    classValue = cIt.next();
                }
                return classValue;
            } else { // Means nbClass == 1
                return firstClass;
            }

        } catch (ParameterException ex) {
            Logger.getLogger(Categorize.class.getName()).log(Level.WARNING, "Unable to categorize the feature", ex);
        }
        return fallbackValue;
    }

    /**
     *
     * @param ds
     * @param values the values to affect to classes. number of values give the number of classes
     */
    public void categorizeByEqualsInterval(DataSource ds, ToType[] values) {
        method = CategorizeMethod.EQUAL_INTERVAL;
        // int n = values.length;
        // TODO compute n-1 thresholds and assign values
    }

    /**
     *
     * @param ds
     * @param values the values to affect to classes. number of values give the numbe of classes
     */
    public void categorizeByNaturalBreaks(DataSource ds, ToType[] values) {
        method = CategorizeMethod.NATURAL_BREAKS;
        //int n = values.length;
        // TODO compute n-1 thresholds and assign values
    }

    /**
     *
     * @param ds
     * @param values the values to affect to classes. number of values give the numbe of classes
     */
    public void categorizeByQuantile(DataSource ds, ToType[] values) {
        method = CategorizeMethod.QUANTILE;
        //int n = values.length;
        // TODO compute n-1 thresholds and assign values
    }

    /**
     *
     *
     * @param ds
     * @param values the values to affect to classes. number of values give the numbe of classes
     * @param factor class (except first and last) interval equals sd*factor
     */
    public void categorizeByStandardDeviation(DataSource ds, ToType[] values, double factor) {
        method = CategorizeMethod.STANDARD_DEVIATION;
        // even => mean is a threshold
        // odd => mean is the central point of the central class
        //int n = values.length;

        // TODO compute n-1 thresholds and assign values

    }

    /**
     * Return the categorization method associated to this instance.
     * @return 
     *  The categorization method.
     */
    public CategorizeMethod getMethod() {
        return method;
    }

    /**
     * Set the categorization method associated to this instance.
     * @param method 
     */
    public void setMethod(CategorizeMethod method) {
        this.method = method;
    }

    /**
     * get the standard deviation. useful only when the classifying method is 
     * <code>STANDARD_DEVIATION</code>
     * @return 
     */
    public double getSdFactor() {
        return sdFactor;
    }

    /**
     * Set the standard deviation
     * @param sdFactor 
     */
    public void setSdFactor(double sdFactor) {
        this.sdFactor = sdFactor;
    }

    @Override
    public ParameterValueType getJAXBParameterValueType() {
        ParameterValueType p = new ParameterValueType();
        p.getContent().add(this.getJAXBExpressionType());
        return p;
    }

    protected void setPropertyFromJaxB(CategorizeType t) {

        method = CategorizeMethod.MANUAL;
        if (t.getExtension() != null) {
            for (ExtensionParameterType param : t.getExtension().getExtensionParameter()) {
                if (param.getName().equalsIgnoreCase(METHOD_KEY)) {
                    try {
                        method = CategorizeMethod.valueOf(param.getContent());
                    } catch (IllegalArgumentException e) {
                        method = CategorizeMethod.MANUAL;
                    }
                    break;
                }
            }

            if (method == CategorizeMethod.STANDARD_DEVIATION) {
                for (ExtensionParameterType param : t.getExtension().getExtensionParameter()) {
                    if (param.getName().equalsIgnoreCase(SD_FACTOR_KEY)) {
                        sdFactor = Double.parseDouble(param.getContent());
                    }
                }
            }
        }
    }

    @Override
    public JAXBElement<?> getJAXBExpressionType() {
        CategorizeType c = new CategorizeType();

        if (fallbackValue != null) {
            c.setFallbackValue(fallbackValue.toString());
        }

        if (lookupValue != null) {
            c.setLookupValue(lookupValue.getJAXBParameterValueType());
        }

        if (this.succeeding) {
            c.setThresholdBelongsTo(ThresholdBelongsToType.SUCCEEDING);
        } else {
            c.setThresholdBelongsTo(ThresholdBelongsToType.PRECEDING);
        }
        ObjectFactory of = new ObjectFactory();

        List<Object> tv = c.getThresholdAndValue();

        if (firstClass != null) {
            tv.add(firstClass.getJAXBParameterValueType());
        }
        Iterator<RealLiteral> tIt = thresholds.iterator();
        Iterator<ToType> cIt = classValues.iterator();

        while (tIt.hasNext()) {
            tv.add(tIt.next().getJAXBLiteralType());
            tv.add(cIt.next().getJAXBParameterValueType());
        }



        ExtensionType exts = of.createExtensionType();
        ExtensionParameterType param = of.createExtensionParameterType();
        param.setName(METHOD_KEY);
        param.setContent(method.name());
        exts.getExtensionParameter().add(param);

        if (method == CategorizeMethod.STANDARD_DEVIATION) {
            ExtensionParameterType sd = of.createExtensionParameterType();
            sd.setName(SD_FACTOR_KEY);
            sd.setContent("" + sdFactor);
            exts.getExtensionParameter().add(sd);
        }

        c.setExtension(exts);

        return of.createCategorize(c);
    }

    //**********************************************************************************
     /* Management of the listeners associated to this categorization.
     * 
     **********************************************************************************/

    /**
     * Add a listener to this Categorize instance.
     * @param l 
     */
    public void register(CategorizeListener l) {
        if (!listeners.contains(l)) {
            listeners.add(l);
        }
    }
    
    private void fireClassAdded(int index) {
        for (CategorizeListener l : listeners) {
            l.classAdded(index);
        }
    }

    private void fireClassRemoved(int index) {
        for (CategorizeListener l : listeners) {
            l.classRemoved(index);
        }
    }

    private void fireClassMoved(int i, int j) {
        for (CategorizeListener l : listeners) {
            l.classMoved(i, j);
        }
    }
    /**
     * Notify a change in the order of the thresholds.
     */
    private void fireNewThresoldsOrder() {
        for (CategorizeListener l : listeners) {
            l.thresholdResorted();
        }
    }

}
