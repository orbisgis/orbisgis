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

import org.orbisgis.legend.thematic.map.MappedLegend;
import org.orbisgis.sif.components.renderers.TableLaFCellRenderer;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * This renderer is used to draw previews of the symbols defined in unique value analysis. It draws a preview of the
 * symbol that will be associated to a particular value from the original data.
 * @author alexis
 */
public class PreviewCellRenderer extends TableLaFCellRenderer {
    private final MappedLegend legend;

    /**
     * Set listener to L&F events
     * {@link javax.swing.JTable#getDefaultRenderer}
     *
     * @param table Where the listener has to be installed
     * @param type  Default cell renderer for this columnClass
     */
    public PreviewCellRenderer(JTable table, Class<?> type, MappedLegend sym) {
        super(table, type);
        legend = sym;
    }


    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel lab = (JLabel)lookAndFeelRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if(value instanceof String || value instanceof Double){
            lab.setText("");
            CanvasSE cse = new CanvasSE(legend.getSymbolizer(),CanvasSE.WIDTH/2, CanvasSE.HEIGHT/2);
            Map<String,Object> map = new HashMap<String,Object>();
            map.put(legend.getLookupFieldName(), value);
            cse.setSampleDatasource(map);
            ImageIcon ii = new ImageIcon(cse.getImage());
            lab.setIcon(ii);
            lab.setOpaque(true);
            lab.setPreferredSize(new Dimension(CanvasSE.WIDTH/2, CanvasSE.HEIGHT/2));
        }
        return lab;
    }
}
