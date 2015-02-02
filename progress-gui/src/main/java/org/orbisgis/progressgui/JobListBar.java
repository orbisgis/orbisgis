/*
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
package org.orbisgis.progressgui;

import org.orbisgis.mainframe.api.MainWindow;
import org.orbisgis.progressgui.api.SwingWorkerPool;
import org.orbisgis.sif.components.StatusBar;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ListDataListener;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ComponentListener;
import java.awt.event.FocusListener;
import java.awt.event.MouseListener;
import java.beans.EventHandler;

/**
 * JobList status bar component
 * @author Nicolas Fortin
 */
@Component
public class JobListBar {
    private JPanel jobListBar; //This component contain the first job panel
    private JobListModel runningJobs = new JobListModel();
    private JobListItem firstJob; //Job[0] listener & simplified panel
    private JFrame jobPopup; //The job floating frame
    private JFrame owner;

    public JobListBar() {
    }

    @Reference
    public void setMainWindow(MainWindow mainWindow) {
        // Install on main window
        owner = mainWindow.getMainFrame();
        install(mainWindow.getStatusBar().getComponent());
    }

    public void unsetMainWindow(MainWindow mainWindow) {
        unInstall(mainWindow.getStatusBar().getComponent());
        closeJobPopup();
        owner = null;
    }

    @Reference
    public void setSwingWorkerPool(SwingWorkerPool swingWorkerPool) {
        runningJobs.setSwingWorkerPool(swingWorkerPool);
    }

    public void unsetSwingWorkerPool(SwingWorkerPool swingWorkerPool) {
        runningJobs.unsetSwingWorkerPool(swingWorkerPool);
    }

    public void install(StatusBar statusBar) {
        runningJobs.addListDataListener(EventHandler.create(ListDataListener.class, this, "onListContentChanged"));
        jobListBar = new JPanel(new BorderLayout());
        //Set hand cursor to notify the user that a list/link can be poped-up
        jobListBar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jobListBar.addMouseListener(EventHandler.create(MouseListener.class, this, "onUserClickJobLabel", null, "mouseClicked"));
        statusBar.addComponent(jobListBar, SwingConstants.RIGHT);
    }

    public void unInstall(StatusBar statusBar) {
        statusBar.removeComponent(jobListBar);
    }

    private void closeJobPopup() {
        if (jobPopup != null) {
            jobPopup.dispose();
            jobPopup = null;
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
                EventHandler.create(FocusListener.class, this, "onJobPopupLostFocus", null, "focusLost"));
        //On resize , this window must be moved
        jobPopup.addComponentListener(
                EventHandler.create(ComponentListener.class,
                        this, "onJobPopupResize", null, "componentResized"));
        //Do size and place
        jobPopup.setVisible(true);
        jobPopup.pack();
        onJobPopupResize();
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
