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
package org.orbisgis.view.table.jobs;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.orbisgis.core.common.IntegerUnion;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.view.background.BackgroundJob;
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
        private DataSource source;
        private AtomicBoolean filterRunning;
        private static final Logger LOGGER = Logger.getLogger("gui."+SearchJob.class);
        
        public SearchJob(TableSelectionFilter activeFilter, JTable table, DataSource source, AtomicBoolean filterRunning) {
                this.activeFilter = activeFilter;
                this.table = table;
                this.source = source;
                this.filterRunning = filterRunning;
        }
        private void runFilter(final ProgressMonitor pm) { 
                //Launch filter initialisation
                activeFilter.initialize(pm,source);
                //Iterate on rows
                final IntegerUnion nextViewSelection = new IntegerUnion();
                int rowCount = table.getRowCount();
                pm.startTask(getTaskName(), 100);
                int lastprog = 0;
                for(int viewId=0;viewId<rowCount;viewId++) {
                        if(activeFilter.isSelected(table.getRowSorter().convertRowIndexToModel(viewId), source)) {
                                nextViewSelection.add(viewId);
                                int newProg = viewId / rowCount * 100;
                                if(lastprog != newProg) {
                                        lastprog = newProg;
                                        pm.progressTo(newProg);
                                }
                                if(pm.isCancelled()) {
                                        return;
                                }
                        }
                }
                pm.endTask();
                SwingUtilities.invokeLater( new Runnable() {

                        @Override
                        public void run() {
                                // Update the table values
                                Iterator<Integer> intervals = nextViewSelection.getValueRanges().iterator();
                                try {
                                        table.getSelectionModel().setValueIsAdjusting(true);
                                        table.clearSelection();
                                        while (intervals.hasNext()) {
                                                int begin = intervals.next();
                                                int end = intervals.next();
                                                table.addRowSelectionInterval(begin, end);
                                                if (pm.isCancelled()) {
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
