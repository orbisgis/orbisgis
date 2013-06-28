package org.orbisgis.view.toc.actions.cui.legends.model;

import org.orbisgis.legend.thematic.map.MappedLegend;
import org.orbisgis.view.components.renderers.TableLaFCellRenderer;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * @author Alexis Guéganno
 */
public class KeyCellRenderer extends TableLaFCellRenderer {
    private NumberFormat formatter;
    private final MappedLegend symbolizer;

    /**
     * Set listener to L&F events
     * {@link javax.swing.JTable#getDefaultRenderer}
     *
     * @param table Where the listener has to be installed
     * @param type  Default cell renderer for this columnClass
     */
    public KeyCellRenderer(JTable table, Class<?> type, MappedLegend sym) {
        super(table, type);
        symbolizer = sym;
        formatter = NumberFormat.getInstance(Locale.getDefault());
        formatter.setGroupingUsed(false);
        formatter.setMaximumFractionDigits(TableModelInterval.DIGITS_NUMBER);
    }


    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel lab = (JLabel)lookAndFeelRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if(value instanceof Double){
            lab.setText(formatter.format(value));
        }
        return lab;
    }
}
