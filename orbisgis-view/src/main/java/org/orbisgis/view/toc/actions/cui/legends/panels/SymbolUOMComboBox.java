package org.orbisgis.view.toc.actions.cui.legends.panels;

import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.legend.thematic.LineParameters;
import org.orbisgis.legend.thematic.map.MappedLegend;
import org.orbisgis.legend.thematic.uom.SymbolUom;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;

/**
 * Created with IntelliJ IDEA.
 * User: adam
 * Date: 26/07/13
 * Time: 14:28
 * To change this template use File | Settings | File Templates.
 */
public class SymbolUOMComboBox<K, U extends LineParameters> extends UOMComboBox<K, U> {

    public SymbolUOMComboBox(SymbolUom legend,
                             CanvasSE preview,
                             TablePanel<K, U> tablePanel) {
        super((MappedLegend) legend, preview, tablePanel);
        setSelectedItem(legend.getSymbolUom());
    }

    @Override
    protected void updatePreview() {
        ((SymbolUom) legend).setSymbolUom(Uom.fromString((String) getSelectedItem()));
        Util.updatePreview((MappedLegend) legend, preview, tablePanel);
    }
}