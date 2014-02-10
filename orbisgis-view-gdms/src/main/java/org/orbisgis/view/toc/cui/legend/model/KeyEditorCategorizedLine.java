package org.orbisgis.view.toc.actions.cui.legend.model;

import org.orbisgis.legend.thematic.LineParameters;
import org.orbisgis.legend.thematic.categorize.AbstractCategorizedLegend;

import javax.swing.*;
import java.awt.*;

/**
 * @author Alexis Guéganno
 */
public class KeyEditorCategorizedLine extends KeyEditorCategorized<LineParameters> {

    /**
     * Build a cell editor dedicated to the management of keys in a recoded legend.
     */
    public KeyEditorCategorizedLine() {
        super();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        setVal((Double) value);
        AbstractCategorizedLegend<LineParameters> uv = (AbstractCategorizedLegend<LineParameters>)
                ((TableModelCatLine) table.getModel()).getMappedLegend();
        setLegend(uv);
        getField().setText(getVal().toString());
        return getField();
    }

}