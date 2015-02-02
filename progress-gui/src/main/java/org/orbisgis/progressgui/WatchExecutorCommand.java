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
package org.orbisgis.progressgui;

import org.orbisgis.commons.progress.SwingWorkerPM;
import org.orbisgis.progressgui.api.SwingWorkerPool;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.apache.felix.service.command.Descriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.SwingWorker;
import java.util.ArrayList;

/**
 * Command shell for watching running SwingWorker
 * @author Nicolas Fortin
 */
@Component(immediate = true, property = {"osgi.command.scope=run", "osgi.command.function=list",
        "osgi.command.function=stop", "osgi.command.function=help"}, service = WatchExecutorCommand.class)
public class WatchExecutorCommand {
    private SwingWorkerPool watchExecutorService;
    private static final String COMMAND = "sw";
    private static final Logger LOGGER = LoggerFactory.getLogger(WatchExecutorService.class);

    @Reference
    public void setWatchExecutorService(SwingWorkerPool watchExecutorService) {
        this.watchExecutorService = watchExecutorService;
    }

    public void unsetWatchExecutorService(SwingWorkerPool watchExecutorService) {
        this.watchExecutorService = null;
    }

    public String getUsage() {
        return COMMAND +" (list|stop <jobId>)";
    }

    @Descriptor("List active SwingWorker(s)")
    public void list() {
        LOGGER.info("List of active SwingWorker :");
        for (Runnable runnable : new ArrayList<>(watchExecutorService.getQueue())) {
            String taskName = "", progression = "";
            if (runnable instanceof SwingWorkerPM) {
                SwingWorkerPM swingWorkerPM = (SwingWorkerPM) runnable;
                progression = " " + String.valueOf((int) (swingWorkerPM.getProgressMonitor().getOverallProgress() * 10000) / 100) + " %";
                if (swingWorkerPM.isDone()) {
                    progression = " done";
                }
                taskName = swingWorkerPM.getProgressMonitor().getCurrentTaskName();
            } else if (runnable instanceof SwingWorker) {
                SwingWorker swingWorker = (SwingWorker) runnable;
                taskName = runnable.toString();
                progression = " " + swingWorker.getProgress() + " %";
                if (swingWorker.isDone()) {
                    progression = " done";
                }
            }
            LOGGER.info(taskName + progression);
        }
    }

    @Descriptor("Stop a SwingWorker")
    public void stop(@Descriptor("Job identifier") Integer jobId) {
        LOGGER.info(String.valueOf(jobId));
    }

    @Descriptor("Print usage")
    public void help() {
        LOGGER.info(getUsage());
    }
}
