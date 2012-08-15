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

/**
 * Default implementation of an ProgressMonitor.
 */
public final class DefaultProgressMonitor implements ProgressMonitor {

        private static final int PERCENTAGEMAX = 100;
        private Task overallTask;
        private Task currentTask;
        private boolean cancelled;

        /**
         * Creates a new DefaultProgressMonitor.
         * @param taskName the name of the task
         * @param end the end of the progress of the task
         */
        public DefaultProgressMonitor(String taskName, int end) {
                init(taskName, end);
        }

        @Override
        public void init(String taskName, long end) {
                overallTask = new Task(taskName, end);
        }

        @Override
        public void startTask(String taskName, long end) {
                currentTask = new Task(taskName, end);
        }

        private static class Task {

                private String taskName;
                private int percentage;
                private long end;

                Task(String taskName, long end) {
                        this.taskName = taskName;
                        this.percentage = 0;
                        this.end = end > 0 ? end : 1;
                }
        }

        @Override
        public void endTask() {
                currentTask = null;
        }

        @Override
        public void progressTo(long progress) {
                if (currentTask != null) {
                        currentTask.percentage = (int) (progress * PERCENTAGEMAX / currentTask.end);
                } else {
                        overallTask.percentage = (int) (progress * PERCENTAGEMAX / overallTask.end);
                }
        }

        @Override
        public int getOverallProgress() {
                return overallTask.percentage;
        }

        @Override
        public String toString() {
                StringBuilder ret = new StringBuilder().append(overallTask.taskName).append(": ");
                ret.append(overallTask.percentage).append("\n");
                if (currentTask != null) {
                        ret.append(currentTask.taskName).append(": ");
                        ret.append(currentTask.percentage).append("\n");
                }

                return ret.toString();
        }

        @Override
        public synchronized boolean isCancelled() {
                return cancelled;
        }

        @Override
        public synchronized void setCancelled(boolean cancelled) {
                this.cancelled = cancelled;
        }

        @Override
        public String getCurrentTaskName() {
                if (currentTask != null) {
                        return currentTask.taskName;
                } else {
                        return null;
                }
        }

        @Override
        public int getCurrentProgress() {
                if (currentTask != null) {
                        return currentTask.percentage;
                } else {
                        return 0;
                }
        }
}
