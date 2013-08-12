package org.orbisgis.view.toc.actions.cui.legends.ui;

import org.orbisgis.legend.Legend;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.xnap.commons.i18n.I18n;

/**
 * Root class for non-classification UIs. (That is, everything other than Value
 * and Interval Classifications).
 *
 * @author Adam Gouge
 */
public abstract class PnlNonClassification extends AbstractFieldPanel {

    private String id;
    private CanvasSE preview;

    public static final String BORDER_SETTINGS = I18n.marktr("Border settings");
    public static final String FILL_SETTINGS = I18n.marktr("Fill settings");
    public static final String MARK_SETTINGS = I18n.marktr("Mark settings");

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
}
