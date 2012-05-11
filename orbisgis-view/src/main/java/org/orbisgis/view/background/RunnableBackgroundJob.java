/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *
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
 * info _at_ orbisgis.org
 */
package org.orbisgis.view.background;

import org.apache.log4j.Logger;
import org.orbisgis.progress.ProgressMonitor;

public class RunnableBackgroundJob implements Runnable {

	private Job job;
	private ProgressMonitor pm;
	private BackgroundManager jobQueue;
        private static final Logger LOGGER = Logger.getLogger(RunnableBackgroundJob.class);
	public RunnableBackgroundJob(BackgroundManager jobQueue, ProgressMonitor pm, Job job) {
		this.job = job;
		this.pm = pm;
		this.jobQueue = jobQueue;
	}

	public synchronized void run() {
		try {
			job.run(pm);
		} catch (Throwable t) {
			LOGGER.error(t.getMessage(), t);
		}
		jobQueue.processFinished(job.getId());
	}

}
