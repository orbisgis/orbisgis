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
package org.orbisgis.progress;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Root progress monitor hold canceled property and listeners
 * @author Nicolas Fortin
 */
public class RootProgressMonitor extends DefaultProgressMonitor {
    private PropertyChangeSupport listeners = new PropertyChangeSupport(this);
    private boolean canceled;
    private String taskName = "";

    /**
     * Constructor.
     * @param subprocess_size Number of task in this process.
     */
    public RootProgressMonitor(long subprocess_size) {
        super(subprocess_size, null);
    }

    /**
     *
     * @param taskName
     * @param subprocess_size
     */
    public RootProgressMonitor(String taskName, long subprocess_size) {
        super(subprocess_size, null);
        this.taskName = taskName;
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
        listeners.firePropertyChange(PROP_PROGRESSION, oldProgress, getOverallProgress());
    }

    @Override
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
}
