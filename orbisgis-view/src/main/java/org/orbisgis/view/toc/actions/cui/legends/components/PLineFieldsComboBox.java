package org.orbisgis.view.toc.actions.cui.legends.components;

import org.gdms.data.DataSource;
import org.orbisgis.legend.thematic.proportional.ProportionalLine;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;

/**
 * Field combo box for Proportional Lines.
 *
 * @author Adam Gouge
 */
public class PLineFieldsComboBox extends ProportionalFieldsComboBox {

    /**
     * This constructor is private so that we use the {@link #createInstance}
     * method to initialize the combo box properly.
     *
     * @param ds      DataSource
     * @param legend  Legend
     * @param preview Preview
     */
    private PLineFieldsComboBox(DataSource ds,
                                ProportionalLine legend,
                                CanvasSE preview) {
        super(ds, legend, preview);
    }

    /**
     * Create and initialize a new {@link PLineFieldsComboBox}.
     *
     * @param ds      DataSource
     * @param legend  Legend
     * @param preview Preview
     * @return A newly initialized field combo box for proportional lines
     */
    public static PLineFieldsComboBox createInstance(
            DataSource ds,
            ProportionalLine legend,
            CanvasSE preview) {
        PLineFieldsComboBox box =
                new PLineFieldsComboBox(ds, legend, preview);
        box.init();
        return box;
    }

    @Override
    protected void setFirstAndSecondValues(double[] minAndMax) {
        getLegend().setFirstData(minAndMax[0]);
        getLegend().setSecondData(minAndMax[1]);
    }
}
