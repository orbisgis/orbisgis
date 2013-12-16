/*
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
package org.orbisgis.view.table.jobs;

import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeSet;
import javax.sql.DataSource;
import javax.sql.RowSet;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.h2gis.utilities.JDBCUtilities;
import org.orbisgis.core.common.IntegerUnion;
import org.orbisgis.core.events.EventException;
import org.orbisgis.core.events.Listener;
import org.orbisgis.core.events.ListenerContainer;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.view.background.BackgroundJob;
import org.orbisgis.view.table.DataSourceTableModel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Sort the specified column and update the Table model.
 * The Data source is used to not store all the column values in memory
 * @author Nicolas Fortin
 */
public class SortJob implements BackgroundJob {
        public interface SortJobListener extends Listener<SortJobEventSorted>{
                
        }
        protected final static I18n I18N = I18nFactory.getI18n(SortJob.class);
        private static final Logger LOGGER = Logger.getLogger(SortJob.class);
        private DataSourceTableModel model;
        private SortKey sortRequest;
        private Integer columnToSort;
        private String columnSortName;
        private ListenerContainer<SortJobEventSorted> eventSortedListeners = new ListenerContainer<SortJobEventSorted>();
        private Collection<Integer> modelIndex;
        private DataSource dataSource;

        /**
         * 
         * @param sortRequest 
         * @param tableModel 
         * @param modelIndex Current state of Index, can be null if the index is the same as the model
         */
        public SortJob(SortKey sortRequest, DataSourceTableModel tableModel, Collection<Integer> modelIndex, DataSource dataSource) {
                this.sortRequest = sortRequest;
                this.columnToSort = sortRequest.getColumn();
                this.modelIndex = modelIndex;
                this.dataSource = dataSource;
                model = tableModel;
                columnSortName = model.getColumnName(columnToSort);
        }

        public ListenerContainer<SortJobEventSorted> getEventSortedListeners() {
                return eventSortedListeners;
        }
        
        

        public static Collection<Integer> sortArray(Collection<Integer> modelIndex, Comparator<Integer> comparator, ProgressMonitor pm) throws IllegalStateException, DriverException {
                int rowCount = modelIndex.size();
                Collection<Integer> columnValues = new TreeSet<Integer>(comparator);
                int processedRows = 0;
                for (int i : modelIndex) {
                        columnValues.add(new Integer(i));
                        if (i / 100 == i / 100.0) {
                                if (pm.isCancelled()) {
                                        throw new IllegalStateException(I18N.tr("Aborted by user"));
                                } else {
                                        pm.progressTo(100 * processedRows / rowCount);
                                }
                        }
                        processedRows++;
                }
                return columnValues;
        }

        @Override
        public void run(ProgressMonitor pm) {
                if (model.getRowCount() < 2) {
                        return;
                }
                // Retrieve the index if the model have a restricted set of rows
                if (modelIndex == null) {
                        //Create an array [0 1 ..rows]
                        modelIndex = new IntegerUnion(0, model.getRowCount() - 1);
                }
                int rowCount = modelIndex.size();
                try(Connection connection = dataSource.getConnection();
                    Statement st = connection.createStatement()) {

                        PropertyChangeListener listener = EventHandler.create(PropertyChangeListener.class, st, "cancel");
                        pm.addPropertyChangeListener(ProgressMonitor.PROP_CANCEL,
                            listener);
                        int pkIndex = JDBCUtilities.getIntegerPrimaryKey(connection.getMetaData(), model.getTableName());
                        final Collection<Integer> columnValues;
                        if (pkIndex > 0) {
                                // Do not cache values
                                // Use SQL sort

                        } else {
                                //Cache values
                                ProgressMonitor cacheProgress = pm.startTask(I18N.tr("Cache table values"), rowCount);
                                Object[] cache = new Object[rowCount];
                                try(ResultSet rs = st.executeQuery(""))
                                while(rs.next()) {
                                    cache[i] = source.getFieldValue(i, columnToSort);
                                    cacheProgress.endTask();
                                }
                                comparator = new SortValueCachedComparator(cache);
                                if (sortRequest.getSortOrder().equals(SortOrder.DESCENDING)) {
                                    comparator = Collections.reverseOrder(comparator);
                                }
                                columnValues = sortArray(modelIndex, comparator, pm);
                        }
                        pm.removePropertyChangeListener(listener);
                        SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    //Update the table model on the swing thread
                                    eventSortedListeners.callListeners(new SortJobEventSorted(sortRequest, columnValues, this));
                                } catch (EventException ex) {
                                    //Ignore
                                }
                            }
                        });

                } catch (IllegalStateException | SQLException ex) {
                        LOGGER.error(I18N.tr("Driver error"), ex);
                }
        }

        @Override
        public String getTaskName() {
                return I18N.tr("Sorting {0}",columnSortName);
        }
}
