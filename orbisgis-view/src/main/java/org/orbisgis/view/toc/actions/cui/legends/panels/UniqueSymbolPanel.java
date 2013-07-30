package org.orbisgis.view.toc.actions.cui.legends.panels;

import net.miginfocom.swing.MigLayout;
import org.orbisgis.legend.Legend;
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
public class UniqueSymbolPanel extends JPanel {

    protected Legend legend;
    protected CanvasSE preview;

    public UniqueSymbolPanel(Legend legend,
                             CanvasSE preview) {
        super(new MigLayout("wrap 2", AbstractFieldPanel.COLUMN_CONSTRAINTS));
        this.legend = legend;
        this.preview = preview;
    }

    /**
     * Rebuild the {@code CanvasSE} instance used to display a preview of
     * the current symbol.
     */
    protected void initPreview() {
        if (legend != null) {
            preview = new CanvasSE(legend.getSymbolizer());
            preview.imageChanged();
        }
    }
}
