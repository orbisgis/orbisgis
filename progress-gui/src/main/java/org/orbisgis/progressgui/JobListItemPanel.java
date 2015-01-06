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

import java.awt.FlowLayout;
import java.awt.event.MouseListener;
import java.beans.EventHandler;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import org.orbisgis.commons.progress.SwingWorkerPM;
import org.orbisgis.progressgui.icons.JobsIcon;

/**
 * Panel of a JobList row
 */


public class JobListItemPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private SwingWorker job;
        private JLabel jobCancelLabel;
        private JLabel jobLabel;
        private JProgressBar jobProgressBar;
        private boolean statusBarJob;
        
        public JobListItemPanel(SwingWorker job,boolean statusBarJob) {
                this.job = job;
                this.statusBarJob = statusBarJob;
                //The panel show the background of the DataSource Item
                FlowLayout fl = new FlowLayout(FlowLayout.LEADING);
                fl.setHgap(5);
                fl.setVgap(0);
                setLayout(fl);
                //The progress Bar of the Job
                jobProgressBar = new JProgressBar(0, 100);
                jobProgressBar.setStringPainted(false);
                jobProgressBar.setIndeterminate(true);
                //The cancel label
                jobCancelLabel = new JLabel(JobsIcon.getIcon("cancel"));
                //When the user click on the label, the job is canceled
                jobCancelLabel.addMouseListener(
                        EventHandler.create(MouseListener.class,job,
                                      "cancel",null,"mouseClicked"));
                //The label show the text of the DataSource Item
                jobLabel = new JLabel();
                add(jobProgressBar);
                if(!statusBarJob) {
                        add(jobCancelLabel);
                }
                //Add the label into the Panel
                add(jobLabel);
                //On the status bar the job is aligned on the right
                if(statusBarJob) {
                        add(jobCancelLabel);
                }
                readJob();
        }

        public JLabel getJobCancelLabel() {
                return jobCancelLabel;
        }

        /***
         * Update the Panel content
         */
        public final void readJob() {
                jobLabel.setText(getText());
                int progress = job.getProgress();
                if(progress>0 && progress<=100) {
                        jobProgressBar.setStringPainted(true);
                        jobProgressBar.setIndeterminate(false);
                        jobProgressBar.setValue(progress);
                } else {
                        jobProgressBar.setIndeterminate(true);
                }
        }
        /**
         * @return text from job data
         */
        public final String getText() {
                StringBuilder sb = new StringBuilder();
                if(!statusBarJob) {
                        sb.append("<html>");
                }
                if(job instanceof SwingWorkerPM) {
                    sb.append(((SwingWorkerPM)job).getCurrentTaskName());
                } else {
                    sb.append(job.toString());
                }
                sb.append(" (");
                sb.append(job.getProgress());
                sb.append(" %)");
                if(!statusBarJob) {
                        sb.append("</html>");
                }
                return sb.toString();
        }        
}
