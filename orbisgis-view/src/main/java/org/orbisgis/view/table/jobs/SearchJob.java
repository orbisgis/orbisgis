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

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.orbisgis.core.common.IntegerUnion;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.view.background.BackgroundJob;
import org.orbisgis.viewapi.table.TableEditableElement;
import org.orbisgis.view.table.filters.TableSelectionFilter;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 *
 * @author Nicolas Fortin
 */
public class SearchJob implements BackgroundJob {
        protected final static I18n I18N = I18nFactory.getI18n(SearchJob.class);
        private TableSelectionFilter activeFilter;
        private JTable table;
        private TableEditableElement source;
        private AtomicBoolean filterRunning;
        private static final Logger LOGGER = Logger.getLogger("gui."+SearchJob.class);
        
        public SearchJob(TableSelectionFilter activeFilter, JTable table, TableEditableElement source, AtomicBoolean filterRunning) {
                this.activeFilter = activeFilter;
                this.table = table;
                this.source = source;
                this.filterRunning = filterRunning;
        }
        private void runFilter(ProgressMonitor progress) {
                final ProgressMonitor pm = progress.startTask(getTaskName(), 3);
                //Launch filter initialisation
                try {
                    activeFilter.initialize(pm, source);
                } catch (SQLException ex) {
                    LOGGER.error(ex.getLocalizedMessage(), ex);
                    return;
                }
                pm.progressTo(1);  // If filter does not handle progress monitor
                //Iterate on rows
                final IntegerUnion nextViewSelection = new IntegerUnion();
                int rowCount = table.getRowCount();
                ProgressMonitor viewUpdate = pm.startTask(I18N.tr("Read filter"), rowCount);
                for(int viewId=0;viewId<rowCount;viewId++) {
                        if(activeFilter.isSelected(table.getRowSorter().convertRowIndexToModel(viewId), source)) {
                                nextViewSelection.add(viewId);
                                viewUpdate.endTask();
                                if(pm.isCancelled()) {
                                        return;
                                }
                        }
                }
                SwingUtilities.invokeLater( new Runnable() {

                        @Override
                        public void run() {
                                // Update the table values
                                List<Integer> ranges = nextViewSelection.getValueRanges();
                                Iterator<Integer> intervals = ranges.iterator();
                                try {
                                        table.getSelectionModel().setValueIsAdjusting(true);
                                        table.clearSelection();
                                        ProgressMonitor swingPm = pm.startTask("Apply filter", ranges.size());
                                        while (intervals.hasNext()) {
                                                int begin = intervals.next();
                                                int end = intervals.next();
                                                table.addRowSelectionInterval(begin, end);
                                                swingPm.endTask();
                                                if (swingPm.isCancelled()) {
                                                        return;
                                                }
                                        }                                        
                                } finally {
                                        table.getSelectionModel().setValueIsAdjusting(false);
                                }
                        }
                });         
        }
        @Override
        public void run(ProgressMonitor pm) {
                try {
                        runFilter(pm);
                } finally {
                        filterRunning.set(false);
                }
        }

        @Override
        public String getTaskName() {
                return I18N.tr("Selecting rows");
        }
        
}
