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
package org.gdms.data.indexes;

import java.io.File;
import java.io.IOException;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.values.Value;
import org.orbisgis.progress.IProgressMonitor;

public interface DataSourceIndex extends AdHocIndex {

	/**
	 * To update the index.
	 *
	 * @param direction
	 * @throws IndexException
	 */
	public void deleteRow(Value value, int row) throws IndexException;

	/**
	 * To update the index
	 *
	 * @throws IndexException
	 */
	public void insertRow(Value value, int row) throws IndexException;

	/**
	 * To update the index
	 *
	 * @throws IndexException
	 */
	public void setFieldValue(Value oldGeometry, Value newGeometry, int rowIndex)
			throws IndexException;

	/**
	 * Gets the field this index is built on
	 *
	 * @return
	 */
	public String getFieldName();

	/**
	 * Sets the name of the field to index
	 *
	 * @param fieldName
	 */
	public void setFieldName(String fieldName);

	/**
	 * Indexes the specified field of the specified source
	 *
	 * @param ds
	 * @param fieldName
	 * @param pm
	 * @param indexFile
	 * @throws IndexException
	 */
	public void buildIndex(DataSourceFactory dsf, DataSource ds,
			IProgressMonitor pm) throws IndexException;

	/**
	 * Loads the index information from the file
	 *
	 * @param file
	 * @throws IOException
	 */
	public void load() throws IndexException;

	/**
	 * Stores the information of this index at disk
	 *
	 * @throws IndexException
	 */
	public void save() throws IndexException;

	/**
	 * Sets the file related with the index
	 *
	 * @param file
	 */
	public void setFile(File file);

	/**
	 * Gets the file this index uses for storage
	 *
	 * @return
	 */
	public File getFile();

	/**
	 * Frees all the resources used by the index
	 *
	 * @throws IOException
	 */
	public void close() throws IOException;

	/**
	 * Returns true if the method is called between an invocation to buildIndex
	 * or load and an invocation to close
	 *
	 * @return
	 */
	public boolean isOpen();

}
