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
package org.orbisgis.view.table;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.HashMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import org.apache.log4j.Logger;
import org.gdms.data.types.Type;
import org.orbisgis.core.Services;
import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.view.background.BackgroundJob;
import org.orbisgis.view.background.BackgroundManager;
import org.orbisgis.view.docking.DockingPanelParameters;
import org.orbisgis.view.edition.EditableElement;
import org.orbisgis.view.edition.EditableElementException;
import org.orbisgis.view.edition.EditorDockable;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Edit a data source through a grid GUI.
 * @author Nicolas Fortin
 */
public class TableEditor extends JPanel implements EditorDockable {
        protected final static I18n I18N = I18nFactory.getI18n(TableEditor.class);
        private static final Logger LOGGER = Logger.getLogger(TableEditor.class);
        
        private static final long serialVersionUID = 1L;
        private TableEditableElement tableEditableElement;
        private DockingPanelParameters dockingPanelParameters;
        private JTable table;
        private JScrollPane tableScrollPane;
        private DataSourceTableModel tableModel;

        public TableEditor(TableEditableElement element) {
                super(new BorderLayout());
                this.tableEditableElement = element;
                dockingPanelParameters = new DockingPanelParameters();
                dockingPanelParameters.setTitle(I18N.tr("Table Editor of {0}",element.getSourceName()));
                dockingPanelParameters.setTitleIcon(OrbisGISIcon.getIcon("openattributes"));
                tableScrollPane = new JScrollPane(makeTable());
                add(tableScrollPane,BorderLayout.CENTER);
                BackgroundManager bm = Services.getService(BackgroundManager.class);
                bm.nonBlockingBackgroundOperation(new OpenEditableElement());
        }
        
        private JComponent makeTable() {
                table = new JTable();
                table.getSelectionModel().setSelectionMode(
                        ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                table.getTableHeader().setReorderingAllowed(false);
                table.setColumnSelectionAllowed(true);
                //table.setPreferredScrollableViewportSize(new Dimension(500, 70));
                table.setFillsViewportHeight(true);                
                return table;
        }

        /**
         * Return the editable document
         * @return 
         */
        public TableEditableElement getTableEditableElement() {
                return tableEditableElement;
        }

        @Override
        public boolean match(EditableElement editableElement) {
                if(editableElement instanceof TableEditableElement) {
                        TableEditableElement tableElement = (TableEditableElement) editableElement;
                        return tableElement.getDataSource().equals(tableEditableElement.getDataSource());
                }
                return false;
        }


        private int getColumnOptimalWidth(int rowsToCheck, int maxWidth,
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
        
        /**
         * When the editable element is open, 
         * the data model of the table can be set
         */
        private void readDataSource() {                        
                tableModel = new DataSourceTableModel(tableEditableElement);
                table.setModel(tableModel);
                table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);                
                autoResizeColWidth(Math.min(5, tableModel.getRowCount()),
                new HashMap<String, Integer>(),
                new HashMap<String, TableCellRenderer>());
                //Set the row count at left
                tableScrollPane.setRowHeaderView(new TableRowHeader(table));
        }
        
        private void autoResizeColWidth(int rowsToCheck,
                HashMap<String, Integer> widths,
                HashMap<String, TableCellRenderer> renderers) {
                DefaultTableColumnModel colModel = new DefaultTableColumnModel();
                int maxWidth = 200;
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                        TableColumn col = new TableColumn(i);
                        String columnName = tableModel.getColumnName(i);
                        int columnType = tableModel.getColumnType(i).getTypeCode();

                        col.setHeaderValue(columnName);
                        TableCellRenderer tableCellRenderer = renderers.get(columnName);

                        /*
                        if (tableCellRenderer == null) {
                                tableCellRenderer = new DefaultTableCellRenderer();
                        }
                        col.setHeaderRenderer(tableCellRenderer);
                        */
                        Integer width = widths.get(columnName);
                        if (width == null) {
                                width = getColumnOptimalWidth(rowsToCheck, maxWidth, i,
                                        new NullProgressMonitor());
                        }
                        col.setPreferredWidth(width);
                        colModel.addColumn(col);
                        switch (columnType) {
                                case Type.DOUBLE:
                                case Type.INT:
                                case Type.LONG:
                                        /*
                                        NumberFormat formatter = NumberFormat.getCurrencyInstance();
                                        FormatRenderer formatRenderer = new FormatRenderer(formatter);
                                        formatRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
                                        formatRenderer.setBackground(NUMERIC_COLOR);
                                        col.setCellRenderer(formatRenderer);
                                        * 
                                        */
                                        break;
                                default:
                                        break;
                        }

                }
                table.setColumnModel(colModel);
        }        
        
        @Override
        public EditableElement getEditableElement() {
                return tableEditableElement;
        }

        @Override
        public void setEditableElement(EditableElement editableElement) {
                
        }

        @Override
        public DockingPanelParameters getDockingParameters() {
                return dockingPanelParameters;
        }

        @Override
        public JComponent getComponent() {
                return this;
        }
        private class OpenEditableElement implements BackgroundJob {

                @Override
                public void run(ProgressMonitor pm) {
                        try {
                                tableEditableElement.open(pm);
                        } catch (UnsupportedOperationException ex) {
                                LOGGER.error(I18N.tr("Error while loading the table editor"),ex);
                        } catch (EditableElementException ex) {
                                LOGGER.error(I18N.tr("Error while loading the table editor"),ex);
                        }
                        readDataSource();
                        
                }

                @Override
                public String getTaskName() {
                        return I18N.tr("Open the data source {0}",tableEditableElement.getSourceName());
                }
                
        }
}
