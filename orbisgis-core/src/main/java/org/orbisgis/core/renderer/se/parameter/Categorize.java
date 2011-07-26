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
import org.gdms.data.SpatialDataSourceDecorator;
import net.opengis.se._2_0.core.ExtensionParameterType;
import net.opengis.se._2_0.core.ExtensionType;
import net.opengis.se._2_0.core.ObjectFactory;
import net.opengis.se._2_0.core.ParameterValueType;
import net.opengis.se._2_0.core.ThresholdBelongsToType;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;

import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;

/**
 *
 * @param <ToType> One of ColorParameter, RealParameter, StringParameter
 * @param <FallbackType> the Literal implementation of <ToType>
 * @author maxence
 *
 */
public abstract class Categorize<ToType extends SeParameter, FallbackType extends ToType> implements SeParameter, LiteralListener {

    private static final String SD_FACTOR_KEY = "SdFactor";
    private static final String METHOD_KEY = "method";

    private CategorizeMethod method;
    private boolean succeeding = true;
    private RealParameter lookupValue;
    private FallbackType fallbackValue;
    private ToType firstClass;
    private double sdFactor;
    private List<ToType> classValues;
    private List<RealParameter> thresholds;
    private List<CategorizeListener> listeners;

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

    public void register(CategorizeListener l) {
        if (!listeners.contains(l)) {
            listeners.add(l);
        }
    }

    public enum CategorizeMethod {

        MANUAL, NATURAL_BREAKS, QUANTILE, EQUAL_INTERVAL, STANDARD_DEVIATION
    }

    protected Categorize() {
        //this.classes = new ArrayList<Category<ToType>>();
        this.classValues = new ArrayList<ToType>();
        this.thresholds = new ArrayList<RealParameter>();
        this.listeners = new ArrayList<CategorizeListener>();
        this.sdFactor = 1.0;
    }

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

    public void setFallbackValue(FallbackType fallbackValue) {
        this.fallbackValue = fallbackValue;
    }

    public final FallbackType getFallbackValue() {
        return fallbackValue;
    }

    public void setLookupValue(RealParameter lookupValue) {
        this.lookupValue = lookupValue;
        if (lookupValue != null) {
            lookupValue.setContext(RealParameterContext.realContext);
        }
    }

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
    public void addClass(RealParameter threshold, ToType value) {
        thresholds.add(threshold);
        threshold.setContext(RealParameterContext.realContext);
        if (threshold instanceof RealLiteral) {
            ((RealLiteral) threshold).register(this);
        }

        int tIndex = thresholds.indexOf(threshold);
        System.out.println("ADD CLASS " + tIndex);
        classValues.add(tIndex, value);
        this.method = CategorizeMethod.MANUAL;
        fireClassAdded(tIndex + 1);
    }

    public boolean removeClass(int i) {
        if (getNumClasses() > 1 && i < getNumClasses() && i >= 0) {
                if (i == 0) {
                    // when the first class is remove, the second one takes its place
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

    public ToType getClassValue(int i) {
        System.out.println("GET CLASS VALUE");
        if (i == 0) {
            System.out.println(" -> first class");
            return firstClass;
        } else {
            System.out.println(" -> " + i);
            return classValues.get(i - 1);
        }
    }

    public void setClassValue(int i, ToType value) {
        System.out.println("Set Class n° " + i + " value :" + value);
        int n = i;
        if (n == 0) {
            firstClass = value;
        } else if (n > 0 && n < getNumClasses() - 1) {
            //classes.get(i - 1).setClassValue(value);
            n--; // first class in not in the list
            classValues.remove(n);
            classValues.add(n, value);
        } else {
            // TODO throw
        }
    }

    public void setThresholdValue(int i, RealParameter threshold) {
        if (i >= 0 && i < getNumClasses() - 1) {
            RealParameter remove = thresholds.remove(i);
            thresholds.add(i, threshold);
            threshold.setContext(RealParameterContext.realContext);

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

    public RealParameter getThresholdValue(int i) {
        return thresholds.get(i);
    }

    public void setThresholdsSucceeding() {
        succeeding = true;
    }

    public boolean areThresholdsSucceeding() {
        return succeeding;
    }

    public void setThresholdsPreceding() {
        succeeding = false;
    }

    public boolean areThresholdsPreceding() {
        return (!succeeding);
    }

    private void sortClasses() {
        Collections.sort(thresholds);
        fireNewThresoldsOrder();
    }

    private void fireNewThresoldsOrder() {
        for (CategorizeListener l : listeners) {
            l.thresholdResorted();
        }
    }

    protected ToType getParameter(SpatialDataSourceDecorator sds, long fid) {
        try {
            if (getNumClasses() > 1) {
                double value = lookupValue.getValue(sds, fid);
                Iterator<ToType> cIt = classValues.iterator();
                Iterator<RealParameter> tIt = thresholds.iterator();
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

    public CategorizeMethod getMethod() {
        return method;
    }

    public void setMethod(CategorizeMethod method) {
        this.method = method;
    }

    public double getSdFactor() {
        return sdFactor;
    }

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
    public JAXBElement<? extends ExpressionType> getJAXBExpressionType() {
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

        List<JAXBElement<ParameterValueType>> tv = c.getThresholdAndValue();

        if (firstClass != null) {
            tv.add(of.createValue(firstClass.getJAXBParameterValueType()));
            //c.setFirstValue(firstClass.getJAXBParameterValueType());
        }
        Iterator<RealParameter> tIt = thresholds.iterator();
        Iterator<ToType> cIt = classValues.iterator();

        while (tIt.hasNext()) {
            tv.add(of.createThreshold(tIt.next().getJAXBParameterValueType()));
            tv.add(of.createValue(cIt.next().getJAXBParameterValueType()));
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


}
