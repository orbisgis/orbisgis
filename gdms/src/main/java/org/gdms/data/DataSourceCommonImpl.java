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
package org.gdms.data;

import org.gdms.data.edition.EditionListener;
import org.gdms.data.edition.MetadataEditionListener;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

/**
 * Base class with the common implementation for non-decorator DataSource
 * implementations
 *
 * @author Fernando Gonzalez Cortes
 */
public abstract class DataSourceCommonImpl extends AbstractDataSource {

	protected DataSourceFactory dsf;

	/**
	 * @see org.gdms.data.DataSource#getDataSourceFactory()
	 */
	public DataSourceFactory getDataSourceFactory() {
		return dsf;
	}

	/**
	 * @see org.gdms.data.DataSource#setDataSourceFactory(DataSourceFactory)
	 */
	public void setDataSourceFactory(DataSourceFactory dsf) {
		this.dsf = dsf;
	}

	/**
	 * Redoes the last undone edition action
	 *
	 * @throws DriverException
	 */
	public void redo() throws DriverException {
		throw new UnsupportedOperationException(
				"Not supported. Try to obtain the DataSource with the DataSourceFactory.UNDOABLE constant");
	}

	/**
	 * Undoes the last edition action
	 *
	 * @throws DriverException
	 */
	public void undo() throws DriverException {
		throw new UnsupportedOperationException(
				"Not supported. Try to obtain the DataSource with the DataSourceFactory.UNDOABLE constant");
	}

	/**
	 * @return true if there is an edition action to redo
	 *
	 */
	public boolean canRedo() {
		return false;
	}

	/**
	 * @return true if there is an edition action to undo
	 *
	 */
	public boolean canUndo() {
		return false;
	}

	public boolean isOpen() {
		return false;
	}

	public void deleteRow(long rowId) throws DriverException {
		throw new UnsupportedOperationException("The DataSource wasn't "
				+ "retrieved with edition capabilities");
	}

	public void insertFilledRow(Value[] values) throws DriverException {
		throw new UnsupportedOperationException("The DataSource wasn't "
				+ "retrieved with edition capabilities");
	}

	public void insertEmptyRow() throws DriverException {
		throw new UnsupportedOperationException("The DataSource wasn't "
				+ "retrieved with edition capabilities");
	}

	public void insertFilledRowAt(long index, Value[] values)
			throws DriverException {
		throw new UnsupportedOperationException("The DataSource wasn't "
				+ "retrieved with edition capabilities");
	}

	public void insertEmptyRowAt(long index) throws DriverException {
		throw new UnsupportedOperationException("The DataSource wasn't "
				+ "retrieved with edition capabilities");
	}

	public void setFieldValue(long row, int fieldId, Value value)
			throws DriverException {
		throw new UnsupportedOperationException("The DataSource wasn't "
				+ "retrieved with edition capabilities");
	}

	public void addEditionListener(EditionListener listener) {
		throw new UnsupportedOperationException("The DataSource wasn't "
				+ "retrieved with edition capabilities");
	}

	public void removeEditionListener(EditionListener listener) {
		throw new UnsupportedOperationException("The DataSource wasn't "
				+ "retrieved with edition capabilities");
	}

	public void setDispatchingMode(int dispatchingMode) {
		throw new UnsupportedOperationException("The DataSource wasn't "
				+ "retrieved with edition capabilities");
	}

	public int getDispatchingMode() {
		throw new UnsupportedOperationException("The DataSource wasn't "
				+ "retrieved with edition capabilities");
	}

	public void endUndoRedoAction() {
		throw new UnsupportedOperationException("The DataSource wasn't "
				+ "retrieved with edition capabilities");
	}

	public void startUndoRedoAction() {
		throw new UnsupportedOperationException("The DataSource wasn't "
				+ "retrieved with edition capabilities");
	}

	public void addField(String name, Type type) throws DriverException {
		throw new UnsupportedOperationException("The DataSource wasn't "
				+ "retrieved with edition capabilities");
	}

	public void removeField(int fieldId) throws DriverException {
		throw new UnsupportedOperationException("The DataSource wasn't "
				+ "retrieved with edition capabilities");
	}

	public void setFieldName(int fieldId, String newFieldName)
			throws DriverException {
		throw new UnsupportedOperationException("The DataSource wasn't "
				+ "retrieved with edition capabilities");
	}

	public void addMetadataEditionListener(MetadataEditionListener listener) {
		throw new UnsupportedOperationException("The DataSource wasn't "
				+ "retrieved with edition capabilities");
	}

	public void removeMetadataEditionListener(MetadataEditionListener listener) {
		throw new UnsupportedOperationException("The DataSource wasn't "
				+ "retrieved with edition capabilities");
	}

	public boolean isModified() {
		return false;
	}

}