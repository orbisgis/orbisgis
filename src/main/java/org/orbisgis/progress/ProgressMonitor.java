/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan Bocher, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
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
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.orbisgis.progress;

/**
 * Represents a way to report progress of a task.
 */
public interface ProgressMonitor {

        /**
         * Initialize a new task with the given end.
         * @param taskName the name of the task
         * @param end the end of the progress of the task
         */
        void init(String taskName, long end);

        /**
         * Adds a new child task to the last added.
         *
         * @param taskName
         *            Task name
         * @param end  
         */
        void startTask(String taskName, long end);

        /**
         * Ends the currently running task.
         */
        void endTask();

        /**
         * Gets the current name of the task. The name at init or the name at the
         * last call to startTask if any.
         *
         * @return
         */
        String getCurrentTaskName();

        /**
         * Indicates the progress of the last added task.
         *
         * @param progress
         */
        void progressTo(long progress);

        /**
         * Gets the progress of the overall process.
         *
         * @return
         */
        int getOverallProgress();

        /**
         * Gets the progress of the current process.
         *
         * @return
         */
        int getCurrentProgress();

        /**
         * Returns true if the process is canceled and should end as quickly as
         * possible.
         *
         * @return
         */
        boolean isCancelled();

        /**
         * Sets the cancel state of the process.
         * @param cancelled
         */
        void setCancelled(boolean cancelled);
}
