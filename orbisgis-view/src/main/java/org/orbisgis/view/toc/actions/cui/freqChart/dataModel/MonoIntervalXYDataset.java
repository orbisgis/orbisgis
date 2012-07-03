package org.orbisgis.view.toc.actions.cui.freqChart.dataModel;

import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Mono interval dataset
 */
public class MonoIntervalXYDataset extends AbstractIntervalXYDataset implements IntervalXYDataset {

    /** I18n */
    private final static I18n I18N = I18nFactory.getI18n(MonoIntervalXYDataset.class);
    /** The start value */
    private double xStart;
    /** The end value */
    private double xEnd;
    /** The y value */
    private int yValues;

    /**
     * SimpleIntervalXYDataset constructor
     * @param minRange start value
     * @param maxRange end value
     * @param elemRange y value
     */
    public MonoIntervalXYDataset(double minRange, double maxRange, int elemRange) {
        xStart = minRange;
        xEnd = maxRange;
        yValues = elemRange;
    }

    /**
     * Returns the integer one, the number of series in the dataset
     * @return one, the number of series in the dataset
     */
    @Override
    public int getSeriesCount() {
        return 1;
    }

    /**
     * Returns the String "Series 0" , the key for the serie.
     * @param series the series (zero-based index).
     * @return The series key.
     */
    @Override
    public String getSeriesKey(int series) {
        return I18N.tr("Series 0");
    }

    /**
     * Returns the integer one, the number of items in a serie
     * @param series the series (not significant)
     * @return the number of items within a series.
     */
    @Override
    public int getItemCount(int series) {
        return 1;
    }

    /**
     * Returns the x start value
     * @param series the series (not significant)
     * @param item the item number (not significant)
     * @return the x start value
     */
    @Override
    public Number getX(int series, int item) {
        return xStart;
    }

    /**
     * Returns the y value
     * @param series the series (not significant)
     * @param item the item number (not significant)
     * @return the y value
     */
    @Override
    public Number getY(int series, int item) {
        return yValues;
    }

    /**
     * Returns the x start value
     * @param series the series (not significant)
     * @param item the item number (not significant)
     * @return the x start value
     */
    @Override
    public Number getStartX(int series, int item) {
        return xStart;
    }

    /**
     * Returns the x end value
     * @param series the series (not significant)
     * @param item the item number (not significant)
     * @return the x end value
     */
    @Override
    public Number getEndX(int series, int item) {
        return xEnd;
    }

    /**
     * Returns the y value
     * @param series the series (not significant)
     * @param item the item number (not significant)
     * @return the y value
     */
    @Override
    public Number getStartY(int series, int item) {
        return yValues;
    }

    /**
     * Returns the y value
     * @param series the series (not significant)
     * @param item the item number (not significant)
     * @return the y value
     */
    @Override
    public Number getEndY(int series, int item) {
        return yValues;
    }

    /**
     * Registers an object for notification of changes to the dataset.
     * @param listener the object to register.
     */
    @Override
    public void addChangeListener(DatasetChangeListener listener) {
    }

    /**
     * Deregisters an object for notification of changes to the dataset.
     * @param listener the object to deregister.
     */
    @Override
    public void removeChangeListener(DatasetChangeListener listener) {
    }
}
