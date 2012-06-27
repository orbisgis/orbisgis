package org.orbisgis.view.toc.actions.cui.choropleth.dataModel;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.gdms.data.schema.Metadata;
import org.gdms.driver.DriverException;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.renderer.classification.ClassificationUtils;
import org.orbisgis.core.renderer.classification.Range;
import org.orbisgis.core.renderer.classification.RangeMethod;
import org.orbisgis.core.renderer.se.parameter.real.RealAttribute;
import org.orbisgis.view.toc.actions.cui.freqChart.dataModel.FreqChartDataModel;


/**
 * ChoroplethDataModel
 * the Choropleth data model
 * @author sennj
 */
public class ChoroplethDataModel {

    private final ILayer layer;
    private StatisticMethod statIndex;
    private String fieldName;

    public enum StatisticMethod {

        QUANTILES, MEAN, JENKS, MANUAL
    }

    /**
     * ChoroplethDataModel
     * @param layer
     */
    public ChoroplethDataModel(ILayer layer) {
        this.layer = layer;
        this.statIndex = StatisticMethod.MANUAL;
    }

    public void setField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getField() {
        return fieldName;
    }

    public double[] getData() {
        RealAttribute field = new RealAttribute(fieldName); //Get field
        try {
            return ClassificationUtils.getSortedValues(layer.getDataSource(), field);
        } catch (Exception ex) {
            Logger.getLogger(ChoroplethDataModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * getFields
     * return the disponible fields
     * @return the fields
     */
    public List<String> getFields() {

        ArrayList<String> fields = new ArrayList<String>();

        Metadata metadata;
        try {
            metadata = layer.getDataSource().getMetadata();
            String retainedFiledName = null;
            for (int i = 0; i < metadata.getFieldCount(); i++) {
                int currentType = metadata.getFieldType(i).getTypeCode();

                if (currentType == 4 || (currentType >= 16 && currentType <= 256)) {
                    retainedFiledName = metadata.getFieldName(i);
                    fields.add(retainedFiledName);
                }
            }
        } catch (DriverException ex) {
            Logger.getLogger(ChoroplethDataModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fields;
    }

    /**
     * getStatisticMethod
     * Gets the selected statistic method
     * @return the selected statistic method
     */
    public StatisticMethod getStatisticMethod() {
        return statIndex;
    }

    /**
     * Set the Statistic (classification) method
     * @param freqChartDataModel
     * @param statisticIndex
     */
    public void setStatisticMethod(FreqChartDataModel freqChartDataModel, StatisticMethod statisticIndex) {

        int numberOfSeuil = freqChartDataModel.getNbSeuil();

        if (statisticIndex != statIndex) //Check for change
        {
            //Set the statistic method
            statIndex = statisticIndex;
            resetRanges(freqChartDataModel, numberOfSeuil); //Reset and recalculate ranges
        }
    }

    /**
     * resetRanges
     * @param freqChartDataModel
     * @param numberOfSeuil
     */
    public void resetRanges(FreqChartDataModel freqChartDataModel, int numberOfSeuil) {

        RealAttribute field = new RealAttribute(fieldName); //Get field
        //Create a RangeMethod helper
        try {
            RangeMethod rangesHelper = new RangeMethod(layer.getDataSource(), field, numberOfSeuil);
            switch (statIndex) {
                case MEAN:
                    rangesHelper.disecMean();
                    break;
                case JENKS:
                    rangesHelper.disecNaturalBreaks();
                    break;
                case QUANTILES:
                    rangesHelper.disecQuantiles();
                    break;
                case MANUAL:
                    rangesHelper.disecEquivalences();
                    break;
            }
            Range[] ranges = rangesHelper.getRanges();

            List<double[]> seuilList = new ArrayList<double[]>();

            for (int i = 1; i <= ranges.length; i++) {
                Range range = ranges[i - 1];
                double[] rangeElem = new double[2];
                rangeElem[0] = range.getMinRange();
                rangeElem[1] = range.getMaxRange();
                seuilList.add(rangeElem);
            }

            freqChartDataModel.setSeuilList(seuilList);

        } catch (Exception ex) {
            Logger.getLogger(ChoroplethDataModel.class.getName()).log(Level.SEVERE, null, ex);
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
    public int[] getNumberOfClassesAllowed(FreqChartDataModel freqChartDataModel, StatisticMethod statmethod) {
        switch (statmethod) {
            case MEAN: //2, 4 and 8 only alowed
                return new int[]{2, 4, 8};
            default:
                int nbMaxSeuil = freqChartDataModel.getMaxSeuil();
                int[] nonRestricted = new int[nbMaxSeuil];
                for (int i = 0; i < nbMaxSeuil; i++) {
                    nonRestricted[i] = i + 1;
                }
                return nonRestricted;
        }
    }
}
