 
package org.orbisgis.view.main.frames;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.beans.EventHandler;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;
import org.orbisgis.view.components.statusbar.StatusBar;
import org.orbisgis.view.joblist.JobListCellRenderer;
import org.orbisgis.view.joblist.JobListItem;
import org.orbisgis.view.joblist.JobListModel;
import org.orbisgis.view.joblist.JobListPanel;

/**
 * The status bar of the MainFrame
 * @author fortin
 */
public class MainFrameStatusBar extends StatusBar {
        //Layout parameters
        private final static int OUTER_BAR_BORDER = 1;
        private final static int HORIZONTAL_EMPTY_BORDER = 4;
        private final static int STATUS_BAR_HEIGHT = 30;
        //JobLabel
        private JPanel jobListBar;     //This component contain the first job panel
        private JobListPanel jobList;  //Popup Panel
        private JobListItem firstJob;  //Job[0] listener & simplified panel
        
        public MainFrameStatusBar() {
                super(OUTER_BAR_BORDER, HORIZONTAL_EMPTY_BORDER);
                setPreferredSize(new Dimension(-1,STATUS_BAR_HEIGHT));
                setMinimumSize(new Dimension(1,STATUS_BAR_HEIGHT));
                //Add the JobList
                makeJobList();
        }
        
        private void makeJobList() {
                jobList = new JobListPanel();
                jobList.setRenderer(new JobListCellRenderer());
                jobList.setModel(new JobListModel().listenToBackgroundManager());
                jobList.getModel().addListDataListener(EventHandler.create(ListDataListener.class,this,"onListContentChanged"));
                //jobList.addContainerListener(EventHandler.create(ContainerListener.class,this,"onListContentChanged"));
                jobListBar = new JPanel(new BorderLayout());
                addComponent(jobListBar);
        }

        @Override
        public void removeNotify() {
                super.removeNotify();
                clearJobTitle();
        }
        
        
        
        private void clearJobTitle() {
                if(firstJob!=null) {
                        firstJob.dispose();
                }
                firstJob = null;
                if(jobListBar!=null) {
                        if(jobListBar.getComponentCount()>0) {
                                jobListBar.remove(0);
                        }
                        jobListBar.setVisible(false); 
                }
        }
        
        /**
         * The list content has been updated,
         * the panel title label must be hide/shown
         */
        public void onListContentChanged() {
                ListModel lm = jobList.getModel();
                if(lm.getSize()>0) {
                        JobListItem firstItem = (JobListItem)lm.getElementAt(0);
                        //If the first job is not the one shown in the status bar
                        if(firstJob==null || !firstItem.equals(firstJob)) {
                                clearJobTitle();
                                //Create a local joblistitem (simplified)
                                firstJob = new JobListItem(firstItem.getJob()).listenToJob(true);
                                jobListBar.setVisible(true);
                                jobListBar.add(firstJob.getItemPanel(),BorderLayout.CENTER);
                        }
                        
                } else {
                        clearJobTitle();
                }
        }     
}
