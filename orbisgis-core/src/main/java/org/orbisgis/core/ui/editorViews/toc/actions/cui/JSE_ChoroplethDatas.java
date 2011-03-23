/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import java.awt.Color;
import java.awt.Component;
import java.net.URL;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.gdms.data.feature.Feature;
import org.gdms.data.values.Value;
import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.renderer.classification.ClassificationUtils;
import org.orbisgis.core.renderer.classification.Range;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.RealAttribute;
import org.orbisgis.core.renderer.classification.RangeMethod;
import org.orbisgis.core.sif.UIPanel;
import org.orbisgis.core.ui.plugins.toc.CreateChoroplethPlugIn;

/**
 *
 * @author sennj
 */
public class JSE_ChoroplethDatas{

    private ILayer layer;
    private List<String> fields;
    private int fieldIndex; //Selected Field Index
    private Range[] ranges;
    private int numberOfClasses; //Number of classes to create
    private Value value;
    private StatisticMethod statIndex; //Selected statistic method
    private int defaultClassNumbers = 10; // The default maximum amount of classes which can be created
    private Color beginColor;
    private Color endColor;
    private Color[] classesColors;
    private List<String> aliases;

    //Statistic (Classification) methods enumeration
    public enum StatisticMethod {QUANTILES, AVERAGE, JENKS, MANUAL}
    public enum DataChangedType {FIELD, NUMBEROFCLASS, RANGES, STATMETHOD, BEGINCOLOR, ENDCOLOR, CLASSCOLORS, ALIASES}

    JSE_ChoroplethDatas(ILayer layer) {
        super();
        this.layer = layer;
        fields = new ArrayList<String>();
        aliases = new ArrayList<String>();
        //BEGIN
        //Temp variable data for testing
        statIndex = StatisticMethod.QUANTILES;
        fieldIndex = 0;
        numberOfClasses = 7;
        beginColor = Color.LIGHT_GRAY;
        endColor = Color.ORANGE;
        classesColors = new Color[7];
        classesColors[0] = Color.RED;
        classesColors[1] = Color.YELLOW;
        classesColors[2] = Color.BLUE;
        classesColors[3] = Color.GREEN;
        classesColors[4] = Color.ORANGE;
        classesColors[5] = Color.BLACK;
        classesColors[6] = Color.CYAN;
        //END
        try {
            init();
        } catch (DriverException ex) {
            Logger.getLogger(CreateChoroplethPlugIn.class.getName()).log(Level.SEVERE, null, ex);
        }




    }

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

    void readData() throws DriverException {
        /*Metadata metadata = layer.getDataSource().getMetadata();

        // Quick (and hugly) step to fetch the first numeric attribute
        String retainedFiledName = null;
        for (int i = 0; i < metadata.getFieldCount(); i++) {
        int currentType = metadata.getFieldType(i).getTypeCode();

        if (currentType == 4 || (currentType >= 16 && currentType <= 256)) {
        retainedFiledName = metadata.getFieldName(i);
        fields.add(retainedFiledName);
        }
        }

        for(int i=1;i<=fields.size();i++)
        {
        System.out.println("fields "+i+" "+fields.get(i-1));
        }*/

        String selectedField = fields.get(fieldIndex);


        if (selectedField != null) {
            try {
                RealAttribute defaultField = new RealAttribute(selectedField);
                RangeMethod rangesHelper = new RangeMethod(layer.getDataSource(), defaultField, numberOfClasses);
                rangesHelper.disecQuantiles();
                ranges = rangesHelper.getRanges();



            } catch (ParameterException ex) {
                Logger.getLogger(CreateChoroplethPlugIn.class.getName()).log(Level.SEVERE, null, ex);
            }
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
                        ranges = rangesHelper.getRanges();
                        break;
                    case JENKS:
                        rangesHelper.disecNaturalBreaks();
                        ranges = rangesHelper.getRanges();
                        break;
                    case QUANTILES:
                        rangesHelper.disecQuantiles();
                        ranges = rangesHelper.getRanges();
                        break;
                    case MANUAL:
                        rangesHelper.disecQuantiles();
                        ranges = rangesHelper.getRanges();
                        break;
                }
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

    public Boolean isAllowed(int nbrOfClasses, StatisticMethod statmethod)
    {
        int[] values = getNumberOfClassesAllowed(statmethod);
        for(int i = 0; i < values.length; i++)
            if(values[i] == nbrOfClasses)
                return true;
        return false;
    }

    /*
     * Set the number of classes to create in classification
     * Note: AVERAGE will only accept 2, 4 or 8 classes
     */
    public void setNbrClasses(int NbrClasses) {
        if (NbrClasses != numberOfClasses) //Check for change before updating
        {
            //Check for restrictions
            switch (statIndex) {
                case AVERAGE: //2, 4 and 8 only alowed
                    if (NbrClasses == 2 || NbrClasses == 4 || NbrClasses == 8) {
                        numberOfClasses = NbrClasses;
                    }
                    break;
                default:
                    numberOfClasses = NbrClasses;
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
    public void setFieldIndex(int FieldIndex) {
        if (FieldIndex != fieldIndex) //Check for change
        {
            //Set the field index
            fieldIndex = FieldIndex;
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
    public void setStatisticMethod(StatisticMethod StatisticIndex) {
        if (StatisticIndex != statIndex) //Check for change
        {
            //Set the statistic method
            statIndex = StatisticIndex;
            try {
                resetRanges(); //Reset and recalculate ranges
                fireMyEvent(new DataChanged(this, DataChangedType.STATMETHOD));
            } catch (DriverException ex) {
                Logger.getLogger(CreateChoroplethPlugIn.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

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

    public List<String> getAliases() {
        return aliases;
    }

    public void setAlias(String value, int index) {
        if (index < aliases.size())
        {
            aliases.set(index, value);
            fireMyEvent(new DataChanged(this, DataChangedType.ALIASES));
        }
    }

    public Color getBeginColor() {
        return beginColor;
    }

    public void setBeginColor(Color value) {
        beginColor = value;
        fireMyEvent(new DataChanged(this, DataChangedType.BEGINCOLOR));
    }

    public Color getEndColor() {
        return endColor;
    }

    public void setEndColor(Color value) {
        endColor = value;
        fireMyEvent(new DataChanged(this, DataChangedType.ENDCOLOR));
    }

    public Color[] getClassesColors() {
        return classesColors;
    }

    public Color getClassColor(int index) {
        if (index < classesColors.length) {
            return classesColors[index];
        }
        return null;
    }

    public void setClassColor(Color value, int index) {
        if (index < classesColors.length)
        {
            classesColors[index] = value;
            fireMyEvent(new DataChanged(this, DataChangedType.CLASSCOLORS));
        }
    }

    public StatisticMethod getStatisticMethod() {
        return statIndex;
    }

    public RealAttribute getField() {
        return new RealAttribute(fields.get(fieldIndex));
    }

    public List<String> getFields() {
        return fields;
    }

    public Range[] getRange() {
        return ranges;
    }

    public void setRange(Range[] ranges)
    {
        this.ranges = ranges;
        fireMyEvent(new DataChanged(this, DataChangedType.RANGES));
    }

    public Value getValue() {
        return value;
    }

    // Declare the event. It must extend EventObject.
    public class DataChanged extends EventObject {
        public DataChangedType dataType;
        public DataChanged(Object source, DataChangedType datachangedtype) {
            super(source);
            dataType = datachangedtype;
        }
    }

    // Declare the listener class. It must extend EventListener.
    // A class must implement this interface to get DataChanged.
    public interface DataChangedListener extends EventListener {
        public void dataChangedOccurred(DataChanged evt);
    }

    // Create the listener list
    protected javax.swing.event.EventListenerList listenerList = new javax.swing.event.EventListenerList();

    // This methods allows classes to register for DataChanged
    public void addDataChangedListener(DataChangedListener listener) {
        listenerList.add(DataChangedListener.class, listener);
    }

    // This methods allows classes to unregister for DataChanged
    public void removeDataChangedListener(DataChangedListener listener) {
        listenerList.remove(DataChangedListener.class, listener);
    }

    // This private class is used to fire DataChanged
    void fireMyEvent(DataChanged evt) {
        Object[] listeners = listenerList.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i=0; i<listeners.length; i+=2) {
            if (listeners[i]==DataChangedListener.class) {
                ((DataChangedListener)listeners[i+1]).dataChangedOccurred(evt);
            }
        }
    }

}
