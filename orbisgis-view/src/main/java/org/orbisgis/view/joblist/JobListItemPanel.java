
package org.orbisgis.view.joblist;

import java.awt.FlowLayout;
import java.awt.event.MouseListener;
import java.beans.EventHandler;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import org.orbisgis.view.background.Job;
import org.orbisgis.view.icons.OrbisGISIcon;

/**
 * Panel of a JobList row
 */


public class JobListItemPanel extends JPanel {
        private Job job;
        private JLabel jobCancelLabel;
        private JLabel jobLabel;
        private JProgressBar jobProgressBar;
        private boolean simplified;
        
        public JobListItemPanel(Job job,boolean simplified) {
                this.job = job;
                this.simplified = simplified;
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
                jobCancelLabel = new JLabel(OrbisGISIcon.getIcon("cancel"));
                //When the user click on the label, the job is canceled
                jobCancelLabel.addMouseListener(
                        EventHandler.create(MouseListener.class,job,
                                      "cancel",null,"mouseClicked"));
                //The label show the text of the DataSource Item
                jobLabel = new JLabel();
                add(jobProgressBar);
                add(jobCancelLabel);
                //Add the label into the Panel
                add(jobLabel);
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
                int progress = -1;
                if(job.getOverallProgress()>0) {
                        progress = job.getOverallProgress();
                }
                if(job.getCurrentProgress()>0) {
                        progress = job.getCurrentProgress();
                }
                if(progress>0 && progress<=100) {
                        jobProgressBar.setStringPainted(true);
                        jobProgressBar.setIndeterminate(false);
                        jobProgressBar.setValue(job.getCurrentProgress());
                } else {
                        jobProgressBar.setIndeterminate(true);
                }
                
        }
        /**
         * Build the text from job data
         * @return 
         */
        public final String getText() {
                StringBuilder sb = new StringBuilder();
                if(!simplified) {
                        sb.append("<html>");
                }
                sb.append(job.getTaskName());
                sb.append(" (");
                sb.append(job.getOverallProgress());
                sb.append(" %)");
                if(!simplified && job.getCurrentProgress()>0) {
                        sb.append("<br>&nbsp;");
                        sb.append(job.getCurrentTaskName());
                        sb.append(" (");
                        sb.append(job.getCurrentProgress());
                        sb.append(" %)");
                }
                if(!simplified) {
                        sb.append("</html>");
                }
                return sb.toString();
        }        
}
