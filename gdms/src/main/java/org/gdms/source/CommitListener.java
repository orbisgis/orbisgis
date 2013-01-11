/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.source;

import org.gdms.driver.DriverException;

/**
 * Interface implemented for those decorators that have to keep their content up
 * to date when a commit is done
 *
 * @author Fernando Gonzalez Cortes
 *
 */
public interface CommitListener {

	/**
	 * The specified source is going to be changed
	 *
	 * @param name
	 *            name of the source
	 * @throws DriverException
	 *             If the commit should be cancelled
	 */
	void isCommiting(String name, Object source) throws DriverException;

	/**
	 * The specified source has been modified
	 *
	 * @param name
	 *            name of the source
	 * @throws DriverException
	 *             If some decorator could not be updated to the new contents
	 */
	void commitDone(String name) throws DriverException;

	/**
	 * Gets the name of the source this commit listener represents
	 *
	 * @return
	 */
	String getName();
}
