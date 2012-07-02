package org.orbisgis.view.toc.actions.cui.choropleth.gui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Class that render the cell from the range table
 * @author sennj
 */
class ColorCellRenderer extends DefaultTableCellRenderer {

    /**
     * This method is used to configure the renderer before drawing
     * This method can threw a ClassCastException
     * @param table the JTable
     * @param value the value of the cell to be rendered
     * @param isSelected true if the cell is to be rendered with the selection highlighted; otherwise false
     * @param hasFocus if true, render cell appropriately
     * @param row the row index
     * @param column the column index
     * @return the component used for drawing the cell.
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        Color color = (Color) value;

        this.setText("");
        this.setBackground(color);

        return this;
    }
}