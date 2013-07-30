package org.orbisgis.view.toc.actions.cui.legends.panels;

import net.miginfocom.swing.MigLayout;
import org.orbisgis.legend.Legend;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.orbisgis.view.toc.actions.cui.legends.AbstractFieldPanel;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: adam
 * Date: 30/07/13
 * Time: 11:22
 * To change this template use File | Settings | File Templates.
 */
public abstract class UniqueSymbolPanel extends JPanel {

    protected LegendStructure legend;
    protected CanvasSE preview;

    public UniqueSymbolPanel(LegendStructure legend,
                             CanvasSE preview,
                             String title) {
        super(new MigLayout("wrap 2", AbstractFieldPanel.COLUMN_CONSTRAINTS));
        setBorder(BorderFactory.createTitledBorder(title));
        this.legend = legend;
        this.preview = preview;
        if (preview == null && legend != null) {
            initPreview();
        }
    }

    /**
     * Gets the legend.
     *
     * @return The legend.
     */
    protected LegendStructure getLegend() {
        return legend;
    }

    /**
     * Rebuild the {@code CanvasSE} instance used to display a preview of
     * the current symbol.
     */
    protected void initPreview() {
        if (legend != null) {
            preview = new CanvasSE(((Legend) legend).getSymbolizer());
            preview.imageChanged();
        }
    }

    /**
     * Add the components to the UI.
     */
    protected abstract void addComponents();
}
