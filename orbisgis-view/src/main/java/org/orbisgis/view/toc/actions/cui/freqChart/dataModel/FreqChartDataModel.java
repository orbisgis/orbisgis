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
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.parameter.color.ColorLiteral;
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

    private double[] data;
    private ClassNumberGenerator cng;
    private List<double[]> bornesList;
    private List<SimpleIntervalXYDataset> intervalList;
    private List<Color> color;
    private List<String> label;
    private Color[] colorInit;
    private Color strokeColor;
    private double strokeWidth;
    private double[] minMax;
    private double classNumber;
    private double classNumberGen;
    private double interval;
    private double opacity;
    private List<double[]> seuilList;
    private int nbSeuil;
    private int nbSeuilMax;
    protected EventListenerList listenerList = new EventListenerList();
    private int pixDelta;
    private PenStroke stroke;

    /**
     * FreqChartDataModel constructor
     *
     */
    public FreqChartDataModel() {
    }

    /**
     * FreqChartDataModel constructor
     *
     * @param data
     *            the data
     */
    public FreqChartDataModel(List<String> fields, double[] data) {
        this.data = data;

        this.colorInit = new Color[2];
        this.colorInit[0] = Color.BLUE;
        this.colorInit[1] = Color.RED;

        this.cng = new YuleGenerator();
        this.classNumber = cng.getClassNumber(data.length);
        this.classNumberGen = this.classNumber;

        this.color = new ArrayList<Color>();
        this.label = new ArrayList<String>();

        this.pixDelta = 10;
        this.nbSeuil = 5;
        this.nbSeuilMax = 10;
        this.opacity = 100;

        this.stroke = new PenStroke();
        this.strokeColor = Color.BLACK;
        this.strokeWidth = 0.5;

        this.stroke.setFill(new SolidFill(strokeColor, 1.0));
        this.stroke.setWidth(new RealLiteral(0.5));
    }

    /**
     * setData set the data
     * @param data
     */
    public void setData(double[] data) {
        this.data = data;
    }

    /**
     * setClassNumberGenerator
     * @param cng
     */
    public void setClassNumberGenerator(ClassNumberGenerator cng) {
        this.cng = cng;
    }

    /**
     * setClassNumber
     * @param classNumber
     */
    public void setClassNumber(int classNumber) {
        this.classNumber = classNumber;
    }

    /**
     * getClassNumber
     * @return classNumber
     */
    public int getClassNumber() {
        return (int) classNumber;
    }

    /**
     * getClassNumberGen
     * @return classNumber
     */
    public int getClassNumberGen() {
        return (int) classNumberGen;
    }

    /**
     * setNbSeuil
     * @param nbSeuil
     */
    public void setNbSeuil(int nbSeuil) {
        this.nbSeuil = nbSeuil;
    }

    /**
     * getNbSeuil
     * @return nb seuil
     */
    public int getNbSeuil() {
        return nbSeuil;
    }

    /**
     * getMaxSeuil
     * @return max seuil
     */
    public int getMaxSeuil() {
        return nbSeuilMax;
    }

    /**
     * getSeuilList
     * @return seuilList
     */
    public List<double[]> getSeuilList() {
        return seuilList;
    }

    /**
     * setSeuilList
     * @param list
     */
    public void setSeuilList(List<double[]> list) {
        this.seuilList = list;
    }

    /**
     * getBornesList
     * @return bornesList
     */
    public List<double[]> getBornesList() {
        return bornesList;
    }

    /**
     * getRange
     * @return the Range tab
     */
    public Range[] getRange() {
        Range[] ranges = new Range[seuilList.size()];
        double[] seuil;
        for (int i = 1; i <= seuilList.size(); i++) {
            Range range = new Range();
            seuil = seuilList.get(i - 1);
            range.setMinRange(seuil[0]);
            range.setMinRange(seuil[1]);
            ranges[i - 1] = range;
        }
        return ranges;
    }

    /**
     * setColorInit
     * @param col
     */
    public void setColorInit(Color[] col) {
        this.colorInit = col;
    }

    /**
     * getColorInit
     * @return colorInit
     */
    public Color[] getColorInit() {
        return colorInit;
    }

    /**
     * setSerieColor
     * @param i
     * @param color
     */
    public void setSerieColor(int i, Color newColor) {
        color.set(i, newColor);
    }

    /**
     * getColor
     * @return color
     */
    public List<Color> getColor() {
        return color;
    }

    /**
     * setLabel
     * @param i
     * @param string
     */
    public void setLabel(int i, String string) {
        label.set(i, string);
    }

    /**
     * getLabel
     * @return label
     */
    public List<String> getLabel() {
        return label;
    }

    /**
     * getHistogramDataset
     * @return intervalList
     */
    public List<SimpleIntervalXYDataset> getHistogramDataset() {
        return intervalList;
    }

    /**
     * setPixRangeDelta
     * @param pixDelta
     */
    public void setPixRangeDelta(int pixDelta) {
        this.pixDelta = pixDelta;
    }

    /**
     * getPixRangeDelta
     * @return pixDelta
     */
    public int getPixRangeDelta() {
        return pixDelta;
    }

    /**
     * setOpacity
     * @param opacityValue
     */
    public void setOpacity(double opacityValue) {
        this.opacity = opacityValue;
    }

    /**
     * getOpacity
     * @return opacity
     */
    public double getOpacity() {
        return opacity;
    }

    /**
     * setStroke
     * @param strokeValue
     */
    public void setStroke(PenStroke strokeValue) {
        this.stroke = strokeValue;
    }

    /**
     * getStroke
     * @return stroke
     */
    public PenStroke getStroke() {
        this.stroke.setFill(new SolidFill(strokeColor, 1.0));
        this.stroke.setWidth(new RealLiteral(strokeWidth));
        return stroke;
    }

    /**
     * getStrokeColor
     * @return strokeColor
     */
    public Color getStrokeColor() {
        return strokeColor;
    }

    /**
     * setStrokeColor
     * @param strokeColor
     */
    public void setStrokeColor(Color strokeColor) {
        this.strokeColor = strokeColor;
    }

    /**
     * getStrokeWidth
     * @return stroke width
     */
    public double getStrokeWidth() {
        return strokeWidth;
    }

    /**
     * setStrokeWidth
     * @param strokeWidth
     */
    public void setStrokeWidth(double strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    /**
     * generateChartData
     */
    public void generateChartData() {
        minMax = findMinMax(data);
        interval = (minMax[1] - minMax[0]) / Math.round(classNumber);
        bornesList = findBornes(minMax[0], classNumber, interval);
        seuilList = findSeuil(minMax[0], minMax[1], nbSeuil);
        computeChartData();
    }

    /**
     * findMinMax
     * @return the min and the max value
     */
    public double[] findMinMax(double[] rawData) {
        double xMin = Double.MAX_VALUE;
        double xMax = 0;

        for (int i = 1; i <= rawData.length; i++) {
            double value = rawData[i - 1];
            if (value < xMin) {
                xMin = value;
            }
            if (value > xMax) {
                xMax = value;
            }
        }
        double[] minMaxVal = new double[2];
        minMaxVal[0] = xMin;
        minMaxVal[1] = xMax;
        return minMaxVal;
    }

    /**
     * findSeuil
     * @param min
     * @param max
     * @param seuilNb
     * @return seuilList
     */
    public List<double[]> findSeuil(double min, double max, int seuilNb) {
        List<double[]> seuilListLocal = new ArrayList<double[]>();

        double startSeuil = min;
        double stopSeuil;
        double seuil = (max - min) / seuilNb;
        for (int j = 1; j <= seuilNb; j++) {
            stopSeuil = startSeuil + seuil;
            double[] borne = new double[2];
            borne[0] = startSeuil;
            borne[1] = stopSeuil;
            seuilListLocal.add(borne);
            startSeuil = stopSeuil;
        }
        return seuilListLocal;
    }

    /**
     * findBornes
     * @param min
     * @param classNb
     * @param inter
     * @return bornes
     */
    public List<double[]> findBornes(double min, double classNb, double inter) {
        List<double[]> bornes = new ArrayList<double[]>();

        double startBorne = min;
        double stopBorne;
        for (int j = 1; j <= Math.round(classNb); j++) {
            stopBorne = startBorne + inter;
            double[] borne = new double[2];
            borne[0] = startBorne;
            borne[1] = stopBorne;
            bornes.add(borne);
            startBorne = stopBorne;
        }

        return bornes;
    }

    /**
     * createLabel
     * @param label
     * @return label
     */
    public List<String> createLabel(List<String> label) {
        if (label.size() > nbSeuil) {
            for (int i = nbSeuil; i < label.size(); i++) {
                label.remove(i - 1);
            }
        } else {
            for (int i = label.size(); i < nbSeuil; i++) {
                double[] seuil = seuilList.get(i);
                DecimalFormat df = new DecimalFormat("#.#");
                label.add(df.format(seuil[0]) + " - " + df.format(seuil[1]));
            }
        }
        return label;
    }

    /**
     * computeChartData
     */
    public void computeChartData() {

        // Generate ordered map

        Map<String, Integer> map = new TreeMap<String, Integer>(
                new MapCompare() {
                });
        Integer ONE = new Integer(1);
        for (int i = 1; i <= data.length; i++) {
            double dataEch = data[i - 1];
            for (int j = 1; j <= bornesList.size(); j++) {
                double[] bornes = bornesList.get(j - 1);
                if (dataEch >= bornes[0] && dataEch < bornes[1]) {
                    String borneStr = bornes[0] + " " + bornes[1];
                    Integer frequency = (Integer) map.get(borneStr);
                    if (frequency == null) {
                        frequency = ONE;
                    } else {
                        int valueBorne = frequency.intValue();
                        frequency = new Integer(valueBorne + 1);
                    }

                    map.put(borneStr, frequency);
                }
            }

        }

        // Parse map

        int indexMax = Math.max(map.size(), seuilList.size());

        int indexBorne = 0;
        int indexSeuil = 0;

        String[] tempBorne;
        double borne;
        double seuil;
        double oldElem;
        int elemRange;

        intervalList = new ArrayList<SimpleIntervalXYDataset>();
        Iterator<Entry<String, Integer>> it = map.entrySet().iterator();
        Entry<String, Integer> entree = (Entry<String, Integer>) it.next();

        oldElem = seuilList.get(0)[0];

        while ((indexBorne < indexMax) && (indexSeuil < indexMax)) {
            tempBorne = entree.getKey().split(" ");
            borne = Double.parseDouble(tempBorne[1]);

            if (indexSeuil < seuilList.size()) {
                seuil = seuilList.get(indexSeuil)[1];
                elemRange = entree.getValue();

                SimpleIntervalXYDataset dataInterval;

                if (borne < seuil) {
                    dataInterval = new SimpleIntervalXYDataset(oldElem, borne,
                            elemRange, intervalList.size() + 1);
                    if (it.hasNext()) {
                        entree = (Entry<String, Integer>) it.next();
                    }
                    oldElem = borne;
                    indexBorne++;
                } else {
                    dataInterval = new SimpleIntervalXYDataset(oldElem, seuil,
                            elemRange, intervalList.size() + 1);
                    oldElem = seuil;
                    indexSeuil++;
                }
                intervalList.add(dataInterval);
            } else {
                indexSeuil++;
            }
        }

        color = generateColor(color, colorInit[0], colorInit[1], nbSeuil);
        label = createLabel(label);
    }

    /**
     * generateColor
     * @param colorList
     * @param colorStart
     * @param colorStop
     * @param seuilNb
     * @return colorList
     */
    public List<Color> generateColor(List<Color> colorList, Color colorStart,
            Color colorStop, int seuilNb) {
        int red = colorStart.getRed();
        int green = colorStart.getGreen();
        int blue = colorStart.getBlue();
        double rstep = (colorStop.getRed() - colorStart.getRed())
                / (double) (seuilNb - 1);
        double gstep = (colorStop.getGreen() - colorStart.getGreen())
                / (double) (seuilNb - 1);
        double bstep = (colorStop.getBlue() - colorStart.getBlue())
                / (double) (seuilNb - 1);
        colorList.clear();
        for (int i = 1; i <= seuilNb; i++) {
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
     * addAxisListener
     * This methods allows classes to register for Axis Changed
     * @param listener
     */
    public void addAxisListener(AxisListener listener) {
        listenerList.add(AxisListener.class, listener);
    }

    /**
     * removeAxisListener
     * This methods allows classes to unregister for Axis Changed
     * @param listener
     */
    public void removeAxisListener(AxisListener listener) {
        listenerList.remove(AxisListener.class, listener);
    }

    /**
     * addDataListener
     * This methods allows classes to register for DataChanged
     * @param dcl
     */
    public void addDataListener(DataChangeListener dcl) {
        listenerList.add(DataChangeListener.class, dcl);
    }

    /**
     * removeDataListener
     * This methods allows classes to unregister for DataChanged
     * @param dcl
     */
    public void removeDataListener(DataChangeListener dcl) {
        listenerList.remove(DataChangeListener.class, dcl);
    }

    /**
     * fireEvent
     * @param axisChanged
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
     * fireDataEvent
     * @param dataChanged
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
