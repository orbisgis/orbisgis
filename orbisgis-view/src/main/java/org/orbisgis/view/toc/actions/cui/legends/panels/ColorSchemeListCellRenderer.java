/*
 * The Unified Mapping Platform (JUMP) is an extensible, interactive GUI 
 * for visualizing and manipulating spatial features with geometry and attributes.
 *
 * Copyright (C) 2003 Vivid Solutions
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * For more information, contact:
 *
 * Vivid Solutions
 * Suite #1A
 * 2328 Government Street
 * Victoria BC  V8T 5G5
 * Canada
 *
 * (250)385-6040
 * www.vividsolutions.com
 */
package org.orbisgis.view.toc.actions.cui.legends.panels;
import java.awt.*;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.*;

/**
 * This dedicated ListCellRenderer intends to ease the embedding of ColorSchemes in list and combo box.</p>
 * <p>This file was originally integrated in the openJUMP software.</p>
 * @author OpenJump
 * @author Alexis Guéganno
 */
public class ColorSchemeListCellRenderer
        extends JPanel
        implements ListCellRenderer {

    private JLabel colorPanel1 = new JLabel();
    private JLabel colorPanel2 = new JLabel();
    private JLabel colorPanel3 = new JLabel();
    private JLabel colorPanel4 = new JLabel();
    private JLabel colorPanel5 = new JLabel();
    private GridBagLayout gridBagLayout1 = new GridBagLayout();
    private JLabel label = new JLabel();

    /**
     * Builds a new renderer.
     */
    public ColorSchemeListCellRenderer() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        String name = (String) value;
        JPanel pan;
        if(isSelected){
            pan = new ColorSchemeListCell(name, isSelected, list.getSelectionForeground(), list.getSelectionBackground());
        } else {
            pan = new ColorSchemeListCell(name, isSelected, list.getForeground(), list.getBackground());
        }
        return pan;
    }

    /**
     * Gets the ColorScheme whose name is {@code name}
     * @param name The name of the ColorScheme.
     * @return The ColorScheme
     */
    protected ColorScheme colorScheme(String name) {
        return ColorScheme.create(name);
    }

    /**
     * Sets the foreground and background colors of the given JLabel to {@code fillColor}.
     * @param colorPanel The JLabel we want to color.
     * @param fillColor The Color.
     */
    private void color(JLabel colorPanel, Color fillColor) {
        colorPanel.setSize(new Dimension(8,8));
        colorPanel.setOpaque(true);
        colorPanel.setBackground(fillColor);
        colorPanel.setForeground(fillColor);
    }

    /**
     * Initializes the CellRenderer.
     * @throws Exception
     */
    private void jbInit() throws Exception {
        this.setLayout(gridBagLayout1);
        label.setText("jLabel1");
        this.add(
                colorPanel1,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                        ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 2, 0, 0), 0, 0));
        this.add(
                colorPanel2,
                new GridBagConstraints(
                        1,
                        0,
                        1,
                        1,
                        0.0,
                        0.0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.NONE,
                        new Insets(0, 0, 0, 0),
                        0,
                        0));
        this.add(
                colorPanel3,
                new GridBagConstraints(
                        2,
                        0,
                        1,
                        1,
                        0.0,
                        0.0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.NONE,
                        new Insets(0, 0, 0, 0),
                        0,
                        0));
        this.add(
                colorPanel4,
                new GridBagConstraints(
                        3,
                        0,
                        1,
                        1,
                        0.0,
                        0.0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.NONE,
                        new Insets(0, 0, 0, 0),
                        0,
                        0));
        this.add(
                colorPanel5,
                new GridBagConstraints(
                        4,
                        0,
                        1,
                        1,
                        0.0,
                        0.0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.NONE,
                        new Insets(0, 0, 0, 0),
                        0,
                        0));
        this.add(
                label,
                new GridBagConstraints(5, 0, 1, 1, 1.0, 0.0
                        ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 0, 0), 0, 0));
    }

    /**
     * Workaround for bug 4238829 in the Java bug database:
     * "JComboBox containing JPanel fails to display selected item at creation time"
     */
    @Override
    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, y, w, h);
        validate();
    }

}
