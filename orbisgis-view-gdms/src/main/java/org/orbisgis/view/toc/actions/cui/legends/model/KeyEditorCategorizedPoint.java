package org.orbisgis.view.toc.actions.cui.legends.model;

import org.orbisgis.legend.thematic.PointParameters;
import org.orbisgis.legend.thematic.categorize.AbstractCategorizedLegend;

import javax.swing.*;
import java.awt.*;

/**
 * @author Alexis Guéganno
 */
public class KeyEditorCategorizedPoint extends KeyEditorCategorized<PointParameters> {

    /**
     * Build a cell editor dedicated to the management of keys in a recoded legend.
     */
    public KeyEditorCategorizedPoint() {
        super();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        setVal((Double) value);
        AbstractCategorizedLegend<PointParameters> uv = (AbstractCategorizedLegend<PointParameters>)
                ((TableModelCatPoint) table.getModel()).getMappedLegend();
        setLegend(uv);
        getField().setText(getVal().toString());
        return getField();
    }

}