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
 * 
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.view.table;

import java.beans.EventHandler;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import javax.sql.DataSource;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import org.apache.log4j.Logger;
import org.orbisgis.core.Services;
import org.orbisgis.corejdbc.common.IntegerUnion;
import org.orbisgis.view.background.BackgroundJob;
import org.orbisgis.view.background.BackgroundManager;
import org.orbisgis.view.table.jobs.SortJob;
import org.orbisgis.view.table.jobs.SortJobEventSorted;

/**
 * This class extends the swing RowSorter to launch SortJob
 * and to filter the shown rows. Currently the columns cannot be filtered.
 * @author Nicolas Fortin
 */
public class DataSourceRowSorter extends RowSorter<DataSourceTableModel> {
        private static final Logger LOGGER = Logger.getLogger(DataSourceRowSorter.class);
        private DataSourceTableModel model;        //If the Model rows do not reflect the DataSource row number
        //this array give the link between the TableModel Row Id
        //and the DataSource row ID
        private List<Integer> viewToModel = null;
        //The model can be filtered, then a model row can not be in the view
        private Map<Integer,Integer> modelToView = null;
        //Sorted columns
        private List<SortKey> sortedColumns = new ArrayList<>();
        private DataSource dataSource;
        // Sort result given through JDBC
        private Collection<Integer> viewToModelJDBC;
        
        public DataSourceRowSorter(DataSourceTableModel model, DataSource dataSource) {
                this.model = model;
                this.dataSource = dataSource;
        }
        
        @Override
        public DataSourceTableModel getModel() {
                return model;
        }

        private void applyJDBCSort(int[] oldViewToModel) {
            Set<Integer> filter = null;
            if(viewToModel != null) {
                filter = new HashSet<>(viewToModel);
            }
            // Sorted is done using JDBC Index
            // And it is not filtered
            if(viewToModelJDBC != null) {
                viewToModel = new ArrayList<>(viewToModelJDBC.size());
                for (int i : viewToModelJDBC) {
                    if (filter == null) {
                        viewToModel.add(i - 1);
                    } else if (filter.contains(i - 1)) {
                        viewToModel.add(i - 1);
                    }
                }
            }
            initModelToView();
            fireSortOrderChanged();
            fireRowSorterChanged(oldViewToModel);
        }
        
        /**
         * Called by the Sort job listener
         * Update the internal indexes and inform the table.
         * @param sortData Sort result
         */
        public void onRowSortDone(SortJobEventSorted sortData) {
                int[] oldViewToModel = getViewToModelArray();
                viewToModelJDBC = sortData.getViewToModelIndex();
                sortedColumns.clear();
                sortedColumns.add(sortData.getSortRequest());
                applyJDBCSort(oldViewToModel);
        }
        /**
         * Create the model to view from viewToModel
         */
        private void initModelToView() {
                modelToView = new HashMap<>();
                if(viewToModel != null) {
                    for (int viewIndex = 0; viewIndex < viewToModel.size(); viewIndex++) {
                        Integer modelIndex = viewToModel.get(viewIndex);
                        modelToView.put(modelIndex, viewIndex);
                    }
                }
        }
        private int[] getViewToModelArray() {
                int[] viewToModelArray = null;
                if(viewToModel!=null) {
                        viewToModelArray = new int[viewToModel.size()];
                        for(int i=0;i<viewToModelArray.length;i++) {
                                viewToModelArray[i]=viewToModel.get(i);
                        }
                }
                return viewToModelArray;
        }

        @Override
        public void toggleSortOrder(int column) {
                if(isSortable(column)) {
                        SortKey sortRequest=new SortKey(column, SortOrder.ASCENDING);
                        boolean doReverse = true;
                        //Find if the user already set an order
                        for (SortKey col : sortedColumns) {
                            if (col.getColumn() == column) {
                                SortOrder order;
                                if (col.getSortOrder().equals(SortOrder.ASCENDING)) {
                                    order = SortOrder.DESCENDING;
                                } else {
                                    order = SortOrder.ASCENDING;
                                }
                                sortRequest = new SortKey(column, order);
                                doReverse = false;
                                break;
                            }
                        }
                        //Multiple order is not available
                        //To enable it, a new TableHeaderRenderer need to be defined
                        //UIManager.getIcon("Table.ascendingSortIcon");
                        //UIManager.getIcon("Table.descendingSortIcon");
                        //http://www.jroller.com/nweber/entry/multi_column_sorting_w_mustang
                        if(doReverse || viewToModelJDBC == null) {
                            launchSortProcess(sortRequest);
                        } else {
                            // The user reverse the already sorted column
                            int[] oldViewToModel = getViewToModelArray();
                            ArrayList<Integer> reversed = new ArrayList<>(viewToModelJDBC);
                            Collections.reverse(reversed);
                            viewToModelJDBC = reversed;
                            sortedColumns.clear();
                            sortedColumns.add(sortRequest);
                            applyJDBCSort(oldViewToModel);
                        }
                }
        }
        
        private void launchSortProcess(SortKey sortInformation) {
                SortJob sortJob = new SortJob(sortInformation, model, viewToModel, dataSource);
                sortJob.getEventSortedListeners().addListener(this, EventHandler.create(SortJob.SortJobListener.class,this,"onRowSortDone",""));
                launchJob(sortJob);
        }

        @Override
        public int convertRowIndexToModel(int index) {
                if(viewToModel==null) {
                        return index;
                } else {
                        return viewToModel.get(index);
                }
        }

        private void launchJob(BackgroundJob job) {
                Services.getService(BackgroundManager.class).nonBlockingBackgroundOperation(job);
        }
        
        @Override
        public int convertRowIndexToView(int index) {
                if(modelToView==null) {
                        return index;
                }
                Integer viewIndex = modelToView.get(index);
                if(viewIndex==null) {
                        return -1;
                } else {
                        return viewIndex;
                }
        }
        
        @Override
        public void setSortKeys(List<? extends SortKey> list) {
                if (list == null || list.isEmpty()) {
                        setSortKey(null);
                } else {
                        setSortKey(list.get(0));
                }
        }
        /**
         * Sort the column in the provided order
         *
         * @param sortRequest The key to sort
         */
        public void setSortKey(SortKey sortRequest) {
                if (sortRequest != null) {     
                        //Check if the sort request is not on the geometry column
                        int sortIndex = sortRequest.getColumn();
                        if(!isSortable(sortIndex)) {
                            //Ignore sort request
                            return;
                        }
                        launchSortProcess(sortRequest);
                } else {
                        sortedColumns.clear();
                        if(isFiltered()) {
                                IntegerUnion shownRows = new IntegerUnion(viewToModel);
                                clearIndex();
                                setRowsFilter(shownRows);
                        } else {
                                clearIndex();
                        }
                }
        }

        private void clearIndex() {
                if(viewToModel!=null) {
                        int[] oldViewToModel = getViewToModelArray();
                        viewToModel = null;
                        modelToView = null;
                        fireSortOrderChanged();
                        fireRowSorterChanged(oldViewToModel);
                }
        }

        /**
         * Show only the provided model row id
         *
         * @param rowsFilter Rows to show, must be already visible in the view
         * row list (sort is not launch)
         */
        public void setRowsFilter(IntegerUnion rowsFilter) {
                int[] oldViewToModel = getViewToModelArray();
                if(rowsFilter!=null) {
                        //Update the internal list
                        viewToModel = new ArrayList<>(rowsFilter);
                        initModelToView();
                } else {
                        viewToModel = null;
                        modelToView = null;
                }
                // Apply sort on filtered result
                applyJDBCSort(oldViewToModel);
        }

        /**
         * 
         * @return True if the shown rows are filtered
         */
        public boolean isFiltered() {
                return getModelRowCount()!=getViewRowCount();
        }


        private boolean isSortable(int columnIndex) {
            try {
                ResultSetMetaData meta = model.getRowSet().getMetaData();
                return !meta.getColumnTypeName(columnIndex + 1).equalsIgnoreCase("geometry");
            } catch (SQLException ex) {
                LOGGER.error(ex.getLocalizedMessage(), ex);
                return false;
            }
        }

        @Override
        public List<? extends SortKey> getSortKeys() {
                return sortedColumns;
        }
        
        @Override
        public int getViewRowCount() {
                if(viewToModel==null) {
                        return getModelRowCount();
                }
                return viewToModel.size();
        }

        @Override
        public int getModelRowCount() {
                return model.getRowCount();
        }
        
        /**
         * Launch sort processing and remove the row filter
         */
        private void refreshSorter() {
                if(sortedColumns!=null && !sortedColumns.isEmpty()) {
                        launchSortProcess(sortedColumns.get(0));
                }
        }

        @Override
        public void modelStructureChanged() {
                refreshSorter();
        }

        @Override
        public void allRowsChanged() {
                refreshSorter();
        }

        @Override
        public void rowsInserted(int i, int i1) {
                clearIndex();
                refreshSorter();
        }

        @Override
        public void rowsDeleted(int i, int i1) {
                clearIndex();
                refreshSorter();
        }

        @Override
        public void rowsUpdated(int i, int i1) {
                refreshSorter();
        }

        @Override
        public void rowsUpdated(int i, int i1, int i2) {
                refreshSorter();
        }
        /**
         * The list of index correspondance between the seens rows and the model rows
         * @return The list or null if there is no sort or filter
         */
        public List<Integer> getViewToModelIndex() {
                return Collections.unmodifiableList(viewToModel);
        }
}
