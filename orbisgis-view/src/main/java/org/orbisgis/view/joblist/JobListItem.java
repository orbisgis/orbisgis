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


import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.beans.EventHandler;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import org.orbisgis.view.background.Job;
import org.orbisgis.view.background.ProgressListener;
import org.orbisgis.view.components.ContainerItemProperties;

/**
 * This list item is linked with a Job
 */

public class JobListItem extends ContainerItemProperties {
        //Minimal interval of refreshing label in ms
        private static final long updateLabelInterval = 50; 
        private Job job;
        private ProgressListener listener = 
                EventHandler.create(ProgressListener.class,
                                    this,
                                    "onJobUpdate");
        private JobListItemPanel itemPanel;
        private long lastTimeUpdatedItem = 0;
        
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
                return this;
        }
        /**
         * Stop listening to the job
         */
        public void dispose() {
                job.removeProgressListener(listener);
        }
        
        /**
         * The user click on the cancel button
         */
        public void onCancel() {
                job.cancel();
        }
        /**
         * Update the JobPanel content and the item text
         */
        public void onJobUpdate() {
                if(itemPanel!=null) {
                        long now = System.currentTimeMillis();
                        if(now - lastTimeUpdatedItem > updateLabelInterval) {
                                lastTimeUpdatedItem = now;
                                itemPanel.readJob();
                                setLabel(itemPanel.getText());
                        }
                }                
        }
        /**
         * 
         * @return The associated Job
         */
        public Job getJob() {
                return job;
        }
}
