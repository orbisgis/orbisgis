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
package org.orbisgis.pluginManager.background;

public interface BackgroundManager {

	/**
	 * Executes an operation in a background thread. This method blocks the
	 * interface so no more operation than cancel can be done
	 *
	 * @param lp
	 *            instance that executes the action.
	 */
	void backgroundOperation(BackgroundJob lp);

	/**
	 * Executes an operation in a background thread.
	 *
	 * @param lp
	 *            instance that executes the action.
	 */
	void nonBlockingBackgroundOperation(BackgroundJob lp);

	/**
	 * Executes an operation in a background thread. If there already exists an
	 * operation being executed or waiting to be executed with the same JobId as
	 * specified in this method the job is replaced
	 *
	 * @param processId
	 * @param lp
	 */
	void backgroundOperation(JobId processId, BackgroundJob lp);

	/**
	 * Executes an operation in a background thread without blocking the
	 * interface. If there already exists an operation being executed or waiting
	 * to be executed with the same JobId as specified in this method the job is
	 * replaced
	 *
	 * @param processId
	 * @param lp
	 */
	void nonBlockingBackgroundOperation(JobId processId, BackgroundJob lp);

	/**
	 * Gets a reference to the job queue
	 *
	 * @return
	 */
	JobQueue getJobQueue();

	/**
	 * Adds a listener to the Background process system
	 *
	 * @param listener
	 */
	void addBackgroundListener(BackgroundListener listener);

	/**
	 * Removes a listener from the Background process system
	 *
	 * @param listener
	 */
	void removeBackgroundListener(BackgroundListener listener);

}
