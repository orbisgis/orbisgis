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
package org.orbisgis.view.background;

import org.orbisgis.progress.DefaultProgressMonitor;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.progress.RootProgressMonitor;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Encapsulate a background job in order to attach Thread instance and job identifier.
 */
public class Job extends DefaultProgressMonitor implements BackgroundJob {

    private PropertyChangeSupport listeners = new PropertyChangeSupport(this);
    private boolean canceled;
    private String taskName = "";
    private JobId processId;
    private BackgroundJob lp;
    private BackgroundManager jobQueue;
    private Thread currentThread = null;
    private boolean isBlocking;
    private static final I18n I18N = I18nFactory.getI18n(Job.class);
    private static final double INC_PROGRESS_FIRE = 0.01;
    private double lastFireProgress = 0;


    public Job(JobId processId, BackgroundJob lp, BackgroundManager jobQueue, boolean blocking) {
        super(1, null);
        this.processId = processId;
        this.lp = lp;
        this.isBlocking = blocking;
        this.jobQueue = jobQueue;
    }

    /**
     * @return True if the job with the same name cannot be run
     */
    public boolean isBlocking() {
        return isBlocking;
    }

    /**
     * @return The progress monitor of the job.
     */
    public ProgressMonitor getProgressMonitor() {
        return this;
    }

    @Override
    public String getTaskName() {
        return lp.getTaskName();
    }

    @Override
    public void run(ProgressMonitor pm) {
        lp.run(pm);
    }

    public JobId getId() {
        return processId;
    }

    public void setProcess(BackgroundJob lp) {
        this.lp = lp;
    }

    public void cancel() {
        setCancelled(true);
    }

    /**
     * Gets an instance of {@code Runnable} that is ready to be run.
     * @return
     */
    public Thread getReadyRunnable(){
        currentThread = new Thread(new RunnableBackgroundJob(jobQueue, this, this));
        return currentThread;
    }

    public synchronized void start() {
        RunnableBackgroundJob runnable = new RunnableBackgroundJob(jobQueue,
                this, this);
        currentThread = new Thread(runnable);
        currentThread.start();
    }

    public synchronized boolean isStarted() {
        return currentThread != null;
    }

    public void clear() {
        lp = new BackgroundJob() {

            @Override
            public void run(ProgressMonitor pm) {
            }

            @Override
            public String getTaskName() {
                return I18N.tr("Progressing...");
            }

        };
    }


    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listeners.removePropertyChangeListener(listener);
    }

    @Override
    public void addPropertyChangeListener(String property, PropertyChangeListener listener) {
        listeners.addPropertyChangeListener(property, listener);
    }

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    @Override
    public void setCancelled(boolean canceled) {
        boolean oldValue = this.canceled;
        this.canceled = canceled;
        listeners.firePropertyChange(PROP_CANCEL, oldValue, canceled);
    }

    @Override
    public String getCurrentTaskName() {
        return taskName;
    }

    @Override
    protected synchronized void pushProgression(double incProg) {
        double oldProgress = getOverallProgress();
        super.pushProgression(incProg);
        if(oldProgress - lastFireProgress > INC_PROGRESS_FIRE) {
            lastFireProgress = getOverallProgress();
            listeners.firePropertyChange(PROP_PROGRESSION, oldProgress, lastFireProgress);
        }
    }

    @Override
    public void setTaskName(String taskName) {
        String oldTaskName = this.taskName;
        this.taskName = taskName;
        listeners.firePropertyChange(PROP_TASKNAME, oldTaskName, taskName);
    }
}
