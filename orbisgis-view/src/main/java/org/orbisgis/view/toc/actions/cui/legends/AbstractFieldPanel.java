/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.toc.actions.cui.legends;

import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.TypeFactory;
import org.gdms.driver.DriverException;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.components.ColorPicker;
import org.orbisgis.view.components.fstree.TreeNodeFileFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.EventHandler;

/**
 * Some useful methods that will be available for all thematic panels.
 * @author alexis
 */
public class AbstractFieldPanel extends JPanel {
    private static final Logger LOGGER = Logger.getLogger("gui."+AbstractFieldPanel.class);
    /**
     * Width used for the rectangles that displays the color parameters of the symbols.
     */
    public final static int FILLED_LABEL_WIDTH = 40;
    /**
     * Height used for the rectangles that displays the color parameters of the symbols.
     */
    public final static int FILLED_LABEL_HEIGHT = 20;

    /**
     * Initialize a {@code JComboBo} whose values are set according to the
     * not spatial fields of {@code ds}.
     * @param ds
     * @return
     */
    public JComboBox getFieldCombo(DataSource ds){
        JComboBox combo = new JComboBox();
        if(ds != null){
            try {
                Metadata md = ds.getMetadata();
                int fc = md.getFieldCount();
                for (int i = 0; i < fc; i++) {
                    if(!TypeFactory.isSpatial(md.getFieldType(i).getTypeCode())){
                        combo.addItem(md.getFieldName(i));
                    }
                }
            } catch (DriverException ex) {
                LOGGER.error(ex);
            }
        }
        return combo;
    }

    /**
     * Initialize a {@code JComboBo} whose values are set according to the
     * numeric fields of {@code ds}.
     * @param ds
     * @return
     */
    public JComboBox getNumericFieldCombo(DataSource ds){
        JComboBox combo = new JComboBox();
        if(ds != null){
            try {
                Metadata md = ds.getMetadata();
                int fc = md.getFieldCount();
                for (int i = 0; i < fc; i++) {
                    if(TypeFactory.isNumerical(md.getFieldType(i).getTypeCode())){
                        combo.addItem(md.getFieldName(i));
                    }
                }
            } catch (DriverException ex) {
                LOGGER.error(ex);
            }
        }
        return combo;
    }

    /**
     * Get a JLabel of dimensions {@link PnlUniqueSymbolSE#FILLED_LABEL_WIDTH} and {@link PnlUniqueSymbolSE#FILLED_LABEL_HEIGHT}
     * opaque and with a background of Color {@code c}.
     * @param c The background color of the label we want.
     * @return the label with c as a background colour.
     */
    public JLabel getFilledLabel(Color c){
        JLabel lblFill = new JLabel();
        lblFill.setBackground(c);
        lblFill.setBorder(BorderFactory.createLineBorder(Color.black));
        lblFill.setPreferredSize(new Dimension(FILLED_LABEL_WIDTH, FILLED_LABEL_HEIGHT));
        lblFill.setMaximumSize(new Dimension(FILLED_LABEL_WIDTH, FILLED_LABEL_HEIGHT));
        lblFill.setOpaque(true);
        MouseListener ma = EventHandler.create(MouseListener.class, this, "chooseFillColor", "", "mouseClicked");
        lblFill.addMouseListener(ma);
        return lblFill;
    }

    /**
     * Recursively enables or disables all the components contained in the
     * containers of {@code comps}.
     * @param enable
     * @param comp
     */
    protected void setFieldState(boolean enable, Component comp){
        comp.setEnabled(enable);
        if(comp instanceof Container){
            Component[] comps = ((Container)comp).getComponents();
            for(Component c: comps){
                setFieldState(enable, c);
            }
        }
    }

    /**
     * This method will let the user choose a color that will be set as the
     * background of the source of the event.
     * @param e
     */
    public void chooseFillColor(MouseEvent e) {
        Component source = (Component)e.getSource();
        if(source.isEnabled()){
            ColorPicker picker = new ColorPicker();
            if (UIFactory.showDialog(picker,false, true)) {
                Color color = picker.getColor();
                source.setBackground(color);
            }
        }
    }
}
