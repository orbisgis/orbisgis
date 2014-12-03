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
package org.orbisgis.commons.progress;

import javax.swing.*;
import java.beans.PropertyChangeListener;

/**
 * @author Nicolas Fortin
 * Extend this class and use it as SwingWorker is documented in the Java API.This class compute the progression of
 * the swing worker and give the ability to set a task name.
 */
public abstract class ProgressMonitorSW<T, V> extends SwingWorker<T, V> implements ProgressMonitor {
    private final DefaultProgressMonitor defaultProgressMonitor;

    /**
     * Default constructor no default task name
     * @param taskName
     */
    protected ProgressMonitorSW(String taskName, long subprocessCount) {
        this.defaultProgressMonitor = new RootProgressMonitor(taskName, subprocessCount);
    }

    /**
     * Default constructor no default task name.
     */
    protected ProgressMonitorSW() {
        this.defaultProgressMonitor = new RootProgressMonitor(1);
    }

    /**
     * Default constructor no default task name.
     */
    protected ProgressMonitorSW(long subprocessCount) {
        this.defaultProgressMonitor = new RootProgressMonitor(subprocessCount);
    }

    @Override
    public void addPropertyChangeListener(String property, PropertyChangeListener listener) {
        defaultProgressMonitor.addPropertyChangeListener(property, listener);
    }

    @Override
    public void setTaskName(String taskName) {
        String oldValue = defaultProgressMonitor.getCurrentTaskName();
        defaultProgressMonitor.setTaskName(taskName);
        firePropertyChange(ProgressMonitor.PROP_TASKNAME, oldValue, taskName);
    }

    @Override
    public ProgressMonitor startTask(String taskName, long end) {
        return defaultProgressMonitor.startTask(taskName, end);
    }

    @Override
    public ProgressMonitor startTask(long end) {
        return defaultProgressMonitor.startTask(end);
    }

    private void updateProgression() {
        setProgress((int)(defaultProgressMonitor.getOverallProgress() * 100));
    }

    @Override
    public void endTask() {
        defaultProgressMonitor.endTask();
        updateProgression();
    }

    @Override
    public String getCurrentTaskName() {
        return defaultProgressMonitor.getCurrentTaskName();
    }

    @Override
    public void finalize() throws Throwable {
        super.finalize();
        defaultProgressMonitor.finalize();
    }

    @Override
    public void progressTo(long progress) {
        defaultProgressMonitor.progressTo(progress);
        updateProgression();
    }

    @Override
    public double getOverallProgress() {
        return defaultProgressMonitor.getOverallProgress();
    }

    @Override
    public long getCurrentProgress() {
        return defaultProgressMonitor.getCurrentProgress();
    }

    @Override
    public long getEnd() {
        return defaultProgressMonitor.getEnd();
    }

    @Override
    public void setCancelled(boolean cancelled) {
        if(cancelled) {
            cancel(false);
        }
    }
}
