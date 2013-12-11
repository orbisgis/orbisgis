/**
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
package org.orbisgis.view.background;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.progress.RootProgressMonitor;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

public class Job implements BackgroundJob {

    private JobId processId;
    private BackgroundJob lp;
    private ProgressMonitor pm;
    private BackgroundManager jobQueue;
    private Thread currentThread = null;
    private boolean isBlocking;
    private static final I18n I18N = I18nFactory.getI18n(Job.class);


    public Job(JobId processId, BackgroundJob lp, BackgroundManager jobQueue, boolean blocking) {
        this.processId = processId;
        this.lp = lp;
        this.pm = new RootProgressMonitor(lp.getTaskName(), 1);
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
        return pm;
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
        pm.setCancelled(true);
    }

    /**
     * Gets an instance of {@code Runnable} that is ready to be run.
     * @return
     */
    public Thread getReadyRunnable(){
        currentThread = new Thread(new RunnableBackgroundJob(jobQueue, pm, this));
        return currentThread;
    }

    public synchronized void start() {
        RunnableBackgroundJob runnable = new RunnableBackgroundJob(jobQueue,
                pm, this);
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
}
