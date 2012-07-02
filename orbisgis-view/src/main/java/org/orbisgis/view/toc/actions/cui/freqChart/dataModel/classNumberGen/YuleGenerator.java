package org.orbisgis.view.toc.actions.cui.freqChart.dataModel.classNumberGen;

/**
 * YuleGenerator Implement the Yule class number generator
 * @author sennj
 */
public class YuleGenerator extends ClassNumberGenerator {

    @Override
    /**
     * Get the class number
     * @param elem the total number of element
     * @return the number of class
     */
    public double getClassNumber(int elem) {
        return (double) (2.5f * Math.pow(elem, 0.25));
    }
}
