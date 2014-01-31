package org.orbisgis.view.toc.actions.cui.legend.stats;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.orbisgis.core.renderer.se.parameter.Categorize;

import java.util.Arrays;
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
            case QUANTILES: return getQuantiles(classNumber);
            case BOXED_MEANS: return getBoxedMeans(classNumber);
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
     * Retrieve the thresholds for a quantile classification. The first threshold is the minimum value of the input set.
     * Thresholds are computed using the percentile computation capabilities of Apache commons-math.
     * @param classNumber The number of classes.
     * @return The thresholds.
     */
    public SortedSet<Double> getQuantiles(int classNumber){
        Double step = 100/((double)classNumber);
        TreeSet<Double> ret = new TreeSet<Double>();
        Double min = stats.getMin();
        ret.add(min);
        for(int i=1; i<classNumber; i++){
            double p = i*step;
            ret.add(stats.getPercentile(p));
        }
        return ret;
    }

    /**
     * Gets a boxed means analysis using the provided data. If {@code classNumber} is not a power of two,
     * the greatest power of two that is lower than it will be used.
     * @param classNumber The number of classes
     * @return The thresholds
     */
    public SortedSet<Double> getBoxedMeans(int classNumber){
        SortedSet<Double> ret = new TreeSet<Double>();
        ret.add(stats.getMin());
        int levels = classNumber == 0 ? 0 : 32 - Integer.numberOfLeadingZeros(classNumber) -1;
        computeBoxedMeans(stats, ret, levels-1);
        return ret;
    }

    /**
     * This method :
     * - Feeds the given SortedSet with the mean of the given statistics.
     * - Calls itself recursively on the two subset obtained by dividing the set around its mean, if lev > 0.
     * @param inpStat The input statistics
     * @param toFeed The SortedSet we want to feed
     * @param lev The remaining number of levels we have to process.
     */
    private void computeBoxedMeans(DescriptiveStatistics inpStat, SortedSet<Double> toFeed, int lev){
        double[] input = inpStat.getSortedValues();
        double mean = inpStat.getMean();
        toFeed.add(mean);
        if(lev > 0){
            int i = Arrays.binarySearch(input, mean);
            int ind = i < 0 ? -i-1 : i;
            double[] first = Arrays.copyOf(input, ind);
            double[] tail = Arrays.copyOfRange(input, ind, input.length);
            computeBoxedMeans(new DescriptiveStatistics(first), toFeed, lev-1);
            computeBoxedMeans(new DescriptiveStatistics(tail), toFeed, lev-1);
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
