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

import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import org.apache.log4j.Logger;
import org.gdms.data.schema.MetadataUtilities;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.core.common.IntegerUnion;
import org.orbisgis.view.background.BackgroundJob;
import org.orbisgis.view.background.BackgroundManager;
import org.orbisgis.view.table.jobs.SortJob;
import org.orbisgis.view.table.jobs.SortJobEventSorted;

/**
 *
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
        private List<SortKey> sortedColumns = new ArrayList<SortKey>();
        
        public DataSourceRowSorter(DataSourceTableModel model) {
                this.model = model;
        }
        
        @Override
        public DataSourceTableModel getModel() {
                return model;
        }
        
        /**
         * Called by the Sort job listener
         * Update the internal indexes and inform the table.
         * @param sortData 
         */
        public void onRowSortDone(SortJobEventSorted sortData) {
                int[] oldViewToModel = getViewToModelArray();
                viewToModel = new ArrayList<Integer>(sortData.getViewToModelIndex());
                initModelToView();
                sortedColumns.clear();
                sortedColumns.add(sortData.getSortRequest());
                fireSortOrderChanged();
                fireRowSorterChanged(oldViewToModel);
        }
        /**
         * Create the model to view from viewToModel
         */
        private void initModelToView() {
                modelToView = new HashMap<Integer,Integer>();
                for(int viewIndex = 0;viewIndex < viewToModel.size();viewIndex++) {
                        Integer modelIndex = viewToModel.get(viewIndex);
                        modelToView.put(modelIndex, viewIndex);
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
                LOGGER.debug("toggleSortOrder"+column);
                if(isSortable(column)) {
                        SortKey sortRequest=new SortKey(column, SortOrder.ASCENDING);
                        //Find if the user already set an order
                        for(int i=0;i<sortedColumns.size();i++) {
                                SortKey col = sortedColumns.get(i);
                                if(col.getColumn()==column) {
                                        SortOrder order;
                                        if(col.getSortOrder().equals(SortOrder.ASCENDING)) {
                                                order = SortOrder.DESCENDING;
                                        } else {
                                                order = SortOrder.ASCENDING;
                                        }
                                        sortRequest = new SortKey(column, order);
                                        break;
                                }
                        }
                        //Multiple order is not available
                        //To enable it, a new TableHeaderRenderer need to be defined
                        //UIManager.getIcon("Table.ascendingSortIcon");
                        //UIManager.getIcon("Table.descendingSortIcon");
                        //http://www.jroller.com/nweber/entry/multi_column_sorting_w_mustang
                        launchSortProcess(sortRequest);
                }
        }
        
        private void launchSortProcess(SortKey sortInformation) {
                SortJob sortJob = new SortJob(sortInformation, model, viewToModel);
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
         * @param sortRequest
         */
        public void setSortKey(SortKey sortRequest) {
                //Check if the sort request is not on the geometry column
                int geoIndex = -1;
                try {
                        geoIndex = MetadataUtilities.getGeometryFieldIndex(this.model.getDataSource().getMetadata());
                } catch (DriverException ex) {
                        LOGGER.error(ex.getLocalizedMessage(),ex);
                }     
                if(sortRequest.getColumn()==geoIndex) {
                        //Ignore sort request
                        return;
                }
                if (sortRequest != null) {
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
                        viewToModel = new ArrayList<Integer>(rowsFilter);
                        initModelToView();
                } else {
                        viewToModel = null;
                        modelToView = null;
                }                
                fireSortOrderChanged();
                fireRowSorterChanged(oldViewToModel);
                //Do sorting
                refreshSorter();
        }

        /**
         * 
         * @return True if the shown rows are filtered
         */
        public boolean isFiltered() {
                return getModelRowCount()!=getViewRowCount();
        }


        private boolean isSortable(int columnIndex) {
                return model.getColumnType(columnIndex).getTypeCode() != Type.GEOMETRY;
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
                LOGGER.debug("modelStructureChanged");
                refreshSorter();
        }

        @Override
        public void allRowsChanged() {
                LOGGER.debug("allRowsChanged");
                refreshSorter();
        }

        @Override
        public void rowsInserted(int i, int i1) {
                LOGGER.debug("rowsInserted");
                clearIndex();
                refreshSorter();
        }

        @Override
        public void rowsDeleted(int i, int i1) {
                LOGGER.debug("rowsDeleted");
                clearIndex();
                refreshSorter();
        }

        @Override
        public void rowsUpdated(int i, int i1) {
                LOGGER.debug("rowsUpdated");
                refreshSorter();
        }

        @Override
        public void rowsUpdated(int i, int i1, int i2) {
                LOGGER.debug("rowsUpdated");
                refreshSorter();
        }
        
}
