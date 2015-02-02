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
package org.orbisgis.commons.progress;

import java.beans.PropertyChangeListener;

/**
 * Represents a way to report progress of a task.
 *
 * How to use:
 *
 * Your method receive a ProgressMonitor instance named A. You know the number of task in your method
 * then you call {@link ProgressMonitor#startTask(String, long)} and you receive a new instance of ProgressMonitor named B.
 * With B you can advance in task by calling {@link org.orbisgis.commons.progress.ProgressMonitor#endTask()} or
 * by passing B to the sub method parameter.
 * 
 * @author Fernando GONZALEZ CORTES
 * @author Thomas LEDUC
 * @author Antoine Gourlay <antoine@gourlay.fr>
 * @author Nicolas Fortin
 */
public interface ProgressMonitor {
        static final String PROP_PROGRESSION = "P";
        static final String PROP_CANCEL = "C";
        static final String PROP_TASKNAME = "T";

        /**
         * Create a new child task to this parent task.
         *
         * @param taskName
         *            Task name
         * @param end Number of task in the subtask
         */
        ProgressMonitor startTask(String taskName, long end);

        /**
         * Create a new child task to this parent task.
         * @param end Number of task in the subtask
         */
        ProgressMonitor startTask(long end);

        /**
         * Ends the currently running task.
         */
        void endTask();

        /**
         * @return the overall process task name.
         */
        String getCurrentTaskName();

        /**
         * Set the overall process task name
         */
        void setTaskName(String taskName);

        /**
         * Indicates the progress of the last added task.
         *
         * @param progress
         */
        void progressTo(long progress);

        /**
         * Gets the progress of the overall process.
         *
         * @return A value in the range [0-1]
         */
        double getOverallProgress();

        /**
         * Gets the progress of the current process.
         *
         * @return A value in the range [0-getEnd()[
         */
        long getCurrentProgress();

        /**
         * @return Number of task in this process
         */
        long getEnd();

        /**
         * Returns true if the process is canceled and should end as quickly as
         * possible.
         *
         * @return True if it should be canceled
         */
        boolean isCancelled();

        /**
         * Sets the cancel state of the process.
         * This method call property change listeners
         * @param cancelled New value
         */
        void setCancelled(boolean cancelled);

        /**
         * Add a property change listener. The property change listener belongs to the overall process.
         * @param property PROP_* name
         * @param listener Listener instance
         */
        void addPropertyChangeListener(String property, PropertyChangeListener listener);

        /**
         * @param listener PropertyChange listener to remove
         */
        void removePropertyChangeListener(PropertyChangeListener listener);
}
