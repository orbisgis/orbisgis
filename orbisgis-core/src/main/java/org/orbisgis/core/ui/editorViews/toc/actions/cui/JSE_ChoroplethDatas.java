/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import java.awt.Color;
import java.awt.Component;
import java.net.URL;
import java.util.ArrayList;
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
public class JSE_ChoroplethDatas implements UIPanel{

    private ILayer layer;

    private List<JSE_Datas> datas;
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
    public enum StatisticMethod {QUANTITY, AVERAGE, JENKS, MANUAL }

    JSE_ChoroplethDatas(ILayer layer) {
        super();
        this.layer = layer;
        fields = new ArrayList<String>();
        aliases = new ArrayList<String>();
        //BEGIN
        //Temp variable data for testing
        fieldIndex = 0;
        numberOfClasses = 4;
        beginColor = Color.LIGHT_GRAY;
        endColor = Color.ORANGE;
        classesColors = new Color[4];
        classesColors[0] = Color.RED;
        classesColors[1] = Color.YELLOW;
        classesColors[2] = Color.BLUE;
        classesColors[3] = Color.GREEN;
        //END
        try{
            init();
        }catch(DriverException ex){
             Logger.getLogger(CreateChoroplethPlugIn.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void init() throws DriverException
    {
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

    void readData() throws DriverException
    {
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
    private void resetRanges() throws DriverException{
        String selectedField = fields.get(fieldIndex); //Get field name
        RealAttribute field = new RealAttribute(selectedField); //Get field
        //Create a RangeMethod helper
        RangeMethod rangesHelper = new RangeMethod(layer.getDataSource(), field, numberOfClasses);

        //Calculate ranges based on the statIndex
        if (selectedField != null) {
            try {
                switch(statIndex){
                    case AVERAGE:
                       rangesHelper.disecMean();
                       ranges = rangesHelper.getRanges();
                       break;
                    case JENKS:
                        rangesHelper.disecNaturalBreaks();
                        ranges = rangesHelper.getRanges();
                      break;
                    case QUANTITY:
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
    public int[] getNumberOfClassesAllowed(){
        switch(statIndex){
                case AVERAGE: //2, 4 and 8 only alowed
                    return new int[]{2, 4, 8};
                default:
                    int[] nonRestricted = new int[defaultClassNumbers];
                    for(int i= 0; i < defaultClassNumbers; i++)
                        nonRestricted[i] = i + 1;
                    return nonRestricted;
            }
    }

    /*
     * Set the number of classes to create in classification
     * Note: AVERAGE will only accept 2, 4 or 8 classes
     */
    public void setNbrClasses(int NbrClasses){
        if(NbrClasses != numberOfClasses) //Check for change before updating
        {
            //Check for restrictions
            switch(statIndex){
                case AVERAGE: //2, 4 and 8 only alowed
                    if(NbrClasses == 2 || NbrClasses == 4 || NbrClasses == 8)
                        numberOfClasses = NbrClasses;
                    break;
                default:
                    numberOfClasses = NbrClasses;
                    break;
            }
            try{
                 resetRanges(); //Refresh/update ranges
            }catch (DriverException ex) {
                 Logger.getLogger(CreateChoroplethPlugIn.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /*
     * Set the field index (data field)
     */
    public void setFieldIndex(int FieldIndex){
        if(FieldIndex != fieldIndex) //Check for change
        {
            //Set the field index
            fieldIndex = FieldIndex;
            try{
                resetRanges(); //Reset and recalculate ranges
            }catch (DriverException ex) {
                    Logger.getLogger(CreateChoroplethPlugIn.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /*
     * Set the Statistic (classification) method
     */
    public void setStatisticMethod(StatisticMethod StatisticIndex){
        if(StatisticIndex != statIndex) //Check for change
        {
            //Set the statistic method
            statIndex = StatisticIndex;
            try{
                resetRanges(); //Reset and recalculate ranges
            }catch (DriverException ex) {
                    Logger.getLogger(CreateChoroplethPlugIn.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public double[] getSortedData() throws ParameterException{
        String selectedField = fields.get(fieldIndex); //Get field name
        RealAttribute field = new RealAttribute(selectedField); //Get field
        try{
            return ClassificationUtils.getSortedValues(layer.getDataSource(), field);
        }catch(DriverException ex){
            Logger.getLogger(CreateChoroplethPlugIn.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<String> getAliases(){
        return aliases;
    }

    public void setAlias(String value, int index){
        if(index < aliases.size())
            aliases.set(index, value);
    }

    public Color getBeginColor(){
        return beginColor;
    }

    public void setBeginColor(Color value){
        beginColor = value;
    }

    public Color getEndColor(){
        return endColor;
    }

    public void setEndColor(Color value){
        endColor = value;
    }

    public Color[] getClassesColors(){
        return classesColors;
    }

    public Color getClassColor(int index){
        if(index < classesColors.length)
            return classesColors[index];
        return null;
    }

    public void setClassColor(Color value, int index){
        if(index < classesColors.length)
            classesColors[index] = value;
    }

    public StatisticMethod getStatisticMethod(){
        return statIndex;
    }

    public List<JSE_Datas> getDatas(){
        return null;
    }

    public List<JSE_Steps> getSteps(){
        return null;
    }

    public RealAttribute getField()
    {
        return new RealAttribute(fields.get(fieldIndex));
    }

    public List<String> getFields(){
         return fields;
    }

    public Range[] getRange(){
         return ranges;
    }

    public Value getValue()
    {
        return value;
    }

    @Override
    public URL getIconURL() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getTitle() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String initialize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String postProcess() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String validateInput() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Component getComponent() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getInfoText() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
