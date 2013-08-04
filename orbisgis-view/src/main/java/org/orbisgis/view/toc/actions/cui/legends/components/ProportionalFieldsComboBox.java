package org.orbisgis.view.toc.actions.cui.legends.components;

import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.classification.ClassificationUtils;
import org.orbisgis.core.renderer.se.parameter.real.RealAttribute;
import org.orbisgis.legend.IInterpolationLegend;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;

import java.util.HashMap;
import java.util.Map;

/**
 * Root class for proportional point/line field combo boxes.
 *
 * @author Adam Gouge
 */
public abstract class ProportionalFieldsComboBox extends NumericalFieldsComboBox {

    private static final Logger LOGGER =
            Logger.getLogger(ProportionalFieldsComboBox.class);

    protected CanvasSE preview;

    /**
     * Constructor
     *
     * @param ds      DataSource
     * @param legend  Legend
     * @param preview Preview
     */
    protected ProportionalFieldsComboBox(DataSource ds,
                                         IInterpolationLegend legend,
                                         CanvasSE preview) {
        super(ds, legend);
        this.preview = preview;
    }

    /**
     * Gets the legend.
     *
     * @return The legend
     */
    protected IInterpolationLegend getLegend() {
        return (IInterpolationLegend) legend;
    }

    @Override
    protected void updateField(String name) {
        // Set the lookup field name.
        legend.setLookupFieldName(name);
        try {
            // Set the first and second data
            double[] minAndMax = ClassificationUtils
                    .getMinAndMax(ds, new RealAttribute(name));
            setFirstAndSecondValues(minAndMax);
            // Set the sample data source for the preview.
            Map<String, Object> sample = new HashMap<String, Object>();
            sample.put(name, minAndMax[1]);
            // Todo: Remove this. It is slow.
            preview.setSampleDatasource(sample);
            // Update the preview.
            preview.setDisplayed(true);
            preview.imageChanged();
        } catch (Exception e) {
            LOGGER.warn("Cannot get min and max values.");
        }
    }

    /**
     * Set the first and second values of the legend.
     *
     * @param minAndMax A two-item double array containing the min and max
     *                  values, in this order.
     */
    protected abstract void setFirstAndSecondValues(double[] minAndMax);
}
