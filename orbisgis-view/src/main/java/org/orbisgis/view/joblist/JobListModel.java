/**
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
package org.orbisgis.view.joblist;


import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.AbstractListModel;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.orbisgis.core.Services;
import org.orbisgis.view.background.BackgroundListener;
import org.orbisgis.view.background.BackgroundManager;
import org.orbisgis.view.background.Job;
import org.orbisgis.sif.common.ContainerItemProperties;

/**
 * JList model of the Job list
 */
public class JobListModel extends AbstractListModel {
        private static final Logger LOGGER = Logger.getLogger(JobListModel.class);
        private List<JobListItem> shownJobs = new ArrayList<JobListItem>();
        /*!< If true a swing runnable is pending to refresh the content of
          the JobListModel
         */
        private AtomicBoolean awaitingRefresh=new AtomicBoolean(false); 
        private PropertyChangeListener labelUpdateListener;
        
        //Store Job events
        private List<Job> jobAdded = Collections.synchronizedList(new LinkedList<Job>());
        private List<Job> jobRemoved = Collections.synchronizedList(new LinkedList<Job>());
        private List<Job> jobUpdated = Collections.synchronizedList(new LinkedList<Job>());

        /**
         * Remove all items before the release of this object
         */
        public void dispose() {
                while(!shownJobs.isEmpty()) {
                        shownJobs.remove(0).dispose();
                }
        }
              
        
        
        
        /**
         * Attach listeners to the BackgroundManager
         * @return itself
         */
        public JobListModel listenToBackgroundManager() {
                BackgroundManager bm = Services.getService(BackgroundManager.class);
                //bm.addBackgroundListener(EventHandler.create(BackgroundListener.class,this,"onJobListChange"));
                bm.addBackgroundListener(new JobListBackgroundListener());
                labelUpdateListener = EventHandler.create(PropertyChangeListener.class, this, "onJobItemLabelChange","source");
                return this;
        }
        
        @Override
        public int getSize() {
                return shownJobs.size();
        }

        /**
         * A job item change and the List must be notified
         * @param item 
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
                        Job job = jobAdded.remove(0);
                        //Added
                        JobListItem addedJobItem = new JobListItem(job).listenToJob(false);
                        addedJobItem.addPropertyChangeListener(ContainerItemProperties.PROP_LABEL,labelUpdateListener);
                        shownJobs.add(addedJobItem);
                        fireIntervalAdded(addedJobItem, shownJobs.size() - 1, shownJobs.size() - 1);
                        LOGGER.debug("JobListModel:jobAdded");
                }
                //Removed
                while(!jobRemoved.isEmpty()) {
                        Job job = jobRemoved.remove(0);
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
                        Job job = jobUpdated.remove(0);
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
                        awaitingRefresh.set(false);
                        updateJobList();
                }
                
        }
        
        private class JobListBackgroundListener implements BackgroundListener {

                @Override
                public void jobAdded(Job job) {
                        jobAdded.add(job);
                        onJobListChange();
                }

                @Override
                public void jobRemoved(Job job) {
                        jobRemoved.add(job);
                        onJobListChange();
                }

                @Override
                public void jobReplaced(Job job) {
                        jobUpdated.add(job);
                        onJobListChange();
                }
                
        }
}
