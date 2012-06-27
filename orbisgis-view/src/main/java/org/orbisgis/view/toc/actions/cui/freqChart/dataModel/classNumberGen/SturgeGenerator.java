package org.orbisgis.view.toc.actions.cui.freqChart.dataModel.classNumberGen;

/**
 * SturgeGenerator Implement the Sturge class number generator
 * @author sennj
 *
 */
public class SturgeGenerator extends ClassNumberGenerator {

    @Override
    /**
     * getClassNumber
     * @param elem the total number of element
     * @return the number of class
     */
    public double getClassNumber(int elem) {
        return (double) (1 + (3.3 * Math.log10(elem)));
    }
}
