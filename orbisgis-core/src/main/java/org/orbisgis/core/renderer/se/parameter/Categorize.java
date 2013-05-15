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
 * <li>A <code>RealParameter</code> used to retrieve the value to classify</li>
 * <li>A fallback value, whose type (<code>FallBackType</code>) is compatible with
 *      the values of the classes.</li>
 * <li>The list of class values</li>
 * <li>The list of thresholds between successive classes.</li>
 * </ul>
 * </p>
 * <p>To build a categorization with n intervals, (n-1) thresholds are needed. The
 * first interval ranges from <i>-Infinity</i> to the first threshold.</p>
 * <p>It is also possible to determine whether the threshold values are associated to their 
 * preceding or succeeding interval.</p>
 * <p>
 *     This class can be seen as a mapping between the lower limit of the interval and the value
 *     associated to the interval. That means we have a map looking like this :
 *     <ul>
 *         <li> -INF -> val0</li>
 *         <li>threshold0 -> val1 </li>
 *         <li>threshold1 -> val2 </li>
 *         <li>threshold2 -> val3 </li>
 *         <li>threshold3 -> val4 </li>
 *         <li>...</li>
 *     </ul>
 *     In this example, val0 will be returned for input between negative infinity and threshold0, val1 for input values
 *     between threshold0 and threshold1. The value associated to the greatest threshold is used for input between
 *     this particular threshold and positive infinity.
 * </p>
 * <p>
 *     This mapping tries to behave consistently with the Map API (even if it is way more simpler). There are some
 *     really important differences, though.
 *     <ul>
 *         <li>If the mapping is not empty, there shall be a value associated to -INF</li>
 *         <li>If the mapping ahs more than one element, performing a remove(0) removes the first value and the lowest
 *             threshold that is greater than negative infinity. In the previous example, we would obtain
 *             -INF -> val1 as the first mapping after calling remove(0).</li>
 *         <lI></lI>
 *     </ul>
 * </p>
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
     * RealParameter embeds all the needed information (particularly the name of
     * the column where to search).
     */
    private RealParameter lookupValue;
    private FallbackType fallbackValue;
    private double sdFactor;
    private List<CategorizeListener> listeners;
    private SortedMap<RealLiteral,ToType> mapping;

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
        this.listeners = new ArrayList<CategorizeListener>();
        this.mapping = new TreeMap<RealLiteral, ToType>();
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
        mapping.put(new RealLiteral(Double.NEGATIVE_INFINITY), firstClassValue);
        setFallbackValue(fallbackValue);
        setLookupValue(lookupValue);
        this.method = CategorizeMethod.MANUAL;
    }

    /**
     * Set the fall back value that is returned when a value can't be processed.
     * @param fallbackValue The new fall back value used when we can't find a value for a given input.
     */
    public void setFallbackValue(FallbackType fallbackValue) {
        this.fallbackValue = fallbackValue;
        if(this.fallbackValue != null){
                fallbackValue.setParent(this);
        }
    }

    /**
     * Get the value that is returned when an input can't be processed.
     * @return The value used when we can't find a value for a given input.
     */
    public final FallbackType getFallbackValue() {
        return fallbackValue;
    }

    /**
     * Set the lookup value. After using this methods, attributes to be processed in this
     * categorization will be retrieved using <code>lookupValue</code>
     * @param lookupValue The RealParameter that will build the value used to get a mapping from this categorize. It is
     *                    a generic RealParameter so that complex operation can be performed before getting the literal
     *                    from the mapping. Note that the Categorize operation does not have much sense if the
     *                    RealParameter given here does not contain a RealAttribute.
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
     * @return The RealParameter that builds the input given to the inner Double -> ToType mapping.
     */
    public final RealParameter getLookupValue() {
        return lookupValue;
    }

    /**
     * Return the number n of classes defined within the classification. According to this number (n),
     *  available class IDs are [0;n] and IDs for threshold are [0;n-1]
     *
     *  @return number of defined class
     */
    public final int getNumClasses() {
        return mapping.size();
    }

    /**
     * the new class begin from the specified threshold, up to the next one.
     * The class is inserted at the right place
     * @param threshold The new threshold
     * @param value The new value
     */
    public void put(RealLiteral threshold, ToType value) {
        mapping.put(threshold,value);
        threshold.setContext(RealParameterContext.REAL_CONTEXT);
        threshold.register(this);
        this.method = CategorizeMethod.MANUAL;
        fireClassAdded(threshold);
    }

    /**
     * Remove class number i in the categorization.
     * @param i  The range of the class. A value of 0 will remove the the lowest threshold greater than negative infinity
     * and the class value between negative infinity and this threshold.
     * @return true if the class has been removed.
     */
    public boolean remove(int i) {
        RealLiteral rl = getKey(i);
        if(rl != null){
            if(rl.getValue(null) == Double.NEGATIVE_INFINITY){
                if(mapping.size() >= 2){
                    ToType tt = get(1);
                    mapping.remove(getKey(1));
                    mapping.put(rl,tt);
                } else {
                    mapping.remove(rl);
                }
            } else {
                mapping.remove(rl);
                fireClassRemoved(rl);
            }
            return true;
        }
        return false;
    }

    /**
     * Gets the value associated to the interval whose lowest bound is rl.
     * @param rl The lowest bound of the interval associated to the mapping we want.
     * @return The value mapped to the interval whose lowest bound is rl.
     */
    public ToType get(RealLiteral rl){
        return mapping.get(rl);
    }

    /**
     * Gets the class value used for input that lies between thresholds i and i+1, where threshold 0 is negative
     * infinity.
     * @param i The index of the class value we want to retrieve
     * @return The class value between thresholds i and i+1
     */
    public ToType get(int i) {
        return mapping.get(getKey(i));
    }

    /**
     * Gets the ith key of the mapping using the ascending order.
     * @param i The range of the key we want to get.
     * @return The ith key if any, null otherwise.
     */
    public RealLiteral getKey(int i){
        if(i<0){
            return null;
        }
        Set<RealLiteral> keys = mapping.keySet();
        Iterator<RealLiteral> it = keys.iterator();
        int r=0;
        while(it.hasNext() && r<i){
            it.next();
            r++;
        }
        if(r==i && it.hasNext()){
            return it.next();
        } else {
            return null;
        }
    }

    /**
     * Sets the value associated to the interval between thresholds i and i+1 (if any) to <code>val</code>,  where
     * threshold 0 is negative infinity.
     * @param i The index of the class value we want to retrieve
     * @param val The new class value
     */
    public void setValue(int i, ToType val) {
        RealLiteral rl = getKey(i);
        if(rl != null){
            put(rl, val);
            val.setParent(this);
        }
    }

    /**
     * Replace the ith threshold value with the parameter threshold.
     * @param i
     * @param threshold 
     */
    public void setThreshold(int i, RealLiteral threshold) {
        if(i==0){
            ToType rem = mapping.remove(mapping.firstKey());
            mapping.put(threshold,rem);
            rem = mapping.remove(mapping.firstKey());
            mapping.put(new RealLiteral(Double.NEGATIVE_INFINITY),rem);
        } else if (i > 0 && i < getNumClasses() - 1) {
            RealParameter remove = getKey(i);
            mapping.put(threshold, mapping.get(remove));
            mapping.remove(remove);
            threshold.setContext(RealParameterContext.REAL_CONTEXT);
            threshold.register(this);
            threshold.setParent(this);
//            sortClasses();
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
    public RealParameter getThreshold(int i) {
        return getKey(i);
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
        TreeMap<RealLiteral,ToType> nMap = new TreeMap<RealLiteral, ToType>();
        for(Map.Entry<RealLiteral,ToType> entry : mapping.entrySet()){
            nMap.put(entry.getKey(), entry.getValue());
        }
        mapping = nMap;
        fireNewThresoldsOrder();
    }

    /**
     * Retrieves the value associated to the input data corresponding to the
     * lookupValue in {@code sds} at line {@code fid}.
     * @param sds
     * @param fid
     * @return
     */
    protected ToType getParameter(DataSet sds, long fid) {
        try {
            if (getNumClasses() > 1) {
                Double value = lookupValue.getValue(sds, fid);
                if(value == null){
                        return fallbackValue;
                }
                return getClassValue(new RealLiteral(value));
            } else { // Means nbClass == 1
                return mapping.get(new RealLiteral(Double.NEGATIVE_INFINITY));
            }

        } catch (ParameterException ex) {
            LOGGER.warn(I18N.tr("Unable to categorize the feature"), ex);
        }
        return fallbackValue;
    }

    private ToType getClassValue(RealLiteral limit){
        if(mapping.containsKey(limit)){
            if(succeeding || limit.getValue(null) == Double.NEGATIVE_INFINITY){
                return mapping.get(limit);
            } else {
                SortedMap<RealLiteral, ToType> head = mapping.headMap(limit);
                return head.get(head.lastKey());
            }
        } else {
            //we're not on a limit between two intervals
            //We get the map of all the keys that are lower than limit (ie the headMap) and get the greatest
            //element
            SortedMap<RealLiteral, ToType> head = mapping.headMap(limit);
            return head.get(head.lastKey());
        }
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
                return getClassValue(new RealLiteral(value));
            } else { // Means nbClass == 1
                return mapping.get(new RealLiteral(Double.NEGATIVE_INFINITY));
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
        ToType firstClass =mapping.get(new RealLiteral(Double.NEGATIVE_INFINITY));
        if (firstClass != null) {
            tv.add(firstClass.getJAXBParameterValueType());
        }
        Set<Map.Entry<RealLiteral,ToType>> entries = mapping.entrySet();
        Iterator<Map.Entry<RealLiteral, ToType>> tIt = entries.iterator();
        if(tIt.hasNext()){
            //We already registered the first value
            tIt.next();
        }
        while (tIt.hasNext()) {
            Map.Entry<RealLiteral, ToType> next = tIt.next();
            tv.add(next.getKey().getJAXBLiteralType());
            tv.add(next.getValue().getJAXBParameterValueType());
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
    public List<SymbolizerNode> getChildren() {
        ArrayList<SymbolizerNode> classValues= new ArrayList<SymbolizerNode>(mapping.size());
        ArrayList<SymbolizerNode> thresholds = new ArrayList<SymbolizerNode>(mapping.size());
        Set<Map.Entry<RealLiteral,ToType>> entries = mapping.entrySet();
        Iterator<Map.Entry<RealLiteral,ToType>> it = entries.iterator();
        if(it.hasNext()){
            classValues.add(it.next().getValue());
        }
        while(it.hasNext()){
            Map.Entry<RealLiteral, ToType> next = it.next();
            classValues.add(next.getValue());
            thresholds.add(next.getKey());

        }
        List<SymbolizerNode> ls = new ArrayList<SymbolizerNode>(classValues.size()+thresholds.size()+1);
        ls.add(lookupValue);
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
    
    private void fireClassAdded(RealLiteral val) {
        for (CategorizeListener l : listeners) {
            l.classAdded(val);
        }
    }

    private void fireClassRemoved(RealLiteral val) {
        for (CategorizeListener l : listeners) {
            l.classRemoved(val);
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
