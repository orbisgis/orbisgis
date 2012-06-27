package org.orbisgis.view.toc.actions.cui.freqChart.dataModel;

/* ----------------------------
 * SimpleIntervalXYDataset.java
 * ----------------------------
 * (C) Copyright 2002-2005, by Object Refinery Limited.
 *
 */

import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.IntervalXYDataset;

/**
 * A quick and dirty sample dataset.
 */
public class SimpleIntervalXYDataset extends AbstractIntervalXYDataset
        implements IntervalXYDataset {

    /** The start values. */
    private Double[] xStart;
    /** The end values. */
    private Double[] xEnd;
    /** The y values. */
    private Integer[] yValues;
    private final int size;
    private final int i;

    /**
     * SimpleIntervalXYDataset constructor
     * @param minRange
     * @param maxRange
     * @param elemRange
     * @param i
     */
     public SimpleIntervalXYDataset(double minRange, double maxRange, int elemRange,int i)
     {
       this.size = 1;
       this.i=i;
               
       this.xStart = new Double[size];
       this.xEnd = new Double[size];
       this.yValues = new Integer[size];

       this.xStart[0] = minRange;
       this.xEnd[0] = maxRange;
       this.yValues[0] = elemRange;
    }

    /**
     * Returns the number of series in the dataset.
     *
     * @return the number of series in the dataset.
     */
    public int getSeriesCount() {
        return 1;
    }

    /**
     * Returns the key for a series.
     *
     * @param series the series (zero-based index).
     *
     * @return The series key.
     */
    public String getSeriesKey(int series) {
        return "Series "+(i);
    }

    /**
     * Returns the number of items in a series.
     *
     * @param series the series (zero-based index).
     *
     * @return the number of items within a series.
     */
    public int getItemCount(int series) {
        return size;
    }

    /**
     * Returns the x-value for an item within a series.
     * <P>
     * The implementation is responsible for ensuring that the x-values are presented in ascending
     * order.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return  the x-value for an item within a series.
     */
    public Number getX(int series, int item) {
        return this.xStart[item];
    }

    /**
     * Returns the y-value for an item within a series.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the y-value for an item within a series.
     */
    public Number getY(int series, int item) {
        return this.yValues[item];
    }

    /**
     * Returns the starting X value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item within a series (zero-based index).
     *
     * @return The value.
     */
    public Number getStartX(int series, int item) {
        return this.xStart[item];
    }

    /**
     * Returns the ending X value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item within a series (zero-based index).
     *
     * @return the end x value.
     */
    public Number getEndX(int series, int item) {
        return this.xEnd[item];
    }

    /**
     * Returns the starting Y value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item within a series (zero-based index).
     *
     * @return The value.
     */
    public Number getStartY(int series, int item) {
        return this.yValues[item];
    }

    /**
     * Returns the ending Y value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item within a series (zero-based index).
     *
     * @return The value.
     */
    public Number getEndY(int series, int item) {
        return this.yValues[item];
    }

    /**
     * Registers an object for notification of changes to the dataset.
     *
     * @param listener  the object to register.
     */
    public void addChangeListener(DatasetChangeListener listener) {
        // ignored
    }

    /**
     * Deregisters an object for notification of changes to the dataset.
     *
     * @param listener  the object to deregister.
     */
    public void removeChangeListener(DatasetChangeListener listener) {
        // ignored
    }
}
