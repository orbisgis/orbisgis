/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
 * PROP_LABEL
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.view.table;

import org.orbisgis.view.icons.OrbisGISIcon;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;

/**
 * @author Nicolas Fortin
 */
public class TableEditorHeaderRenderer implements TableCellRenderer {
    protected TableCellRenderer lookAndFeelRenderer;
    private boolean isKey = true;
    private static final String DESCR_IMAGE = "MERGED";

    /**
     * Update the native renderer.
     * Warning, Used only by PropertyChangeListener on UI property
     */
    public void updateLFRenderer() {
        lookAndFeelRenderer = new JTable().getTableHeader().getDefaultRenderer();
    }
    /**
     * Set listener to L&F events
     * {@link JTable#getDefaultRenderer}
     * @param table Where the listener has to be installed
     */
    public TableEditorHeaderRenderer(JTable table) {
        initialize(table);
    }

    private void initialize(JTable list) {
        updateLFRenderer();
        list.addPropertyChangeListener("UI",
                EventHandler.create(PropertyChangeListener.class, this, "updateLFRenderer"));
    }

    private ImageIcon concatenateImages(ImageIcon left, ImageIcon right) {
        // Merge icons
        BufferedImage image = new BufferedImage(left.getIconWidth() + right.getIconWidth(),
        Math.max(left.getIconHeight(), right.getIconHeight()), BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        g.drawImage(left.getImage(), 0, 0, null);
        g.drawImage(right.getImage(), left.getIconWidth(), 0, null);
        return new ImageIcon(image);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component rendering = lookAndFeelRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if(rendering instanceof JLabel && isKey) {
            JLabel renderLabel = (JLabel)rendering;
            Icon defaultIcon = renderLabel.getIcon();
            if(defaultIcon == null) {
                ImageIcon keyIcon = OrbisGISIcon.getIcon("key");
                keyIcon.setDescription(DESCR_IMAGE);
                renderLabel.setIcon(keyIcon);
            } else {
                if(!(defaultIcon instanceof ImageIcon)) {
                    // Generate Image from laf Icon drawer
                    BufferedImage image = new BufferedImage(defaultIcon.getIconWidth(), defaultIcon.getIconHeight(),
                            BufferedImage.TYPE_INT_ARGB);
                    Graphics g = image.getGraphics();
                    defaultIcon.paintIcon(renderLabel, g, 0, 0);
                    defaultIcon = new ImageIcon(image);
                }
                if(!DESCR_IMAGE.equals(((ImageIcon) defaultIcon).getDescription())) {
                    ImageIcon defaultImageIcon = (ImageIcon) defaultIcon;
                    ImageIcon resultIcon = concatenateImages(defaultImageIcon, OrbisGISIcon.getIcon("key"));
                    resultIcon.setDescription(DESCR_IMAGE);
                    renderLabel.setIcon(resultIcon);
                }
            }
        }
        return rendering;
    }

    /**
     * @param isKey True if the Table column is a primary key
     */
    public void setKey(boolean isKey) {
        this.isKey = isKey;
    }
}

