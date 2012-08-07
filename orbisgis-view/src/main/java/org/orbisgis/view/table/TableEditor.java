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
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.EventHandler;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.UIManager;
import javax.swing.event.RowSorterListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import org.apache.log4j.Logger;
import org.gdms.data.schema.MetadataUtilities;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.core.common.IntegerUnion;
import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.view.background.BackgroundJob;
import org.orbisgis.view.background.BackgroundManager;
import org.orbisgis.view.components.filter.FilterFactoryManager;
import org.orbisgis.view.docking.DockingPanelParameters;
import org.orbisgis.view.edition.EditableElement;
import org.orbisgis.view.edition.EditableElementException;
import org.orbisgis.view.edition.EditorDockable;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.table.filters.FieldsContainsFilterFactory;
import org.orbisgis.view.table.filters.TableSelectionFilter;
import org.orbisgis.view.table.filters.WhereSQLFilterFactory;
import org.orbisgis.view.table.jobs.OptimalWidthJob;
import org.orbisgis.view.table.jobs.SearchJob;
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
        private DataSourceRowSorter tableSorter;
        private DataSourceTableModel tableModel;
        private AtomicBoolean initialised = new AtomicBoolean(false);
        private FilterFactoryManager<TableSelectionFilter> filterManager = new FilterFactoryManager<TableSelectionFilter>();
        private TableRowHeader tableRowHeader;
        private Point popupCellAdress = new Point(); //Col(x) and row(y) that trigger a popup

        public TableEditor(TableEditableElement element) {
                super(new BorderLayout());
                LOGGER.debug("Create the GRID");
                this.tableEditableElement = element;
                dockingPanelParameters = new DockingPanelParameters();
                updateTitle(false);
                dockingPanelParameters.setTitleIcon(OrbisGISIcon.getIcon("openattributes"));
                tableScrollPane = new JScrollPane(makeTable());
                add(tableScrollPane,BorderLayout.CENTER);
        }
        
        private JComponent makeFilterManager() {
                JPanel filterComp = filterManager.makeFilterPanel(false);
                filterManager.setUserCanRemoveFilter(false);
                FieldsContainsFilterFactory factory = new FieldsContainsFilterFactory(table);
                filterManager.registerFilterFactory(factory);
                filterManager.registerFilterFactory(new WhereSQLFilterFactory());
                filterManager.addFilter(factory.getDefaultFilterValue());
                filterManager.getEventFilterChange().addListener(this, EventHandler.create(FilterFactoryManager.FilterChangeListener.class, this, "onApplySelectionFilter"));
                return filterComp;
        }
        
        public void onApplySelectionFilter() {
                List<TableSelectionFilter> filters = filterManager.getFilters();
                launchJob(new SearchJob(filters.get(0), table, tableModel.getDataSource()));                                
        }
        
        private JComponent makeTable() {
                table = new JTable();
                table.addMouseListener(EventHandler.create(MouseListener.class,
                        this,
                        "onMouseActionOnTableCells",
                        ""));
                
                table.getTableHeader().addMouseListener(EventHandler.create(MouseListener.class,
                        this,
                        "onMouseActionOnTableHeader",
                        ""));
                        
                        
                table.getSelectionModel().setSelectionMode(
                        ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                table.getTableHeader().setReorderingAllowed(false);
                table.setColumnSelectionAllowed(true);
                //table.setPreferredScrollableViewportSize(new Dimension(500, 70));
                table.setFillsViewportHeight(true);       
                table.setUpdateSelectionOnSort(true);
                return table;
        }
        
        public void onMouseActionOnTableHeader(MouseEvent e) {
                //Does this action correspond to a popup request
                if (e.isPopupTrigger()) { 
                        int col = table.columnAtPoint(e.getPoint());
                        popupCellAdress.setLocation(col,-1);
                        JPopupMenu menu = makeTableHeaderPopup(col);
                        menu.show(e.getComponent(), e.getX(), e.getY());
                }
        }

        public void onMouseActionOnTableCells(MouseEvent e) {
                //Does this action correspond to a popup request
                if (e.isPopupTrigger()) { 
                        int row = table.rowAtPoint(e.getPoint());
                        int col = table.columnAtPoint(e.getPoint());
                        popupCellAdress.setLocation(col,row);
                        JPopupMenu menu = makeTableCellPopup(row,col);
                        menu.show(e.getComponent(), e.getX(), e.getY());
                }
        }
        
        private JPopupMenu makeTableCellPopup(int row,int col) {
                JPopupMenu pop = new JPopupMenu();                
                boolean hasGeometry=false;
                try {
                        hasGeometry = MetadataUtilities.isGeometry(tableEditableElement.getDataSource().getMetadata());
                }catch (DriverException ex) {
                        LOGGER.error(I18N.tr("Menu creation error"), ex);
                }
                if(table.getSelectedRowCount()>0) {
                        JMenuItem addRowFilter = new JMenuItem(I18N.tr("Filter selected rows"));
                        addRowFilter.setToolTipText(I18N.tr("Show only the selected rows"));
                        addRowFilter.addActionListener(
                                EventHandler.create(ActionListener.class,
                                this,"onMenuFilterRows"));
                        pop.add(addRowFilter);
                }
                if(tableSorter.isFiltered()) {
                        JMenuItem removeRowFilter = new JMenuItem(
                                I18N.tr("Clear row filter"));
                        removeRowFilter.setToolTipText(I18N.tr("Show all rows"));
                        removeRowFilter.addActionListener(
                                EventHandler.create(ActionListener.class,
                                this,"onMenuClearFilter"));
                        pop.add(removeRowFilter);
                }
                return pop;
        }
        
        /**
         * Show all rows of the data source (remove the filter)
         */
        public void onMenuClearFilter() {
                tableSorter.setRowsFilter(null);
        }
        /**
         * Show only selected rows
         */
        public void onMenuFilterRows() {
                IntegerUnion selectedModelIndex = new IntegerUnion();
                for(int viewRowId : table.getSelectedRows()) {
                        selectedModelIndex.add(tableSorter.convertRowIndexToModel(viewRowId));
                }
                tableSorter.setRowsFilter(selectedModelIndex);
        }
        
        /**
         * Create the popup menu of the table header
         * @param col
         * @return 
         */
        private JPopupMenu makeTableHeaderPopup(Integer col) {
                JPopupMenu pop = new JPopupMenu();
                //Optimal width
                JMenuItem optimalWidth = 
                        new JMenuItem(I18N.tr("Optimal width"),
                        OrbisGISIcon.getIcon("text_letterspacing")
                        );
                optimalWidth.addActionListener(
                        EventHandler.create(ActionListener.class,this,
                        "onMenuOptimalWidth"));
                pop.add(optimalWidth);
                // Additionnal functions for specific columns
                int geoIndex = -1;
                try {
                        geoIndex = MetadataUtilities.getGeometryFieldIndex(tableModel.getDataSource().getMetadata());
                } catch (DriverException ex) {
                        LOGGER.error(ex.getLocalizedMessage(),ex);
                }                
                if (geoIndex!=col) {
                        pop.addSeparator();
                        //Sort Ascending
                        JMenuItem sortAscending =
                                new JMenuItem(I18N.tr("Sort ascending"),
                                UIManager.getIcon("Table.ascendingSortIcon")
                                );
                        sortAscending.addActionListener(
                        EventHandler.create(ActionListener.class,this,
                        "onMenuSortAscending"));
                        pop.add(sortAscending);
                        //Sort Descending
                        JMenuItem sortDescending =
                                new JMenuItem(I18N.tr("Sort descending"),
                                UIManager.getIcon("Table.descendingSortIcon")
                                );
                        sortDescending.addActionListener(
                        EventHandler.create(ActionListener.class,this,
                        "onMenuSortDescending"));
                        pop.add(sortDescending);
                        //No sort
                        JMenuItem noSort =
                                new JMenuItem(I18N.tr("No sort"),
                                OrbisGISIcon.getIcon("table_refresh")
                                );
                        noSort.addActionListener(
                        EventHandler.create(ActionListener.class,this,
                        "onMenuNoSort"));
                        pop.add(noSort);
                }
                return pop;

        }
        
        private void launchJob(BackgroundJob job) {
                Services.getService(BackgroundManager.class).nonBlockingBackgroundOperation(job);
        }
        
        /**
         * The user disable table sort
         */
        public void onMenuNoSort() {
                tableSorter.setSortKeys(null);
        }
        /**
         * Ascending sort
         */
        public void onMenuSortAscending() {
                tableSorter.setSortKey(new SortKey(popupCellAdress.x,SortOrder.ASCENDING));
        }
        /**
         * Descending sort
         */
        public void onMenuSortDescending() {
                tableSorter.setSortKey(new SortKey(popupCellAdress.x,SortOrder.DESCENDING));                
        }
        /**
         * Compute the optimal width for this column
         */
        public void onMenuOptimalWidth() {
                launchJob(new OptimalWidthJob(table,popupCellAdress.x));
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

        @Override
        public void addNotify() {
                super.addNotify();
                if(!initialised.getAndSet(true)) {
                        BackgroundManager bm = Services.getService(BackgroundManager.class);
                        bm.backgroundOperation(new OpenEditableElement());                        
                }
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
                tableSorter = new DataSourceRowSorter(tableModel);
                tableSorter.addRowSorterListener(
                        EventHandler.create(RowSorterListener.class,this,
                        "onShownRowsChanged"));
                table.setRowSorter(tableSorter);
                //Set the row count at left
                tableRowHeader = new TableRowHeader(table);
                tableScrollPane.setRowHeaderView(tableRowHeader);
                add(makeFilterManager(),BorderLayout.SOUTH);
        }
        
        /**
         * The model or the sorted have updated the table
         */
        public void onShownRowsChanged() {
                updateTitle(tableSorter.isFiltered());
                tableRowHeader.tableChanged();
        }
        /**
         * 
         * @param filtered If the shown rows do not reflect the model
         */
        private void updateTitle(boolean filtered) {
                if(!filtered) {
                        dockingPanelParameters.setTitle(I18N.tr("Table Editor of {0}",tableEditableElement.getSourceName()));
                }else{
                        dockingPanelParameters.setTitle(I18N.tr("Table Editor of {0} (Filtered)",tableEditableElement.getSourceName()));
                }                
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
                        
                        if (tableCellRenderer != null) {
                                col.setHeaderRenderer(tableCellRenderer);
                        }
                        Integer width = widths.get(columnName);
                        if (width == null) {
                                width = OptimalWidthJob.getColumnOptimalWidth(table,rowsToCheck, maxWidth, i,
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
                                LOGGER.error(I18N.tr("Error while loading the table editor"), ex);
                        } catch (EditableElementException ex) {
                                LOGGER.error(I18N.tr("Error while loading the table editor"), ex);
                        }
                        readDataSource();
                }

                @Override
                public String getTaskName() {
                        return I18N.tr("Open the data source {0}", tableEditableElement.getSourceName());
                }
        }
}
