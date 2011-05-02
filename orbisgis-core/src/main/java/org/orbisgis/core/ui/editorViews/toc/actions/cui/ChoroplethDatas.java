/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.renderer.classification.ClassificationUtils;
import org.orbisgis.core.renderer.classification.Range;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.RealAttribute;
import org.orbisgis.core.renderer.classification.RangeMethod;
import org.orbisgis.core.ui.plugins.toc.CreateChoroplethPlugIn;

/**
 * Class to store the data
 * @author sennj, mairep
 */
public class ChoroplethDatas {

    private ILayer layer;
    private List<String> fields;
    private int fieldIndex; //Selected Field Index
    private Range[] ranges;
    private int numberOfClasses; //Number of classes to create
    private StatisticMethod statIndex; //Selected statistic method
    private int defaultClassNumbers = 10; // The default maximum amount of classes which can be created
    private Boolean autoColorFill = true;
    private Color beginColor;
    private Color endColor;
    private Color[] classesColors;
    private String[] aliases;

    //Statistic (Classification, categorization) methods enumeration
    public enum StatisticMethod {

        QUANTILES, AVERAGE, JENKS, MANUAL
    }

    /**
     * Class constructor
     * Contains all the data used by the Chloropleth wizard
     * @param layer
     */
    public ChoroplethDatas(ILayer layer) {
        super();

        this.layer = layer;
        fields = new ArrayList<String>();
        //BEGIN
        //Temp variable data for testing
        statIndex = StatisticMethod.QUANTILES;
        fieldIndex = 0;
        numberOfClasses = 10;
        beginColor = Color.BLUE;
        endColor = Color.RED;
        classesColors = new Color[numberOfClasses];
        aliases = new String[numberOfClasses];

        try {
            init();
            resetRanges();
            initLabel();
            calculateColors();

        } catch (DriverException ex) {
            Logger.getLogger(ChoroplethDatas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Initiates field data
     * @throws DriverException
     */
    private void init() throws DriverException {
        Metadata metadata = layer.getDataSource().getMetadata();
        String retainedFiledName = null;
        for (int i = 0; i < metadata.getFieldCount(); i++) {
            int currentType = metadata.getFieldType(i).getTypeCode();

            if (currentType == 4 || (currentType >= 16 && currentType <= 256)) {
                retainedFiledName = metadata.getFieldName(i);
                fields.add(retainedFiledName);
            }
        }
    }

    /**
     * Calculate gradient colors based on the numberOfClasses
     */
    public void calculateColors() {
        if (ranges.length == numberOfClasses && autoColorFill) {
            int red = beginColor.getRed();
            int green = beginColor.getGreen();
            int blue = beginColor.getBlue();
            int rstep = (endColor.getRed() - beginColor.getRed()) / ranges.length;
            int gstep = (endColor.getGreen() - beginColor.getGreen()) / ranges.length;
            int bstep = (endColor.getBlue() - beginColor.getBlue()) / ranges.length;
            for (int i = 1; i <= ranges.length; i++) {
                classesColors[i - 1] = new Color(red + ((i - 1) * rstep), green + ((i - 1) * gstep), blue + ((i - 1) * bstep));
            }
            classesColors[ranges.length - 1] = endColor;
            classesColors[0] = beginColor;
            fireMyEvent(new DataChanged(this, DataChangedType.CLASSCOLORS));
        }
    }

    /**
     * initalise the label
     */
     public void initLabel() {
        for (int i = 1; i <= ranges.length; i++) {
            aliases[i - 1] = String.valueOf(i);
        }
    }

    /*
     * Resets ranges
     */
    public void resetRanges() throws DriverException {
        String selectedField = fields.get(fieldIndex); //Get field name
        RealAttribute field = new RealAttribute(selectedField); //Get field
        //Create a RangeMethod helper
        RangeMethod rangesHelper = new RangeMethod(layer.getDataSource(), field, numberOfClasses);

        //Calculate ranges based on the statIndex
        if (selectedField != null) {
            try {
                switch (statIndex) {
                    case AVERAGE:
                        rangesHelper.disecMean();
                        break;
                    case JENKS:
                        rangesHelper.disecNaturalBreaks();
                        break;
                    case QUANTILES:
                        rangesHelper.disecQuantiles();
                        break;
                    case MANUAL:
                        rangesHelper.disecQuantiles();
                        break;
                }
                ranges = rangesHelper.getRanges();

                /* disecMean() speciality
                 * Although this method accepts 2 as a number of class
                 * it actually changes its value to 4. This needs to be updated
                 * in the interface.
                 */
                if (numberOfClasses != ranges.length) {
                    setNbrClasses(ranges.length);
                }

                calculateColors();
            } catch (ParameterException ex) {
                Logger.getLogger(CreateChoroplethPlugIn.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /*
     * @return an int[] containing the numbers of classes
     * which are allowed to be created.
     *
     * Example: AVERAGE only supports 2, 4 or 8 classes
     * If not limit, the amount of classes that can be
     * created are defined in the variable "defaultClassNumbers"
     */
    public int[] getNumberOfClassesAllowed(StatisticMethod statmethod) {
        switch (statmethod) {
            case AVERAGE: //2, 4 and 8 only alowed
                return new int[]{2, 4, 8};
            default:
                int[] nonRestricted = new int[defaultClassNumbers];
                for (int i = 0; i < defaultClassNumbers; i++) {
                    nonRestricted[i] = i + 1;
                }
                return nonRestricted;
        }
    }

    /**
     * returns true if the number of classes is supported by the specified statmethod
     * @param nbrOfClasses
     * @param statmethod
     * @return Boolean
     */
    public Boolean isAllowed(int nbrOfClasses, StatisticMethod statmethod) {
        int[] values = getNumberOfClassesAllowed(statmethod);
        for (int i = 0; i < values.length; i++) {
            if (values[i] == nbrOfClasses) {
                return true;
            }
        }
        return false;
    }

    /*
     * Set the number of classes to create in classification
     * Note: AVERAGE will only accept 2, 4 or 8 classes
     */
    public void setNbrClasses(int nbrClasses) {
        if (nbrClasses != numberOfClasses) //Check for change before updating
        {
            //Check for restrictions
            switch (statIndex) {
                case AVERAGE: //2, 4 and 8 only alowed
                    if (nbrClasses == 2 || nbrClasses == 4 || nbrClasses == 8) {
                        numberOfClasses = nbrClasses;
                    }
                    break;
                default:
                    numberOfClasses = nbrClasses;
                    break;
            }
            try {
                resetRanges(); //Refresh/update ranges
                fireMyEvent(new DataChanged(this, DataChangedType.NUMBEROFCLASS));
            } catch (DriverException ex) {
                Logger.getLogger(CreateChoroplethPlugIn.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /*
     * Set the field index (data field)
     */
    public void setFieldIndex(int fieldIndex) {
        if (fieldIndex != this.fieldIndex) //Check for change
        {
            //Set the field index
            this.fieldIndex = fieldIndex;
            try {
                resetRanges(); //Reset and recalculate ranges
                fireMyEvent(new DataChanged(this, DataChangedType.FIELD));
            } catch (DriverException ex) {
                Logger.getLogger(CreateChoroplethPlugIn.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /*
     * Set the Statistic (classification) method
     */
    public void setStatisticMethod(StatisticMethod statisticIndex, Boolean refreshRanges) {
        if (statisticIndex != statIndex) //Check for change
        {
            //Set the statistic method
            statIndex = statisticIndex;
            try {
                if (refreshRanges) {
                    resetRanges(); //Reset and recalculate ranges
                }
                fireMyEvent(new DataChanged(this, DataChangedType.STATMETHOD));
            } catch (DriverException ex) {
                Logger.getLogger(CreateChoroplethPlugIn.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Gets the data sorted as it is done in the RangeMethod class
     * @return
     * @throws ParameterException
     */
    public double[] getSortedData() throws ParameterException {
        String selectedField = fields.get(fieldIndex); //Get field name
        RealAttribute field = new RealAttribute(selectedField); //Get field
        try {
            return ClassificationUtils.getSortedValues(layer.getDataSource(), field);
        } catch (DriverException ex) {
            Logger.getLogger(CreateChoroplethPlugIn.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }


    

    /**
     * Returns class aliases
     * @return
     */
    public String[] getAliases() {
        return aliases;
    }

    /**
     * Set the specific alias
     * @param value
     * @param index
     */
    public void setAlias(String value, int index) {
        if (index < aliases.length) {
            aliases[index] = value;
            fireMyEvent(new DataChanged(this, DataChangedType.ALIASES));
        }
    }

    /**
     * Gets the begin color of the gradient
     * @return
     */
    public Color getBeginColor() {
        return beginColor;
    }

    /**
     * Sets the begin color of the gradient
     * @param value
     */
    public void setBeginColor(Color value) {
        autoColorFill = true;
        beginColor = value;
        calculateColors();
        fireMyEvent(new DataChanged(this, DataChangedType.BEGINCOLOR));
    }

    /**
     * Gets the end color of the gradient
     * @return
     */
    public Color getEndColor() {
        return endColor;
    }

    /**
     * Sets the end color of the gradient
     * @param value
     */
    public void setEndColor(Color value) {
        autoColorFill = true;
        endColor = value;
        calculateColors();
        fireMyEvent(new DataChanged(this, DataChangedType.ENDCOLOR));
    }

    /**
     * Get an array of color in relation to the classes
     * @return
     */
    public Color[] getClassesColors() {
        return classesColors;
    }

    /**
     * Gets a specific color at a specific index (or class index)
     * @param index
     * @return
     */
    public Color getClassColor(int index) {
        if (index < classesColors.length) {
            return classesColors[index];
        }
        return null;
    }

    /**
     * Sets the class color at specific index
     * @param value
     * @param index
     */
    public void setClassColor(Color value, int index) {
        if (index < classesColors.length) {
            classesColors[index] = value;
            fireMyEvent(new DataChanged(this, DataChangedType.CLASSCOLOR));
        }
    }

    /**
     * Gets the selected statistic method
     * @return
     */
    public StatisticMethod getStatisticMethod() {
        return statIndex;
    }

    /**
     * Gets the selected field
     * @return
     */
    public RealAttribute getField() {
        return new RealAttribute(fields.get(fieldIndex));
    }

    /**
     * Gets the available data fields
     * @return
     */
    public List<String> getFields() {
        return fields;
    }

    /**
     * Gets the ranges
     * @return
     */
    public Range[] getRange() {
        return ranges;
    }

    /**
     * Sets the ranges
     * @param ranges
     */
    public void setRange(Range[] ranges) {
        this.ranges = ranges;
        fireMyEvent(new DataChanged(this, DataChangedType.RANGES));
    }

    /**
     * Data change type enumeration
     */
    public enum DataChangedType {
        FIELD, NUMBEROFCLASS, RANGES, STATMETHOD, BEGINCOLOR, ENDCOLOR, CLASSCOLORS, CLASSCOLOR, ALIASES
    }

    /**
     * Declare the event. It must extend EventObject.
     */
    public class DataChanged extends EventObject {

        public DataChangedType dataType;

        public DataChanged(Object source, DataChangedType datachangedtype) {
            super(source);
            dataType = datachangedtype;
        }
    }

    /**
     * Declare the listener class. It must extend EventListener.
     * A class must implement this interface to get DataChanged.
     */
    public interface DataChangedListener extends EventListener {

        public void dataChangedOccurred(DataChanged evt);
    }

    /**
     * Create the listener list
     */
    protected javax.swing.event.EventListenerList listenerList = new javax.swing.event.EventListenerList();

    /**
     * This methods allows classes to register for DataChanged
     * @param listener
     */
    public void addDataChangedListener(DataChangedListener listener) {
        listenerList.add(DataChangedListener.class, listener);
    }

    /**
     * This methods allows classes to unregister for DataChanged
     * @param listener
     */
    public void removeDataChangedListener(DataChangedListener listener) {
        listenerList.remove(DataChangedListener.class, listener);
    }

    /**
     * This private class is used to fire DataChanged
     * @param evt
     */
    public void fireMyEvent(DataChanged evt) {
        Object[] listeners = listenerList.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == DataChangedListener.class) {
                ((DataChangedListener) listeners[i + 1]).dataChangedOccurred(evt);
            }
        }
    }
}
