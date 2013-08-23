/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier
 * SIG" team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
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
 * For more information, please consult: <http://www.orbisgis.org/> or contact
 * directly: info_at_ orbisgis.org
 */
package org.orbisgis.view.main.frames;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.event.FocusListener;
import java.awt.event.MouseListener;
import java.beans.EventHandler;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ListDataListener;
import org.orbisgis.core.Services;
import org.orbisgis.core.workspace.CoreWorkspace;
import org.orbisgis.sif.components.CustomButton;
import org.orbisgis.view.components.statusbar.StatusBar;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.joblist.JobListCellRenderer;
import org.orbisgis.view.joblist.JobListItem;
import org.orbisgis.view.joblist.JobListModel;
import org.orbisgis.view.joblist.JobListPanel;
import org.orbisgis.view.workspace.WorkspaceSelectionDialog;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * The status bar of the MainFrame.
 */
public class MainFrameStatusBar extends StatusBar {

        private static final I18n I18N = I18nFactory.getI18n(MainFrameStatusBar.class);
        //Layout parameters
        private static final int OUTER_BAR_BORDER = 1;
        private static final int HORIZONTAL_EMPTY_BORDER = 4;
        private static final int STATUS_BAR_HEIGHT = 30;
        //JobLabel
        private JPanel jobListBar;     //This component contain the first job panel
        //private JobListPanel jobList;  //Popup Panel
        private JobListModel runningJobs;
        private JobListItem firstJob;  //Job[0] listener & simplified panel
        private JFrame jobPopup;               //The job floating frame
        private PopupMessageDialog messagePopup;
        private JFrame owner;
        //
        private AtomicBoolean listenToLogger = new AtomicBoolean(false);

        public MainFrameStatusBar(JFrame frame) {
                super(OUTER_BAR_BORDER, HORIZONTAL_EMPTY_BORDER);
                this.owner = frame;
                setPreferredSize(new Dimension(-1, STATUS_BAR_HEIGHT));
                setMinimumSize(new Dimension(1, STATUS_BAR_HEIGHT));
                //Add the JobList
                makeJobList();
                makeWorkspaceManager();
        }

        @Override
        public void addNotify() {
                super.addNotify();
                // Popup disabled
                //if(!listenToLogger.getAndSet(true)) {
                        // - At the current state this popup is too aggressive, and keep all logs message
                        // - Only the last message should be shown without keeping all non displayed messages.
                        // - The user should be able to enable/disable this feature
                        // messagePopup = new PopupMessageDialog(this, owner);
                        // messagePopup.init();
                //}
        }

        private void makeJobList() {
                runningJobs = new JobListModel().listenToBackgroundManager();
                runningJobs.addListDataListener(EventHandler.create(ListDataListener.class, this, "onListContentChanged"));
                jobListBar = new JPanel(new BorderLayout());
                //Set hand cursor to notify the user that a list/link can be poped-up
                jobListBar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                jobListBar.addMouseListener(EventHandler.create(MouseListener.class, this, "onUserClickJobLabel", null, "mouseClicked"));
                addComponent(jobListBar, SwingConstants.RIGHT);
        }

        private void makeWorkspaceManager() {
                JPanel workspaceBar = new JPanel(new BorderLayout());
                JButton btnChangeWorkspace = new CustomButton(OrbisGISIcon.getIcon("application_go"));
                btnChangeWorkspace.setToolTipText(I18N.tr("Switch to another workspace"));
                btnChangeWorkspace.addActionListener(EventHandler.create(ActionListener.class,this,"onChangeWorkspace"));
                workspaceBar.add(btnChangeWorkspace,BorderLayout.WEST);
                CoreWorkspace coreWorkspace = Services.getService(CoreWorkspace.class);
                if(coreWorkspace!=null) {
                        JLabel workspacePath = new JLabel(coreWorkspace.getWorkspaceFolder());
                        workspaceBar.add(workspacePath,BorderLayout.CENTER);
                }
                addComponent(workspaceBar, SwingConstants.LEFT);
        }

        @Override
        public void removeNotify() {
                super.removeNotify();
                closeJobPopup();
                clearJobTitle();
                runningJobs.dispose();
                if(messagePopup!=null) {
                    messagePopup.dispose();
                }
        }
        /**
         * The user click on change workspace button
         */
        public void onChangeWorkspace() {
                CoreWorkspace coreWK = Services.getService(CoreWorkspace.class);
                if(coreWK!=null) {
                        File newWorkspace = WorkspaceSelectionDialog.showWorkspaceFolderSelection(this.owner, coreWK);
                        if(newWorkspace!= null) {
                                // Switching workspace..
                                coreWK.setWorkspaceFolder(newWorkspace.getAbsolutePath());
                        }
                }
        }
        /**
         * The user click on the Job label The JobList component must be shown
         * and the focus set on it
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
                        EventHandler.create(FocusListener.class, this,
                        "onJobPopupLostFocus", null, "focusLost"));
                //On resize , this window must be moved
                jobPopup.addComponentListener(
                        EventHandler.create(ComponentListener.class,
                        this, "onJobPopupResize", null, "componentResized"));
                //Do size and place
                jobPopup.setVisible(true);
                jobPopup.pack();
                onJobPopupResize();

        }

        private void closeJobPopup() {
                if (jobPopup != null) {
                        jobPopup.dispose();
                        jobPopup = null;
                }
        }

        /**
         * The user click outside the joblist This window need to be closed
         */
        public void onJobPopupLostFocus() {
                closeJobPopup();
        }

        /**
         * On resize , the job list window must be moved
         *
         * @param ce
         */
        public void onJobPopupResize() {
                if (jobPopup != null) {
                        Point labelLocation = jobListBar.getLocationOnScreen();
                        jobPopup.setLocation(new Point(labelLocation.x, labelLocation.y - jobPopup.getContentPane().getHeight()));
                }

        }

        private void clearJobTitle() {
                if (firstJob != null) {
                        firstJob.dispose();
                }
                firstJob = null;
                if (jobListBar != null) {
                        if (jobListBar.getComponentCount() > 0) {
                                jobListBar.remove(0);
                        }
                        jobListBar.setVisible(false);
                }
        }

        /**
         * The list content has been updated, the panel title label must be
         * hide/shown
         */
        public void onListContentChanged() {
                if (runningJobs.getSize() > 0) {
                        JobListItem firstItem = (JobListItem) runningJobs.getElementAt(0);
                        //If the first job is not the one shown in the status bar
                        if (firstJob == null || !firstItem.equals(firstJob)) {
                                clearJobTitle();
                                //Create a local joblistitem (simplified)
                                firstJob = new JobListItem(firstItem.getJob()).listenToJob(true);
                                jobListBar.setVisible(true);
                                jobListBar.add(firstJob.getItemPanel(), BorderLayout.CENTER);
                        }
                        if (jobPopup != null) {
                                jobPopup.pack();
                        }
                } else {
                        clearJobTitle();
                        closeJobPopup();
                }
        }
}
