 
package org.orbisgis.view.main.frames;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentListener;
import java.awt.event.FocusListener;
import java.awt.event.MouseListener;
import java.beans.EventHandler;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
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
 */
public class MainFrameStatusBar extends StatusBar {
        //Layout parameters
        private final static int OUTER_BAR_BORDER = 1;
        private final static int HORIZONTAL_EMPTY_BORDER = 4;
        private final static int STATUS_BAR_HEIGHT = 30;
        //JobLabel
        private JPanel jobListBar;     //This component contain the first job panel
        //private JobListPanel jobList;  //Popup Panel
        private JobListModel runningJobs;
        private JobListItem firstJob;  //Job[0] listener & simplified panel
        JFrame jobPopup;               //The floating frame
        
        public MainFrameStatusBar() {
                super(OUTER_BAR_BORDER, HORIZONTAL_EMPTY_BORDER);
                setPreferredSize(new Dimension(-1,STATUS_BAR_HEIGHT));
                setMinimumSize(new Dimension(1,STATUS_BAR_HEIGHT));
                //Add the JobList
                makeJobList();
        }
        
        private void makeJobList() {
                runningJobs = new JobListModel().listenToBackgroundManager();
                runningJobs.addListDataListener(EventHandler.create(ListDataListener.class,this,"onListContentChanged"));
                jobListBar = new JPanel(new BorderLayout());
                //Set hand cursor to notify the user that a list/link can be poped-up
                jobListBar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                jobListBar.addMouseListener(EventHandler.create(MouseListener.class,this,"onUserClickJobLabel",null,"mouseClicked"));
                addComponent(jobListBar);
        }

        @Override
        public void removeNotify() {
                super.removeNotify();
                closeJobPopup();
                clearJobTitle();
                runningJobs.dispose();
        }
        
        /**
         * The user click on the Job label
         * The JobList component must be shown and the focus set on it
         */
        public void onUserClickJobLabel() {
                closeJobPopup();
                jobPopup = new JFrame();
                jobPopup.setUndecorated(true);
                jobPopup.requestFocusInWindow();
                //Create the jobList Panel
                JobListPanel jobList = new JobListPanel();
                jobList.setRenderer(new JobListCellRenderer());
                jobList.setModel(runningJobs);
                jobList.setBorder(BorderFactory.createEtchedBorder());
                jobPopup.setContentPane(jobList);
                //On lost focus this window must be closed
                jobPopup.addFocusListener(
                        EventHandler.create(FocusListener.class,this,
                        "onJobPopupLostFocus",null,"focusLost"));
                //On resize , this window must be moved
                jobPopup.addComponentListener(
                        EventHandler.create(ComponentListener.class,
                        this,"onJobPopupResize",null,"componentResized"));
                //Do size and place
                jobPopup.setVisible(true);
                jobPopup.pack();
                onJobPopupResize();
                
        }
        
        private void closeJobPopup() {
                if(jobPopup!=null) {
                        jobPopup.dispose();
                        jobPopup = null;
                }
        }
        /**
         * The user click outside the joblist
         * This window need to be closed
         */
        public void onJobPopupLostFocus() {
                closeJobPopup();
        }
        /**
         * On resize , the job list window must be moved
         * @param ce 
         */
        public void onJobPopupResize() {
                if(jobPopup!=null) {
                        Point labelLocation = jobListBar.getLocationOnScreen();
                        jobPopup.setLocation(new Point(labelLocation.x,labelLocation.y-jobPopup.getContentPane().getHeight()));
                }
                
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
                if(runningJobs.getSize()>0) {
                        JobListItem firstItem = (JobListItem)runningJobs.getElementAt(0);
                        //If the first job is not the one shown in the status bar
                        if(firstJob==null || !firstItem.equals(firstJob)) {
                                clearJobTitle();
                                //Create a local joblistitem (simplified)
                                firstJob = new JobListItem(firstItem.getJob()).listenToJob(true);
                                jobListBar.setVisible(true);
                                jobListBar.add(firstJob.getItemPanel(),BorderLayout.CENTER);
                        }
                        if(jobPopup!=null) {
                                jobPopup.pack();
                        }
                } else {
                        clearJobTitle();
                        closeJobPopup();
                }
        }
}
