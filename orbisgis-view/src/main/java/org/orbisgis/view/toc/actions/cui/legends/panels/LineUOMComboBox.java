package org.orbisgis.view.toc.actions.cui.legends.panels;

import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.legend.thematic.*;
import org.orbisgis.legend.thematic.map.MappedLegend;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;

/**
 * Created with IntelliJ IDEA.
 * User: adam
 * Date: 26/07/13
 * Time: 10:06
 * To change this template use File | Settings | File Templates.
 */
public class LineUOMComboBox<K, U extends LineParameters> extends UOMComboBox<K, U> {

    public LineUOMComboBox(MappedLegend<K, U> legend,
                           CanvasSE preview,
                           TablePanel<K, U> tablePanel) {
        super(legend, preview, tablePanel);
        setSelectedItem(legend.getStrokeUom());
    }

    @Override
    protected void updatePreview() {
        legend.setStrokeUom(Uom.fromString((String) getSelectedItem()));
        Util.updatePreview(legend, preview, tablePanel);
    }
}
