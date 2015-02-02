/**
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
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.tablegui.impl;

import java.awt.Component;
import java.awt.Graphics;
import java.beans.EventHandler;
import javax.swing.AbstractListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelListener;
import org.orbisgis.sif.components.renderers.ListLaFRenderer;

/**
 * This list is set at the left of the table to show the row number.
 * @author Erwan Bocher
 */
public class TableRowHeader extends JList<Integer> {
        private static final long serialVersionUID = 1L;
    private final JTable table;
        private final RowHeaderListModel model;

        public TableRowHeader(JTable table) {
                this.table = table;
                setFixedCellWidth(1); // Will be reset on syncRowCount
                model = new RowHeaderListModel();
                setModel(model);
                setFocusable(false);
                setFixedCellHeight(table.getRowHeight());
                setCellRenderer(new CellRenderer(this));
                setBorder(new RowHeaderBorder());
                setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
                syncRowCount(); // Initialize to initial size of table.
            TableModelListener listener = EventHandler.create(TableModelListener.class, this, "tableChanged");
            table.getModel().addTableModelListener(listener);
                setOpaque(false);
        }

        @Override
        public void updateUI() {
                super.updateUI();
                if(this.table != null) {
                    setFixedCellHeight(table.getRowHeight());
                    computeCellWidth();
                }
        }
        
        private void computeCellWidth() {
                int rowCount = table.getRowCount();
                //The following lines are very important
                //If you define a fixed cell width then
                //the jlist does'nt have to compute the maximum width of
                // the rowCount labels ! (this can take a very long time)
                JLabel label = (JLabel)getCellRenderer().getListCellRendererComponent(this, rowCount , rowCount - 1, false, false);
                setFixedCellWidth(label.getPreferredSize().width);                
        }
      
        /**
         * Table update
         */
        public void tableChanged() {
                syncRowCount();
        }

        private void syncRowCount() {
                if (table.getRowCount() != model.getSize()) {
                        // Always keep 1 row, even if showing 0 bytes in editor                        
                        model.setSize(Math.max(1, table.getRowCount() ));
                        computeCellWidth();
                }
        }

        /**
         * List model used by the header for the attributes tables.
         */
        private static class RowHeaderListModel extends AbstractListModel<Integer> {

                private static final long serialVersionUID = 1L;
                private int size;

                @Override
                public Integer getElementAt(int index) {
                        return index + 1;
                }

                @Override
                public int getSize() {
                        return size;
                }

                public void setSize(int size) {
                        int old = this.size;
                        this.size = size;
                        int diff = size - old;
                        if (diff > 0) {
                                fireIntervalAdded(this, old, size - 1);
                        } else if (diff < 0) {
                                fireIntervalRemoved(this, size + 1, old - 1);
                        }
                }
        }

        private class CellRenderer extends ListLaFRenderer<Integer> {

                public CellRenderer(JList<Integer> list) {
                        super(list);
                }

                @Override
                public Component getListCellRendererComponent(JList<? extends Integer> list, Integer value,
                        int index, boolean selected, boolean hasFocus) {
                        // Never paint cells as "selected."
                        
                        Component comp = lookAndFeelRenderer.getListCellRendererComponent(list, value, index,
                                false, hasFocus);
                        if(comp instanceof JLabel) {
                                JLabel compLabel = (JLabel)comp;
                                compLabel.setHorizontalAlignment(JLabel.RIGHT);
                                compLabel.setBackground((new JLabel()).getBackground());
                        }
                        return comp;
                }
        }

        /**
         * Border for the entire row header.  This draws a line to separate the
         * header from the table contents, and gives a small amount of whitespace
         * to separate the two.
         *
         * @author Robert Futrell
         * @version 1.0
         */
        private class RowHeaderBorder extends EmptyBorder {

                private static final long serialVersionUID = 1L;

                public RowHeaderBorder() {
                        super(0, 0, 0, 2);
                }

                @Override
                public void paintBorder(Component c, Graphics g, int x, int y,
                        int width, int height) {
                        x = x + width - this.right;
                        //             g.setColor(table.getBackground());
                        //           g.fillRect(x, y, width, height);
                        g.setColor(table.getGridColor());
                        g.drawLine(x, y, x, y + height);
                }
        }
}
