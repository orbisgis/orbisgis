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
package org.orbisgis.core.renderer.se.parameter;

import java.util.*;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.*;
import org.apache.log4j.Logger;
import org.gdms.data.values.Value;
import org.gdms.driver.DataSet;
import org.orbisgis.core.renderer.se.AbstractSymbolizerNode;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

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
 * @author Maxence Laurent, Alexis Guéganno
 *
 */
public abstract class Categorize<ToType extends SeParameter, FallbackType extends ToType>
                extends AbstractSymbolizerNode implements SeParameter, LiteralListener {

    private static final String SD_FACTOR_KEY = "SdFactor";
    private static final String METHOD_KEY = "method";
    private static final Logger LOGGER = Logger.getLogger(Categorize.class);
    private static final I18n I18N = I18nFactory.getI18n(Categorize.class);

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

    /**
     * Set the fall bacj value that is returned when a value can't be processed.
     * @param fallbackValue 
     */
    public void setFallbackValue(FallbackType fallbackValue) {
        this.fallbackValue = fallbackValue;
        if(this.fallbackValue != null){
                fallbackValue.setParent(this);
        }
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
            lookupValue.setParent(this);
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
        threshold.register(this);
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
        }
        val.setParent(this);
    }

    /**
     * Replace the ith threshold value with the parameter threshold.
     * @param i
     * @param threshold 
     */
    public void setClassThreshold(int i, RealLiteral threshold) {
        if (i >= 0 && i < getNumClasses() - 1) {
            RealParameter remove = thresholds.get(i);
            thresholds.set(i, threshold);
            threshold.setContext(RealParameterContext.REAL_CONTEXT);
            threshold.register(this);
            if (! remove.equals(threshold)) {
                sortClasses();
            }
            threshold.setParent(this);
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
    public RealParameter getClassThreshold(int i) {
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

    /**
     * Retrieves the value associated to the input data corresponding to the
     * lookupValue in {@code sds} at line {@code fid}.
     * @param map
     * @return
     */
    protected ToType getParameter(DataSet sds, long fid) {
        try {
            if (getNumClasses() > 1) {
                Double value = lookupValue.getValue(sds, fid);
                if(value == null){
                        return fallbackValue;
                }
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
            LOGGER.warn(I18N.tr("Unable to categorize the feature"), ex);
        }
        return fallbackValue;
    }

    /**
     * Retrieves the value associated to the input data corresponding to the
     * lookupValue in {@code map}.
     * @param map
     * @return
     */
    protected ToType getParameter(Map<String, Value> map) {
        try {
            if (getNumClasses() > 1) {
                Double value = lookupValue.getValue(map);
                if(value == null){
                        return fallbackValue;
                }
                Iterator<ToType> cIt = classValues.iterator();
                Iterator<RealLiteral> tIt = thresholds.iterator();
                ToType classValue = this.firstClass;
                while (cIt.hasNext()) {
                    double threshold = tIt.next().getValue(map);
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
           LOGGER.debug("Unable to categorize the feature", ex);
        }
        return fallbackValue;
    }

    /**
     *
     * @param ds
     * @param values the values to affect to classes. number of values give the number of classes
     */
    public void categorizeByEqualsInterval(DataSet ds, ToType[] values) {
        method = CategorizeMethod.EQUAL_INTERVAL;
        // int n = values.length;
        // TODO compute n-1 thresholds and assign values
    }

    /**
     *
     * @param ds
     * @param values the values to affect to classes. number of values give the numbe of classes
     */
    public void categorizeByNaturalBreaks(DataSet ds, ToType[] values) {
        method = CategorizeMethod.NATURAL_BREAKS;
        //int n = values.length;
        // TODO compute n-1 thresholds and assign values
    }

    /**
     *
     * @param ds
     * @param values the values to affect to classes. number of values give the numbe of classes
     */
    public void categorizeByQuantile(DataSet ds, ToType[] values) {
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
    public void categorizeByStandardDeviation(DataSet ds, ToType[] values, double factor) {
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

    @Override
    public UsedAnalysis getUsedAnalysis() {
        UsedAnalysis ua = new UsedAnalysis();
        ua.include(this);
        ua.merge(lookupValue.getUsedAnalysis());
        if(firstClass != null){
            ua.merge(firstClass.getUsedAnalysis());
        }
        for(ToType t : classValues){
                ua.merge(t.getUsedAnalysis());
        }
        return ua;
    }

    @Override
    public List<SymbolizerNode> getChildren() {
        List<SymbolizerNode> ls = new ArrayList<SymbolizerNode>();
        ls.add(lookupValue);
        if(firstClass != null){
            ls.add(firstClass);
        }
        ls.addAll(classValues);
        ls.addAll(thresholds);
        return ls;
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
