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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Root progress monitor hold canceled property and listeners
 * @author Nicolas Fortin
 */
public class RootProgressMonitor extends DefaultProgressMonitor {
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
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
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    @Override
    public void addPropertyChangeListener(String property, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(property, listener);
    }

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    @Override
    public void setCancelled(boolean canceled) {
        boolean oldValue = this.canceled;
        this.canceled = canceled;
        propertyChangeSupport.firePropertyChange(PROP_CANCEL, oldValue, canceled);
    }

    @Override
    public String getCurrentTaskName() {
        return taskName;
    }

    @Override
    protected synchronized void pushProgression(double incProg) {
        double oldProgress = getOverallProgress();
        super.pushProgression(incProg);
        propertyChangeSupport.firePropertyChange(PROP_PROGRESSION, oldProgress, getOverallProgress());
    }

    @Override
    public void setTaskName(String taskName) {
        String oldTaskName = this.taskName;
        this.taskName = taskName;
        propertyChangeSupport.firePropertyChange(PROP_TASKNAME, oldTaskName, taskName);
    }
}
