/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;

/**
 * @author Nicolas Fortin
 * Extend this class and use it as SwingWorker is documented in the Java API.This class compute the progression of
 * the swing worker and give the ability to set a task name.
 */
public abstract class SwingWorkerPM<T, V> extends SwingWorker<T, V> {
    private final RootProgressMonitor progressMonitor;

    /**
     * Default constructor no default task name
     * @param taskName
     */
    protected SwingWorkerPM(String taskName, long subprocessCount) {
        this.progressMonitor = new RootProgressMonitor(taskName, subprocessCount);
        setListeners();
    }

    /**
     * Default constructor no default task name.
     */
    protected SwingWorkerPM() {
        this.progressMonitor = new RootProgressMonitor(1);
        setListeners();
    }

    @Override
    public String toString() {
        String taskName = progressMonitor.getCurrentTaskName();
        if(taskName == null || taskName.isEmpty()) {
            return this.getClass().getSimpleName();
        } else {
            return taskName;
        }
    }

    public void setTaskName(String taskName) {
        progressMonitor.setTaskName(taskName);
    }

    /**
     * Default constructor no default task name.
     */
    protected SwingWorkerPM(long subprocessCount) {
        this.progressMonitor = new RootProgressMonitor(subprocessCount);
        setListeners();
    }

    private void setListeners() {
        progressMonitor.addPropertyChangeListener(ProgressMonitor.PROP_PROGRESSION, EventHandler.create
                (PropertyChangeListener.class, this, "onProgressMonitorChange"));
        progressMonitor.addPropertyChangeListener(ProgressMonitor.PROP_CANCEL, EventHandler.create
                (PropertyChangeListener.class, this, "cancel"));
    }

    public void onProgressMonitorChange() {
        setProgress((int)(progressMonitor.getOverallProgress() * 100));
    }

    public RootProgressMonitor getProgressMonitor() {
        return progressMonitor;
    }

    public void addPropertyChangeListener(String property, PropertyChangeListener listener) {
        getPropertyChangeSupport().addPropertyChangeListener(property, listener);
    }

    private void updateProgression() {
        setProgress((int) (progressMonitor.getOverallProgress() * 100));
    }

    @Override
    public void finalize() throws Throwable {
        super.finalize();
        progressMonitor.finalize();
    }

    /**
     * Cancel Job
     */
    public void cancel() {
        progressMonitor.setCancelled(true);
        cancel(false);
    }
}
