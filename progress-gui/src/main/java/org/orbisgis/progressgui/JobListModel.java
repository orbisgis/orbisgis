/**
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
package org.orbisgis.progressgui;

import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.AbstractListModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.orbisgis.progressgui.api.SwingWorkerPool;
import org.orbisgis.sif.common.ContainerItemProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JList model of the Job list
 */
public class JobListModel extends AbstractListModel {
        private static final int FETCH_JOB_TIME = 500;
        private static final Logger LOGGER = LoggerFactory.getLogger(JobListModel.class);
        private List<JobListItem> shownJobs = new ArrayList<>();
        /*!< If true a swing runnable is pending to refresh the content of
          the JobListModel
         */
        private AtomicBoolean awaitingRefresh=new AtomicBoolean(false); 
        private PropertyChangeListener labelUpdateListener = EventHandler.create(PropertyChangeListener.class, this, "onJobItemLabelChange","source");
        private ActionListener swingJobListener = EventHandler.create(ActionListener.class, this, "onNewWorker", "source");
        
        //Store Job events
        private List<SwingWorker> jobAdded = Collections.synchronizedList(new LinkedList<SwingWorker>());
        private List<SwingWorker> jobRemoved = Collections.synchronizedList(new LinkedList<SwingWorker>());
        private List<SwingWorker> jobUpdated = Collections.synchronizedList(new LinkedList<SwingWorker>());

        /**
         * Remove all items before the release of this object
         */
        public void dispose() {
            while(!shownJobs.isEmpty()) {
                shownJobs.remove(0).dispose();
            }
        }

        public void setSwingWorkerPool(SwingWorkerPool swingWorkerPool) {
            swingWorkerPool.addActionListener(swingJobListener);
        }

        public void unsetSwingWorkerPool(SwingWorkerPool swingWorkerPool) {
            swingWorkerPool.removeActionListener(swingJobListener);
        }


        /**
         * Attach listeners to the BackgroundManager
         * @return itself
         */
        public JobListModel listenToBackgroundManager() {
            return this;
        }

        /**
         * SwingWorker has been added to ThreadPool
         * @param swingWorker SwingWorker instance
         */
        public void onNewWorker(SwingWorker swingWorker) {
            jobAdded.add(swingWorker);
            // Track en of worker
            swingWorker.getPropertyChangeSupport().addPropertyChangeListener("state",
                    EventHandler.create(PropertyChangeListener.class, this, "onWorkerStateChange", ""));
            onJobListChange();
        }

        /**
         * Worker state changed
         * @param evt SwingWorker event
         */
        public void onWorkerStateChange(PropertyChangeEvent evt) {
            switch ((SwingWorker.StateValue)evt.getNewValue()) {
                case DONE:
                    jobRemoved.add((SwingWorker)evt.getSource());
                    break;
                default:
                    jobUpdated.add((SwingWorker)evt.getSource());
            }
        }

        @Override
        public int getSize() {
                return shownJobs.size();
        }

        /**
         * @param item New task value
         */
        public void onJobItemLabelChange(JobListItem item) {
                int jobIndex = shownJobs.indexOf(item);
                fireContentsChanged(item,jobIndex,jobIndex);
        }
        /**
         * JobList model need to be updated
         */
        public void onJobListChange() {
                if(!awaitingRefresh.getAndSet(true)) {
                        SwingUtilities.invokeLater(new ReadJobListOnSwingThread());
                }
        }
        /**
         * Update the shown job list
         * @warning called only by ReadJobListOnSwingThread
         */
        private void updateJobList() {
                while(!jobAdded.isEmpty()) {
                        SwingWorker job = jobAdded.remove(0);
                        //Added
                        JobListItem addedJobItem = new JobListItem(job).listenToJob(false);
                        addedJobItem.addPropertyChangeListener(ContainerItemProperties.PROP_LABEL,labelUpdateListener);
                        shownJobs.add(addedJobItem);
                        fireIntervalAdded(addedJobItem, shownJobs.size() - 1, shownJobs.size() - 1);
                        LOGGER.debug("JobListModel:jobAdded");
                }
                //Removed
                while(!jobRemoved.isEmpty()) {
                        SwingWorker job = jobRemoved.remove(0);
                        JobListItem jobId = new JobListItem(job);
                        int jobIndex = shownJobs.indexOf(jobId);
                        if(jobIndex!=-1) {
                                shownJobs.get(jobIndex).dispose();
                                shownJobs.remove(jobId);
                                fireIntervalRemoved(jobId, jobIndex, jobIndex);
                                LOGGER.debug("JobListModel:jobRemoved");
                        } else {
                                LOGGER.debug("JobListModel:jobRemoved fail to found the job");
                        }
                }
                //Updated
                while(!jobUpdated.isEmpty()) {
                        SwingWorker job = jobUpdated.remove(0);
                        JobListItem changedJobItem = new JobListItem(job).listenToJob(false);
                        fireContentsChanged(changedJobItem, 0, 0);
                        LOGGER.debug("JobListModel:jobReplaced");
                }                        
        }
        
        
        @Override
        public Object getElementAt(int i) {
                return shownJobs.get(i);
        }
        
        private class ReadJobListOnSwingThread  implements Runnable {
                @Override
                public void run() {
                    try {
                        do {
                            updateJobList();
                            Thread.sleep(FETCH_JOB_TIME);
                        } while (!jobAdded.isEmpty());
                    }catch (InterruptedException ex) {
                        //Ignore
                    } finally {
                        awaitingRefresh.set(false);
                    }
                }
                
        }
}
