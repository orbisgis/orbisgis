package org.orbisgis.view.toc.actions.cui.legends.ui;

import org.orbisgis.legend.Legend;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;

/**
 * Root class for non-classification UIs.
 *
 * @author Adam Gouge
 */
public abstract class PnlNonClassification extends AbstractFieldPanel {

    private String id;
    private CanvasSE preview;

    // *********************** AbstractFieldPanel *************************
    @Override
    public void initPreview() {
        Legend leg = getLegend();
        if (leg != null) {
            preview = new CanvasSE(leg.getSymbolizer());
            preview.imageChanged();
        }
    }

    @Override
    public CanvasSE getPreview() {
        if (preview == null) {
            initPreview();
        }
        return preview;
    }

    // ************************ ISELegendPanel ***********************
    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String validateInput() {
        return null;
    }
}
