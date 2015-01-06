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

import org.orbisgis.commons.progress.SwingWorkerPM;
import org.orbisgis.sif.common.ContainerItemProperties;

import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import javax.swing.SwingWorker;

/**
 * This list item is linked with a Job
 * @warning This component has a Timer running when the function listenToJob is 
 * called, always call the method dispose() when this instance is no longer used
 */

public class JobListItem extends ContainerItemProperties {
        private static final long serialVersionUID = 1L;
        private SwingWorker job;
        private PropertyChangeListener listener =
                EventHandler.create(PropertyChangeListener.class,
                                    this,
                                    "updateJob");
        private JobListItemPanel itemPanel;

        public JobListItemPanel getItemPanel() {
                return itemPanel;
        }
                
        public JobListItem(SwingWorker job) {
                super(job.toString(), job.toString());
                this.job = job;
        }

        public JobListItem(SwingWorkerPM job) {
            super(job.toString(), job.getCurrentTaskName());
            this.job = job;
        }

        /**
         * Update the list item on job changes and make the panel
         * @param simplifiedPanel Job displayed on a single line
         * @return 
         */
        public JobListItem listenToJob(boolean simplifiedPanel) {
            job.getPropertyChangeSupport().addPropertyChangeListener("progress", listener);
            itemPanel = new JobListItemPanel(job, simplifiedPanel);
            updateJob();
            return this;
        }
        /**
         * Stop listening to the job and the timer
         */
        public void dispose() {
                job.removePropertyChangeListener(listener);
        }
        
        /**
         * The user click on the cancel button
         */
        public void onCancel() {
                if(job instanceof SwingWorkerPM) {
                    ((SwingWorkerPM) job).setCancelled(true);
                } else {
                    job.cancel(false);
                }
        }
        
        /**
         * Read the job to update labels and controls
         */
        public void updateJob() {
                if(itemPanel!=null) {
                        itemPanel.readJob();
                        setLabel(itemPanel.getText());
                }   
        }

        /**
         * 
         * @return The associated Job
         */
        public SwingWorker getJob() {
                return job;
        }
}
