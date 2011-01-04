/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.progress;

/**
 * Default implementation of an IProgressMonitor.
 *
 */
public class ProgressMonitor implements IProgressMonitor {

        private Task overallTask;
        private Task currentTask;
        private boolean cancelled;

        public ProgressMonitor(String taskName) {
                init(taskName);
        }

        @Override
        public void init(String taskName) {
                overallTask = new Task(taskName);
        }

        @Override
        public void startTask(String taskName) {
                currentTask = new Task(taskName);
        }

        private class Task {

                String taskName;
                int percentage;

                Task(String taskName) {
                        this.taskName = taskName;
                        this.percentage = 0;
                }
        }

        @Override
        public void endTask() {
                currentTask = null;
        }

        @Override
        public void progressTo(int progress) {
                if (currentTask != null) {
                        this.currentTask.percentage = progress;
                } else {
                        overallTask.percentage = progress;
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
