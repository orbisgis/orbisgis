package org.orbisgis.view.toc.actions.cui.legends.stats;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

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
     * Divide the space between and max in {@code classNumber} equal intervals
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
     * Gets the name of the field associated to this {@code Thresholds} instance.
     * @return The name of the field that had been used to compute stats.
     */
    public String getFieldName(){
        return fieldName;
    }

}
