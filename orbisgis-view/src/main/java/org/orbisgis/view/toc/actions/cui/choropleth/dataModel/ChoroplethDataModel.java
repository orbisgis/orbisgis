package org.orbisgis.view.toc.actions.cui.choropleth.dataModel;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.renderer.classification.ClassificationUtils;
import org.orbisgis.core.renderer.classification.Range;
import org.orbisgis.core.renderer.classification.RangeMethod;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.RealAttribute;
import org.orbisgis.view.toc.actions.cui.freqChart.dataModel.FreqChartDataModel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * ChoroplethDataModel
 * the Choropleth data model
 * @author sennj
 */
public class ChoroplethDataModel {

    /** The display layer */
    private final ILayer layer;
    /** The selected statistic method */
    private StatisticMethod statIndex;
    /** The selected field name */
    private String fieldName;
    /** Logger */
    private static final Logger LOGGER = Logger.getLogger(ChoroplethDataModel.class);
    /** I18n */
    private final static I18n I18N = I18nFactory.getI18n(ChoroplethDataModel.class);

    /** The statistic method enum */
    public enum StatisticMethod {

        QUANTILES, MEAN, JENKS, MANUAL
    }

    /**
     * ChoroplethDataModel constructor
     * @param layer the display panel
     */
    public ChoroplethDataModel(ILayer layer) {
        this.layer = layer;
        this.statIndex = StatisticMethod.MANUAL;
    }

    /**
     * Set the field name
     * @param fieldName
     */
    public void setField(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * Get the field name
     * @return fieldName
     */
    public String getField() {
        return fieldName;
    }

    /**
     * Get the data
     * @return the data
     */
    public double[] getData() {
        RealAttribute field = new RealAttribute(fieldName); //Get field

        try {
            return ClassificationUtils.getSortedValues(layer.getDataSource(), field);
        } catch (DriverException ex) {
            LOGGER.error(I18N.tr("Unable to load the map data"), ex);
        } catch (ParameterException ex) {
            LOGGER.error(I18N.tr("Unable to load the map data"), ex);
        }

        return null;
    }

    /**
     * Get the disponible Fields
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

                if (currentType == Type.BYTE || (currentType >= Type.DOUBLE && currentType <= Type.SHORT)) {
                    retainedFiledName = metadata.getFieldName(i);
                    fields.add(retainedFiledName);
                }
            }
        } catch (DriverException ex) {
            LOGGER.error(I18N.tr("Unable to load the map data header"), ex);
        }
        return fields;
    }

    /**
     * Gets the selected statistic method
     * @return The selected statistic method
     */
    public StatisticMethod getStatisticMethod() {
        return statIndex;
    }

    /**
     * Set the Statistic (classification) method
     * @param freqChartDataModel The frequence chart data model
     * @param statisticIndex The selected statistic method
     */
    public void setStatisticMethod(FreqChartDataModel freqChartDataModel, StatisticMethod statisticIndex) {

        int numberOfThreshold = freqChartDataModel.getThresholdNumber();

        if (statisticIndex != statIndex) //Check for change
        {
            //Set the statistic method
            statIndex = statisticIndex;
            resetRanges(freqChartDataModel, numberOfThreshold); //Reset and recalculate ranges
        }
    }

    /**
     * Reset the ranges
     * @param freqChartDataModel The frequence chart data model
     * @param numberOfThreshold The number of threshold
     */
    public void resetRanges(FreqChartDataModel freqChartDataModel, int numberOfThreshold) {

        try {
            RealAttribute field = new RealAttribute(fieldName); //Get field
            //Create a RangeMethod helper
            RangeMethod rangesHelper = new RangeMethod(layer.getDataSource(), field, numberOfThreshold);
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
            List<List<Double>> seuilList = new ArrayList<List<Double>>();
            for (int i = 1; i <= ranges.length; i++) {
                Range range = ranges[i - 1];
                List<Double> rangeElem = new ArrayList<Double>();
                rangeElem.add(range.getMinRange());
                rangeElem.add(range.getMaxRange());
                seuilList.add(rangeElem);
            }
            freqChartDataModel.setThresholdList(seuilList);
        } catch (ParameterException ex) {
            LOGGER.error(I18N.tr("Unable to reset the ranges"), ex);
        } catch (DriverException ex) {
            LOGGER.error(I18N.tr("Unable to reset the ranges"), ex);
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
                int nbMaxThreshold = freqChartDataModel.getMaxThreshold();
                int[] nonRestricted = new int[nbMaxThreshold];
                for (int i = 0; i < nbMaxThreshold; i++) {
                    nonRestricted[i] = i + 1;
                }
                return nonRestricted;
        }
    }
}
