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
package org.gdms.driver;

import java.io.File;
import java.io.IOException;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.metadata.Metadata;
import org.orbisgis.progress.IProgressMonitor;

/**
 * Interface to be implement by the File drivers that as also RW capabilities
 *
 */

public interface FileReadWriteDriver extends FileDriver, ReadWriteDriver {
	/**
	 * Copies the datasource from file in to file out
	 *
	 * @param in
	 * @param out
	 */
	void copy(File in, File out) throws IOException;

	/**
	 * Writes the content of the DataWare to the specified file.
	 *
	 * @param pm
	 *
	 * @param dataWare
	 *            DataWare with the contents
	 */
	void writeFile(File file, DataSource dataSource, IProgressMonitor pm)
			throws DriverException;

	/**
	 * Creates a new file with the given field names and types
	 *
	 * @param path
	 *            Path to the new file
	 * @param dsm
	 *            Metadata of the source
	 *
	 * @throws DriverException
	 *             If the creation fails
	 */
	void createSource(String path, Metadata metadata,
			DataSourceFactory dataSourceFactory) throws DriverException;
}