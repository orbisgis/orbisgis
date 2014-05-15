package org.orbisgis.view.toc.actions.cui.legend.model;

import org.orbisgis.view.components.renderers.TableLaFCellRenderer;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Renderer for keys of a MappedLegend.
 * @author Alexis Guéganno
 */
public class KeyCellRenderer extends TableLaFCellRenderer {
    private NumberFormat formatter;

    /**
     * Set listener to L&F events
     * {@link javax.swing.JTable#getDefaultRenderer}
     *
     * @param table Where the listener has to be installed
     * @param type  Default cell renderer for this columnClass
     */
    public KeyCellRenderer(JTable table, Class<?> type) {
        super(table, type);
        formatter = NumberFormat.getInstance(Locale.getDefault());
        formatter.setGroupingUsed(false);
        formatter.setMaximumFractionDigits(TableModelInterval.DIGITS_NUMBER);
    }


    @Override
    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column) {
        JLabel lab = (JLabel)
                lookAndFeelRenderer.getTableCellRendererComponent(
                        table,
                        value,
                        isSelected,
                        hasFocus,
                        row,
                        column);
        if(value instanceof Double){
            Double d = (Double) value;
            if(Math.abs(d) < 1.0){
                lab.setText(Double.toString(getRounded(d, TableModelInterval.DIGITS_NUMBER)));
            } else {
                lab.setText(formatter.format(value));
            }
        }
        lab.setHorizontalAlignment(SwingConstants.CENTER);
        return lab;
    }

    /**
     * Get an approximation of d that keeps n significant digits.
     * @param d The input number
     * @param n The number of significant digits we want.
     * @return The rounded value.
     */
    public static double getRounded(double d, int n){
        if(d == 0.0) {
             return 0.0;
        }
        final double powBase = Math.ceil(Math.log10(d < 0 ? -d: d));
        final int power = n - (int) powBase;
        final double magnitude = Math.pow(10, power);
        final long shifted = Math.round(d*magnitude);
        return shifted/magnitude;

    }
}
