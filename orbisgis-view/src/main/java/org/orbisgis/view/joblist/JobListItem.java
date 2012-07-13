 /*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *
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
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */
package org.orbisgis.view.joblist;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.orbisgis.view.background.Job;
import org.orbisgis.view.background.ProgressListener;
import org.orbisgis.view.components.ContainerItemProperties;

/**
 * This list item is linked with a Job
 */

public class JobListItem extends ContainerItemProperties {
        //Minimal interval of refreshing label in ms
        private static final long updateLabelInterval = 50; 
        private long lastTimeUpdatedItem = 0;
        private Job job;
        private ProgressListener listener = 
                EventHandler.create(ProgressListener.class,
                                    this,
                                    "onJobUpdate");
        private JobListItemPanel itemPanel;
        private AtomicBoolean progressionModified = new AtomicBoolean(true);
        private Timer fetchProgressionTimer;
        private final static int PROGRESSION_TIMER_INTERVAL = 50;

        public JobListItemPanel getItemPanel() {
                return itemPanel;
        }
                
        public JobListItem(Job job) {
                super(job.getId().toString(), job.getTaskName());
                this.job = job;
        }

        /**
         * Update the list item on job changes and make the panel
         */
        public JobListItem listenToJob() {
                job.addProgressListener(listener);    
                itemPanel = new JobListItemPanel(job);
                onJobUpdate();
                fetchProgressionTimer = new Timer(PROGRESSION_TIMER_INTERVAL,new TimerFetchListener());
                fetchProgressionTimer.start();
                return this;
        }
        /**
         * Stop listening to the job and the timer
         */
        public void dispose() {
                job.removeProgressListener(listener);
                if(fetchProgressionTimer!=null) {
                        fetchProgressionTimer.stop();
                }
        }
        
        /**
         * The user click on the cancel button
         */
        public void onCancel() {
                job.cancel();
        }
        
        /**
         * Read the job to update labels and controls
         */
        private void updateJob() {
                if(itemPanel!=null) {
                        long now = System.currentTimeMillis();
                        //It is useless to update the controls on each
                        //job progression increment,
                        //then a minimal update interval is set
                        if(now - lastTimeUpdatedItem > updateLabelInterval) {
                                lastTimeUpdatedItem = now;
                                itemPanel.readJob();
                                setLabel(itemPanel.getText());
                        } else {
                                //Update the Job later
                                onJobUpdate();
                        }
                }   
        }
        /**
         * Update the JobPanel content and the item text later
         */
        public void onJobUpdate() {     
                progressionModified.set(true);
        }
        /**
         * 
         * @return The associated Job
         */
        public Job getJob() {
                return job;
        }
        /**
         * Listen to timer events
         */
        private class TimerFetchListener implements ActionListener {

                @Override
                public void actionPerformed(ActionEvent ae) {
                        //Update the job if the progression has been updated
                        if(progressionModified.getAndSet(false)) {
                                updateJob();
                        }
                }
                
        }
}
