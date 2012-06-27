package org.orbisgis.view.toc.actions.cui.freqChart.dataModel.classNumberGen;

/**
 * ClassNumberGenerator the abstract class number generator
 * @author sennj
 *
 */
public abstract class ClassNumberGenerator {

    /**
     * getClassNumber
     * @param elem the total number of element
     * @return the number of class
     */
    public abstract double getClassNumber(int elem);
}
