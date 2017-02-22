/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.view.toc.actions.cui.legend.model;



import org.orbisgis.sif.components.renderers.TableLaFCellRenderer;

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
