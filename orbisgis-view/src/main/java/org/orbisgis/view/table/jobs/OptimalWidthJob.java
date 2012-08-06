/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. 
 * 
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 * 
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.table.jobs;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.view.background.BackgroundJob;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Computation of the optimal column width of the entire table
 */
public class OptimalWidthJob implements BackgroundJob{
        protected final static I18n I18N = I18nFactory.getI18n(OptimalWidthJob.class);
        private JTable table;
        private int selectedColumn;

        public OptimalWidthJob(JTable table, int selectedColumn) {
                this.table = table;
                this.selectedColumn = selectedColumn;
        }
        
        @Override
        public void run(ProgressMonitor pm) {
                final int width = getColumnOptimalWidth(table,table.getRowCount(), Integer.MAX_VALUE,
                        selectedColumn, pm);
                final TableColumn col = table.getColumnModel().getColumn(selectedColumn);
                SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                                col.setPreferredWidth(width);
                        }
                });
        }

        /**
         * Compute the optimal width of a table column
         * @param table 
         * @param rowsToCheck Number of rows used to evaluate the optimal width
         * @param maxWidth Limitation of the width
         * @param column Column index
         * @param pm Progress information
         * @return 
         */
        public static int getColumnOptimalWidth(JTable table, int rowsToCheck, int maxWidth,
                int column, ProgressMonitor pm) {
                TableColumn col = table.getColumnModel().getColumn(column);
                int margin = 5;
                int headerMargin = 10;

                // Get width of column header
                TableCellRenderer renderer = col.getHeaderRenderer();

                if (renderer == null) {
                        renderer = table.getTableHeader().getDefaultRenderer();
                }

                Component comp = renderer.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, 0);

                int width = comp.getPreferredSize().width;

                // Check header
                comp = renderer.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, column);
                width = Math.max(width, comp.getPreferredSize().width + 2
                        * headerMargin);
                // Get maximum width of column data
                for (int r = 0; r < rowsToCheck; r++) {
                        if (r / 100 == r / 100.0) {
                                if (pm.isCancelled()) {
                                        break;
                                } else {
                                        pm.progressTo(100 * r / rowsToCheck);
                                }
                        }
                        renderer = table.getCellRenderer(r, column);
                        comp = renderer.getTableCellRendererComponent(table, table.getValueAt(r, column), false, false, r, column);
                        width = Math.max(width, comp.getPreferredSize().width);
                }

                // limit
                width = Math.min(width, maxWidth);

                // Add margin
                width += 2 * margin;

                return width;
        }

        @Override
        public String getTaskName() {
                return I18N.tr("Computation of the optimal column width");
        }

}
