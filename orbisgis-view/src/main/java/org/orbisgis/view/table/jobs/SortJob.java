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

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeSet;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
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

        /**
         * 
         * @param sortRequest 
         * @param tableModel 
         * @param modelIndex Current state of Index, can be null if the index is the same as the model
         */
        public SortJob(SortKey sortRequest, DataSourceTableModel tableModel, Collection<Integer> modelIndex) {
                this.sortRequest = sortRequest;
                this.columnToSort = sortRequest.getColumn();
                this.modelIndex = modelIndex;
                model = tableModel;
                try {
                        columnSortName = model.getDataSource().getMetadata().getFieldName(columnToSort);
                } catch (DriverException ex) {
                        LOGGER.error(I18N.tr("Driver error"), ex);
                }
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
                DataSource source = model.getDataSource();
                //Create a sorted collection, to follow the progression of order
                //The comparator will read the integer value and
                //use the data source to compare
                Comparator<Integer> comparator;
                try {
                        Type fieldType = source.getMetadata().getFieldType(columnToSort);
                        if (fieldType.getTypeCode() == Type.STRING) {
                                //Do not cache values
                                comparator = new SortValueComparator(source, columnToSort);
                        } else {
                                //Cache values
                                pm.startTask(I18N.tr("Cache table values"), 100);
                                Value[] cache = new Value[(int)source.getRowCount()];
                                for (int i = 0; i < source.getRowCount(); i++) {
                                        cache[i] = source.getFieldValue(i, columnToSort);
                                        if (i / 100 == i / 100.0) {
                                                if (pm.isCancelled()) {
                                                        return;
                                                } else {
                                                        pm.progressTo(100 * i / rowCount);
                                                }
                                        }
                                }
                                pm.endTask();
                                comparator = new SortValueCachedComparator(cache);
                        }
                        if (sortRequest.getSortOrder().equals(SortOrder.DESCENDING)) {
                                comparator = Collections.reverseOrder(comparator);
                        }
                        final Collection<Integer> columnValues = sortArray(modelIndex, comparator, pm);
                        SwingUtilities.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                        try {
                                                //Update the table model on the swing thread
                                                eventSortedListeners.callListeners(new SortJobEventSorted(sortRequest,columnValues , this));
                                        } catch (EventException ex) {
                                                //Ignore
                                        }
                                }
                        });

                } catch (IllegalStateException ex) {
                        LOGGER.error(I18N.tr("Driver error"), ex);
                } catch (DriverException ex) {
                        LOGGER.error(I18N.tr("Driver error"), ex);
                }
        }

        @Override
        public String getTaskName() {
                return I18N.tr("Sorting {0}",columnSortName);
        }
}
