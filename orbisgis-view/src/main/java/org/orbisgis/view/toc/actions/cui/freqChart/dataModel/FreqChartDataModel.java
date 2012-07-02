package org.orbisgis.view.toc.actions.cui.freqChart.dataModel;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import javax.swing.event.EventListenerList;
import org.orbisgis.core.renderer.classification.Range;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.view.toc.actions.cui.choropleth.listener.DataChangeListener;
import org.orbisgis.view.toc.actions.cui.freqChart.chartListener.AxisListener;
import org.orbisgis.view.toc.actions.cui.freqChart.chartListener.DataListener;
import org.orbisgis.view.toc.actions.cui.freqChart.chartListener.FreqChartListener.AxisChanged;
import org.orbisgis.view.toc.actions.cui.freqChart.chartListener.FreqChartListener.AxisChangedType;
import org.orbisgis.view.toc.actions.cui.freqChart.dataModel.DataChanged.DataChangedType;
import org.orbisgis.view.toc.actions.cui.freqChart.dataModel.classNumberGen.ClassNumberGenerator;
import org.orbisgis.view.toc.actions.cui.freqChart.dataModel.classNumberGen.YuleGenerator;

/**
 * FreqChartDataModel the data management
 * 
 * @author sennj
 * 
 */
public class FreqChartDataModel {

    /** The chart row data */
    private double[] data;
    /** The class number generator */
    private ClassNumberGenerator cng;
    /** The list of chart border */
    private List<List<Double>> borderList;
    /** The computed list of chart data */
    private List<MonoIntervalXYDataset> intervalList;
    /** The list of the chart border */
    private List<Color> color;
    /** The list of the chart label */
    private List<String> label;
    /** The list of the two initial chart color */
    private List<Color> colorInit;
    /** The map stroke color */
    private Color strokeColor;
    /** The map stroke width */
    private double strokeWidth;
    /** The map stroke object */
    private PenStroke stroke;
    /** The  number of class */
    private double classNumber;
    /** The original number of class */
    private double classNumberGen;
    /** The map opacity */
    private double opacity;
    /** The list of chart threshold */
    private List<List<Double>> thresholdList;
    /** The number of chart threshold */
    private int thresholdNumber;
    /** The maximal number of chart threshold */
    private int thresholdNumberMax;
    /** The list of event listener */
    protected EventListenerList listenerList = new EventListenerList();
    /** The pixel delta to catch a chart range */
    private int pixDelta;

    /**
     * FreqChartDataModel constructor
     * @param data the data
     */
    public FreqChartDataModel(double[] data) {
        this.data = data;

        this.colorInit = new ArrayList<Color>();
        this.colorInit.add(Color.BLUE);
        this.colorInit.add(Color.RED);

        this.cng = new YuleGenerator();
        this.classNumber = cng.getClassNumber(data.length);
        this.classNumberGen = this.classNumber;

        this.color = new ArrayList<Color>();
        this.label = new ArrayList<String>();

        this.pixDelta = 10;
        this.thresholdNumber = 5;
        this.thresholdNumberMax = 10;
        this.opacity = 100;

        this.stroke = new PenStroke();
        this.strokeColor = Color.BLACK;
        this.strokeWidth = 0.5;

        this.stroke.setFill(new SolidFill(strokeColor, 1.0));
        this.stroke.setWidth(new RealLiteral(0.5));
    }

    /**
     * Set the chart row data
     * @param data the chart row data
     */
    public void setData(double[] data) {
        this.data = data;
    }

    /**
     * Set the number of class generator
     * @param cng the number of class generator
     */
    public void setClassNumberGenerator(ClassNumberGenerator cng) {
        this.cng = cng;
    }

    /**
     * Set the number of class
     * @param classNumber the number of class
     */
    public void setClassNumber(int classNumber) {
        this.classNumber = classNumber;
    }

    /**
     * Get the number of class
     * @return classNumber the number of class
     */
    public int getClassNumber() {
        return (int) classNumber;
    }

    /**
     * Get the generated number of class
     * @return classNumber the original generated number of class
     */
    public int getClassNumberGen() {
        return (int) classNumberGen;
    }

    /**
     * Set the number of threshold
     * @param thresholdNumber the number of threshold
     */
    public void setThresholdNumber(int thresholdNumber) {
        this.thresholdNumber = thresholdNumber;
    }

    /**
     * Get the number of threshold
     * @return thresholdNumber the number of threshold
     */
    public int getThresholdNumber() {
        return thresholdNumber;
    }

    /**
     * Get the number of threshold max
     * @return thresholdNumberMax the number of threshold max
     */
    public int getMaxThreshold() {
        return thresholdNumberMax;
    }

    /**
     * Get the threshold list
     * @return thresholdList the threshold list
     */
    public List<List<Double>> getThresholdList() {
        return thresholdList;
    }

    /**
     * Set the threshold list
     * @param list the threshold list
     */
    public void setThresholdList(List<List<Double>> thresholdList) {
        this.thresholdList = thresholdList;
    }

    /**
     * Get the list of border
     * @return borderList the list of border
     */
    public List<List<Double>> getBorderList() {
        return borderList;
    }

    /**
     * Get the Range
     * @return the Range tab
     */
    public Range[] getRange() {
        Range[] ranges = new Range[thresholdList.size()];
        List<Double> threshold;
        for (int i = 1; i <= thresholdList.size(); i++) {
            Range range = new Range();
            threshold = thresholdList.get(i - 1);
            range.setMinRange(threshold.get(0));
            range.setMinRange(threshold.get(1));
            ranges[i - 1] = range;
        }
        return ranges;
    }

    /**
     * Set the initial color list
     * @param col the initial color list
     */
    public void setColorInit(List<Color> col) {
        this.colorInit = col;
    }

    /**
     * Get the initial color list
     * @return colorInit the initial color list
     */
    public List<Color> getColorInit() {
        return colorInit;
    }

    /**
     * Set an element of the color list
     * @param i the list index
     * @param color the color
     */
    public void setColorListElem(int i, Color newColor) {
        color.set(i, newColor);
    }

    /**
     * Get the color list
     * @return color the color list
     */
    public List<Color> getColor() {
        return color;
    }

    /**
     * Set a label in the label list
     * @param i the list index
     * @param string the label
     */
    public void setLabel(int i, String string) {
        label.set(i, string);
    }

    /**
     * Get the label list
     * @return label the label list
     */
    public List<String> getLabel() {
        return label;
    }

    /**
     * Get the dataset histogram
     * @return intervalList the dataset histogram
     */
    public List<MonoIntervalXYDataset> getHistogramDataset() {
        return intervalList;
    }

    /**
     * Set the pixel delta for range selection
     * @param pixDelta the pixel delta for range selection
     */
    public void setPixRangeDelta(int pixDelta) {
        this.pixDelta = pixDelta;
    }

    /**
     * Get the pixel delta for range selection
     * @return pixDelta the pixel delta for range selection
     */
    public int getPixRangeDelta() {
        return pixDelta;
    }

    /**
     * Set the opacity of the map
     * @param opacityValue the opacity of the map
     */
    public void setOpacity(double opacityValue) {
        this.opacity = opacityValue;
    }

    /**
     * Get the opacity of the map
     * @return opacity of the map
     */
    public double getOpacity() {
        return opacity;
    }

    /**
     * Set the map stroke object
     * @param strokeValue the map stroke object
     */
    public void setStroke(PenStroke strokeValue) {
        this.stroke = strokeValue;
    }

    /**
     * Get the map stroke object
     * @return stroke the map stroke object
     */
    public PenStroke getStroke() {
        this.stroke.setFill(new SolidFill(strokeColor, 1.0));
        this.stroke.setWidth(new RealLiteral(strokeWidth));
        return stroke;
    }

    /**
     * Get the map stroke color
     * @return strokeColor the map stroke color
     */
    public Color getStrokeColor() {
        return strokeColor;
    }

    /**
     * Set the map stroke color
     * @param strokeColor the map stroke color
     */
    public void setStrokeColor(Color strokeColor) {
        this.strokeColor = strokeColor;
    }

    /**
     * Get the map stroke width
     * @return strokeWidth the map stroke width
     */
    public double getStrokeWidth() {
        return strokeWidth;
    }

    /**
     * Set the map stroke width
     * @param strokeWidth the map stroke width
     */
    public void setStrokeWidth(double strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    /**
     * Generate the chart data
     */
    public void generateChartData() {
        List<Double> minMax = findMinMax(data);
        double min = minMax.get(0);
        double max = minMax.get(1);
        double interval = (max - min) / Math.round(classNumber);
        borderList = findBorders(min, classNumber, interval);
        thresholdList = findThreshold(min, max, thresholdNumber);
        computeChartData();
    }

    /**
     * Find the min and the max of the raw data tab
     * @return the min and the max value
     */
    public List<Double> findMinMax(double[] rawData) {
        double xMin = Double.MAX_VALUE;
        double xMax = 0;

        for (int i = 1; i <= rawData.length; i++) {
            double value = rawData[i - 1];
            xMin = Math.min(xMin, value);
            xMax = Math.max(xMax, value);
        }
        List<Double> minMaxVal = new ArrayList<Double>();
        minMaxVal.add(xMin);
        minMaxVal.add(xMax);
        return minMaxVal;
    }

    /**
     * Find the threshold
     * @param min the min of the raw data
     * @param max the max of the raw data
     * @param thresholdNumber the number of threshold
     * @return thresholdListLocal the threshold list
     */
    public List<List<Double>> findThreshold(double min, double max, int thresholdNumber) {
        List<List<Double>> thresholdListLocal = new ArrayList<List<Double>>();

        double startThreshold = min;
        double stopThreshold;
        double threshold = (max - min) / thresholdNumber;
        for (int j = 1; j <= thresholdNumber; j++) {
            stopThreshold = startThreshold + threshold;
            List<Double> border = new ArrayList<Double>();
            border.add(startThreshold);
            border.add(stopThreshold);
            thresholdListLocal.add(border);
            startThreshold = stopThreshold;
        }
        return thresholdListLocal;
    }

    /**
     * Find the borders
     * @param min the min of the raw data
     * @param classNb number of class
     * @param inter the average interval bewteen range
     * @return borders the border list
     */
    public List<List<Double>> findBorders(double min, double classNb, double inter) {
        List<List<Double>> borders = new ArrayList<List<Double>>();

        double startBorder = min;
        double stopBorder;
        for (int j = 1; j <= Math.round(classNb); j++) {
            stopBorder = startBorder + inter;
            List<Double> border = new ArrayList<Double>();
            border.add(startBorder);
            border.add(stopBorder);
            borders.add(border);
            startBorder = stopBorder;
        }
        return borders;
    }

    /**
     * Create the label
     * @param label the current label list
     * @return label the new label list
     */
    public List<String> createLabel(List<String> label) {
        if (label.size() > thresholdNumber) {
            for (int i = thresholdNumber; i < label.size(); i++) {
                label.remove(i - 1);
            }
        } else {
            for (int i = label.size(); i < thresholdNumber; i++) {
                List<Double> threshold = thresholdList.get(i);
                DecimalFormat df = new DecimalFormat("#.#");
                label.add(df.format(threshold.get(0)) + " - " + df.format(threshold.get(1)));
            }
        }
        return label;
    }

    /**
     * Compute the chart data
     */
    public void computeChartData() {

        // Generate ordered map

        Map<String, Integer> map = new TreeMap<String, Integer>(
                new MapCompare() {
                });
        int ONE = 1;
        for (int i = 1; i <= data.length; i++) {
            double dataEch = data[i - 1];
            for (int j = 1; j <= borderList.size(); j++) {
                List<Double> borders = borderList.get(j - 1);
                if (dataEch >= borders.get(0) && dataEch < borders.get(1)) {
                    String bordersStr = borders.get(0) + " " + borders.get(1);
                    int frequency;
                    if (map.get(bordersStr) == null) {
                        frequency = ONE;
                    } else {
                        int valueBorder = map.get(bordersStr);
                        frequency = valueBorder + 1;
                    }

                    map.put(bordersStr, frequency);
                }
            }
        }

        // Parse map

        int indexMax = Math.max(map.size(), thresholdList.size());

        int indexBorder = 0;
        int indexThreshold = 0;

        String[] tempBorder;
        double border;
        double threshold;
        double oldElem;
        int elemRange;

        intervalList = new ArrayList<MonoIntervalXYDataset>();
        Iterator<Entry<String, Integer>> it = map.entrySet().iterator();
        Entry<String, Integer> entree = (Entry<String, Integer>) it.next();

        oldElem = thresholdList.get(0).get(0);

        while ((indexBorder < indexMax) && (indexThreshold < indexMax)) {
            tempBorder = entree.getKey().split(" ");
            border = Double.parseDouble(tempBorder[1]);

            if (indexThreshold < thresholdList.size()) {
                threshold = thresholdList.get(indexThreshold).get(1);
                elemRange = entree.getValue();

                MonoIntervalXYDataset dataInterval;

                if (border < threshold) {
                    dataInterval = new MonoIntervalXYDataset(oldElem, border,
                            elemRange);
                    if (it.hasNext()) {
                        entree = (Entry<String, Integer>) it.next();
                    }
                    oldElem = border;
                    indexBorder++;
                } else {
                    dataInterval = new MonoIntervalXYDataset(oldElem, threshold,
                            elemRange);
                    oldElem = threshold;
                    indexThreshold++;
                }
                intervalList.add(dataInterval);
            } else {
                indexThreshold++;
            }
        }

        color = generateColor(color, colorInit.get(0), colorInit.get(1), thresholdNumber);
        label = createLabel(label);
    }

    /**
     * Generate the color list
     * @param colorList the list of color of the color gradient
     * @param colorStart the first color of the color gradientt
     * @param colorStop the last color of the color gradient
     * @param thresholdNumber the number of color of the color gradientt
     * @return colorList the generated the color list
     */
    public List<Color> generateColor(List<Color> colorList, Color colorStart,
            Color colorStop, int thresholdNumber) {
        int red = colorStart.getRed();
        int green = colorStart.getGreen();
        int blue = colorStart.getBlue();
        double rstep = (colorStop.getRed() - colorStart.getRed())
                / (double) (thresholdNumber - 1);
        double gstep = (colorStop.getGreen() - colorStart.getGreen())
                / (double) (thresholdNumber - 1);
        double bstep = (colorStop.getBlue() - colorStart.getBlue())
                / (double) (thresholdNumber - 1);
        colorList.clear();
        for (int i = 1; i <= thresholdNumber; i++) {
            colorList.add(new Color((int) (red + ((i - 1) * rstep)),
                    (int) (green + ((i - 1) * gstep)),
                    (int) (blue + ((i - 1) * bstep))));
        }
        return colorList;
    }

    class MapCompare implements Comparator<Object> {

        @Override
        public int compare(Object a, Object b) {
            double delta = Double.parseDouble(((String) a).split(" ")[0])
                    - Double.parseDouble(((String) b).split(" ")[0]);
            if (delta > 0) {
                return 1;
            } else if (delta < 0) {
                return -1;
            }
            return 0;
        }
    }

    /**
     * Add an axis listener
     * This methods allows classes to register for Axis Changed
     * @param listener an axis listener
     */
    public void addAxisListener(AxisListener listener) {
        listenerList.add(AxisListener.class, listener);
    }

    /**
     * Remove an axis listener
     * This methods allows classes to unregister for Axis Changed
     * @param listener an axis listener
     */
    public void removeAxisListener(AxisListener listener) {
        listenerList.remove(AxisListener.class, listener);
    }

    /**
     * Add an data listener
     * This methods allows classes to register for DataChanged
     * @param dcl a data change listener
     */
    public void addDataListener(DataChangeListener dcl) {
        listenerList.add(DataChangeListener.class, dcl);
    }

    /**
     * Remove an data listener
     * This methods allows classes to unregister for DataChanged
     * @param dcl a data change listener
     */
    public void removeDataListener(DataChangeListener dcl) {
        listenerList.remove(DataChangeListener.class, dcl);
    }

    /**
     * Fire an event
     * @param axisChanged an axis changed event
     */
    public void fireEvent(AxisChanged axisChanged) {
        Object[] listeners = listenerList.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == AxisListener.class) {
                if (axisChanged.dataType == AxisChangedType.RANGESUP
                        || axisChanged.dataType == AxisChangedType.RANGESDOWN) {
                    ((AxisListener) listeners[i + 1]).rangeStateChanged(axisChanged);
                } else if (axisChanged.dataType == AxisChangedType.CHARTMOVE) {
                    ((AxisListener) listeners[i + 1]).chartMove(axisChanged);
                } else if (axisChanged.dataType == AxisChangedType.RANGEPRESSED) {
                    ((AxisListener) listeners[i + 1]).rangePressed(axisChanged);
                } else if (axisChanged.dataType == AxisChangedType.CHARTPRESSED) {
                    ((AxisListener) listeners[i + 1]).chartPressed(axisChanged);
                } else if (axisChanged.dataType == AxisChangedType.CHARTDRAG) {
                    ((AxisListener) listeners[i + 1]).chartDrag(axisChanged);
                }
            }
        }
    }

    /**
     * Fire a data event
     * @param dataChanged an data changed event
     */
    public void fireDataEvent(DataChanged dataChanged) {
        Object[] listeners = listenerList.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == DataChangeListener.class) {
                if (dataChanged.dataType == DataChangedType.DATACHANGE) {
                    ((DataListener) listeners[i + 1]).dataChanged(dataChanged);
                }
            }
        }
    }
}
