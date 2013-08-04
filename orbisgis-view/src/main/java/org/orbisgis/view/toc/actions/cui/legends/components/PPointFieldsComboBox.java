package org.orbisgis.view.toc.actions.cui.legends.components;

import org.gdms.data.DataSource;
import org.orbisgis.legend.thematic.proportional.ProportionalPoint;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;

/**
 * Field combo box for Proportional Points.
 *
 * @author Adam Gouge
 */
public class PPointFieldsComboBox extends ProportionalFieldsComboBox {

    /**
     * This constructor is private so that we use the {@link #createInstance}
     * method to initialize the combo box properly.
     *
     * @param ds      DataSource
     * @param legend  Legend
     * @param preview Preview
     */
    private PPointFieldsComboBox(DataSource ds,
                                 ProportionalPoint legend,
                                 CanvasSE preview) {
        super(ds, legend, preview);
    }

    /**
     * Create and initialize a new {@link PPointFieldsComboBox}.
     *
     * @param ds      DataSource
     * @param legend  Legend
     * @param preview Preview
     * @return A newly initialized field combo box for proportional points
     */
    public static PPointFieldsComboBox createInstance(
            DataSource ds,
            ProportionalPoint legend,
            CanvasSE preview) {
        PPointFieldsComboBox box =
                new PPointFieldsComboBox(ds, legend, preview);
        box.init();
        return box;
    }

    @Override
    protected void setFirstAndSecondValues(double[] minAndMax) {
        // TODO: Why do we have to use Math.sqrt? (Otherwise the preview is
        // too small.)
        getLegend().setFirstData(Math.sqrt(minAndMax[0]));
        getLegend().setSecondData(Math.sqrt(minAndMax[1]));
    }
}
