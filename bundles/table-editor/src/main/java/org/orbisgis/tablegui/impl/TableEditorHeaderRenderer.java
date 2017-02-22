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
package org.orbisgis.tablegui.impl;

import org.orbisgis.tablegui.icons.TableEditorIcon;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;

/**
 * If the column is a primary key, this class wrap the default renderer in order to show a key icon at the column header.
 * The text of the column is bold if the icon can not be allocated.
 * @author Nicolas Fortin
 */
public class TableEditorHeaderRenderer implements TableCellRenderer {
    protected TableCellRenderer lookAndFeelRenderer;
    private boolean isKey = false;
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
        boolean failedToUseImage = true;
        if(rendering instanceof JLabel && isKey) {
            JLabel renderLabel = (JLabel)rendering;
            Icon defaultIcon = renderLabel.getIcon();
            if(defaultIcon == null) {
                ImageIcon keyIcon = TableEditorIcon.getIcon("key");
                keyIcon.setDescription(DESCR_IMAGE);
                renderLabel.setIcon(keyIcon);
                failedToUseImage = false;
            } else if(defaultIcon instanceof ImageIcon && !DESCR_IMAGE.equals(((ImageIcon) defaultIcon).getDescription())) {
                ImageIcon defaultImageIcon = (ImageIcon) defaultIcon;
                ImageIcon resultIcon = concatenateImages(TableEditorIcon.getIcon("key"), defaultImageIcon);
                resultIcon.setDescription(DESCR_IMAGE);
                renderLabel.setIcon(resultIcon);
                failedToUseImage = false;
            } else if(defaultIcon instanceof ImageIcon) {
                failedToUseImage = false;
            }
        }
        // Use Bold instead of Key icon if icon is not accepted by the component
        if(failedToUseImage && isKey && rendering.getFont() != null) {
            rendering.setFont(rendering.getFont().deriveFont(Font.BOLD));
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

