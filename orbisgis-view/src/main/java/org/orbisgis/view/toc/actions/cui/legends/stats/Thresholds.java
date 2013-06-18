package org.orbisgis.view.toc.actions.cui.legends.stats;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.orbisgis.core.renderer.se.parameter.Categorize;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This class uses statistics on a field to compute thresholds.
 * @author Alexis Guéganno
 */
public class Thresholds {
    private DescriptiveStatistics stats;
    private String fieldName;

    /**
     * Builds a new {@code Thresholds} instance using the given {@code DescriptiveStatistics} and {@code String} instance.
     * @param input The computed statistics.
     * @param name The name of the field we took the data from.
     */
    public Thresholds(DescriptiveStatistics input, String name){
        this.stats = input;
        this.fieldName = name;
    }

    /**
     * Gets {@code classNumber} of methods according to the given classification
     * @param method The classification method
     * @param classNumber The number of classes
     * @return The thresholds in a SortedSet
     */
    public SortedSet<Double> getThresholds(Categorize.CategorizeMethod method, int classNumber){
        switch(method){
            case EQUAL_INTERVAL: return getEqualIntervals(classNumber);
            case STANDARD_DEVIATION: return getMeanStandardDev(classNumber);
            default: throw new UnsupportedOperationException("This method is not supported");
        }
    }

    /**
     * Divide the space between and max in {@code classNumber} equal intervals. The returned set does not contain
     * negative infinity : the first and last thresholds match the extrema of the input data.
     * @param classNumber The number of classes
     * @return The thresholds in a SortedSet.
     */
    public SortedSet<Double> getEqualIntervals(int classNumber){
        Double min = stats.getMin();
        Double max = stats.getMax();
        TreeSet<Double> ret = new TreeSet<Double>();
        if(min < Double.POSITIVE_INFINITY && max > Double.NEGATIVE_INFINITY){
            Double step = (max - min) / classNumber;
            for(int i = 0; i<classNumber;i++){
                ret.add(min+step*i);
            }
        }
        return ret;
    }

    /**
     * Gets {@code classNumber} thresholds computed according to the Mean - Standard Deviation method. The returned set
     * contains negative infinity as this classification method will likely produce thresholds that do not match the
     * extrema of the input set.
     * @param classNumber The number of classes.
     * @return The thresholds.
     */
    public SortedSet<Double> getMeanStandardDev(int classNumber){
        if(classNumber % 2 == 0){
            return getMeanStandardDevEven(classNumber);
        } else {
            return getMeanStandardDevOdd(classNumber);
        }
    }

    /**
     * Gets thresholds for an odd number of classes computed according to the Mean - Standard Deviation method.
     * @param classNumber The number of classes. Shall be odd.
     * @return The thresholds
     */
    private SortedSet<Double> getMeanStandardDevOdd(int classNumber) {
        Double mean = stats.getMean();
        Double stDev = stats.getStandardDeviation();
        SortedSet<Double> ret = new TreeSet<Double>();
        ret.add(Double.NEGATIVE_INFINITY);
        if(classNumber >1){
            int num = (classNumber - 1)/2;
            for(int i = 0; i<num; i++){
                ret.add(mean + (i + 0.5) * stDev);
                ret.add(mean - (i + 0.5) * stDev);
            }
        }
        return ret;
    }

    /**
     * Gets thresholds for an even number of classes computed according to the Mean - Standard Deviation method.
     * @param classNumber The number of classes. Shall be even.
     * @return The thresholds
     */
    private SortedSet<Double> getMeanStandardDevEven(int classNumber) {
        Double mean = stats.getMean();
        Double stDev = stats.getStandardDeviation();
        SortedSet<Double> ret = new TreeSet<Double>();
        ret.add(Double.NEGATIVE_INFINITY);
        if(classNumber > 0){
            ret.add(mean);
        }
        if(classNumber > 2){
            int num = classNumber/2 - 1;
            for(int i = 1; i<= num; i++){
                ret.add(mean + i*stDev);
                ret.add(mean - i*stDev);
            }
        }
        return ret;
    }

    /**
     * Gets the name of the field associated to this {@code Thresholds} instance.
     * @return The name of the field that had been used to compute stats.
     */
    public String getFieldName(){
        return fieldName;
    }

}
