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
package org.orbisgis.tablegui.impl.jobs;

import org.orbisgis.commons.events.EventException;
import org.orbisgis.commons.events.Listener;
import org.orbisgis.commons.events.ListenerContainer;
import org.orbisgis.commons.progress.SwingWorkerPM;
import org.orbisgis.corejdbc.ReadTable;
import org.orbisgis.corejdbc.common.IntegerUnion;
import org.orbisgis.tablegui.impl.DataSourceTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.sql.DataSource;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Sort the specified column and update the Table model.
 * The Data source is used to not store all the column values in memory
 * @author Nicolas Fortin
 */
public class SortJob extends SwingWorkerPM<Collection<Integer>, Collection<Integer>> {
        private static final int MAX_SORT_TIME = 10; // In seconds
        public interface SortJobListener extends Listener<SortJobEventSorted>{
                
        }
        protected final static I18n I18N = I18nFactory.getI18n(SortJob.class);
        private static final Logger LOGGER = LoggerFactory.getLogger(SortJob.class);
        private DataSourceTableModel model;
        private SortKey sortRequest;
        private String columnSortName;
        private ListenerContainer<SortJobEventSorted> eventSortedListeners = new ListenerContainer<>();
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
            Integer columnToSort = sortRequest.getColumn();
                this.modelIndex = modelIndex;
                this.dataSource = dataSource;
                model = tableModel;
                columnSortName = model.getColumnName(columnToSort);
                setTaskName(I18N.tr("Sorting {0}",columnSortName));
        }

        public ListenerContainer<SortJobEventSorted> getEventSortedListeners() {
                return eventSortedListeners;
        }

        @Override
        protected Collection<Integer> doInBackground() throws SQLException {
            // Retrieve the index if the model have a restricted set of rows
            if (modelIndex == null) {
                //Create an array [0 1 ..rows]
                modelIndex = new IntegerUnion(0, model.getRowCount() - 1);
            }
            try(Connection connection = dataSource.getConnection()) {
                return ReadTable.getSortedColumnRowIndex(connection, model.getTableName(), columnSortName, sortRequest.getSortOrder() == SortOrder.ASCENDING, this.getProgressMonitor());
            }
        }

        @Override
        protected void done() {
                try {
                    eventSortedListeners.callListeners(new SortJobEventSorted(sortRequest, get(MAX_SORT_TIME, TimeUnit.SECONDS), this));
                } catch (InterruptedException|ExecutionException|TimeoutException|EventException ex) {
                    LOGGER.error(ex.getLocalizedMessage(), ex);
                }
        }
}
