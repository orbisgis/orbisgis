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
/**
 *
 */
package org.gdms.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.gdms.data.edition.Commiter;
import org.gdms.data.edition.EditionListener;
import org.gdms.data.edition.MetadataEditionListener;
import org.gdms.data.indexes.IndexQuery;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.ReadOnlyDriver;
import org.gdms.source.Source;
import org.gdms.sql.strategies.IncompatibleTypesException;

public class AbstractDataSourceDecorator extends AbstractDataSource {
	private DataSource internalDataSource;

	ArrayList<String> fieldsName = new ArrayList<String>();

	public AbstractDataSourceDecorator(final DataSource internalDataSource) {
		this.internalDataSource = internalDataSource;
	}

	/**
	 * @return the internalDataSource
	 */
	public DataSource getDataSource() {
		return internalDataSource;
	}

	/**
	 * @param listener
	 * @see org.gdms.data.DataSource#addEditionListener(org.gdms.data.edition.EditionListener)
	 */
	public void addEditionListener(EditionListener listener) {
		internalDataSource.addEditionListener(listener);
	}

	/**
	 * @param name
	 * @param driverType
	 * @throws DriverException
	 * @see org.gdms.data.DataSource#addField(java.lang.String,
	 *      java.lang.String)
	 */
	public void addField(String name, Type driverType) throws DriverException {
		if (internalDataSource.getFieldIndexByName(name) == -1) {
			internalDataSource.addField(name, driverType);
		} else {
			try {
				throw new Exception("The field " + name + " already exists.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param listener
	 * @see org.gdms.data.DataSource#addMetadataEditionListener(org.gdms.data.edition.MetadataEditionListener)
	 */
	public void addMetadataEditionListener(MetadataEditionListener listener) {
		internalDataSource.addMetadataEditionListener(listener);
	}

	/**
	 * @throws DriverException
	 * @throws AlreadyClosedException
	 * @see org.gdms.data.DataSource#close()
	 */
	public void close() throws DriverException, AlreadyClosedException {
		internalDataSource.close();
	}

	/**
	 * @return
	 * @see org.gdms.data.DataSource#canRedo()
	 */
	public boolean canRedo() {
		return internalDataSource.canRedo();
	}

	/**
	 * @return
	 * @see org.gdms.data.DataSource#canUndo()
	 */
	public boolean canUndo() {
		return internalDataSource.canUndo();
	}

	/**
	 * @param fieldId
	 * @param value
	 * @return
	 * @throws DriverException
	 * @see org.gdms.data.DataSource#check(int, org.gdms.data.values.Value)
	 */
	public String check(int fieldId, Value value) throws DriverException {
		return internalDataSource.check(fieldId, value);
	}

	/**
	 * @throws DriverException
	 * @throws NonEditableDataSourceException
	 * @throws SemanticException
	 * @see org.gdms.data.DataSource#commit()
	 */
	public void commit() throws DriverException, NonEditableDataSourceException {
		internalDataSource.commit();
	}

	/**
	 * @param rowId
	 * @throws DriverException
	 * @throws SemanticException
	 * @see org.gdms.data.DataSource#deleteRow(long)
	 */
	public void deleteRow(long rowId) throws DriverException {
		internalDataSource.deleteRow(rowId);
	}

	/**
	 * @return
	 * @see org.gdms.data.DataSource#getDataSourceFactory()
	 */
	public DataSourceFactory getDataSourceFactory() {
		return internalDataSource.getDataSourceFactory();
	}

	/**
	 * @return
	 * @throws DriverException
	 * @throws SemanticException
	 * @see org.gdms.data.DataSource#getMetadata()
	 */
	public Metadata getMetadata() throws DriverException {
		return internalDataSource.getMetadata();
	}

	/**
	 * @return
	 * @see org.gdms.data.DataSource#getDispatchingMode()
	 */
	public int getDispatchingMode() {
		return internalDataSource.getDispatchingMode();
	}

	/**
	 * @return
	 * @see org.gdms.data.DataSource#getDriver()
	 */
	public ReadOnlyDriver getDriver() {
		return internalDataSource.getDriver();
	}

	/**
	 * @param rowIndex
	 * @param fieldId
	 * @return
	 * @throws DriverException
	 * @throws SemanticException
	 * @see org.gdms.driver.ReadAccess#getFieldValue(long, int)
	 */
	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		return internalDataSource.getFieldValue(rowIndex, fieldId);
	}

	/**
	 * @return
	 * @see org.gdms.data.DataSource#getName()
	 */
	public String getName() {
		return internalDataSource.getName();
	}

	/**
	 * @return
	 * @throws DriverException
	 * @see org.gdms.driver.ReadAccess#getRowCount()
	 */
	public long getRowCount() throws DriverException {
		return internalDataSource.getRowCount();
	}

	/**
	 * @param dimension
	 * @return
	 * @throws DriverException
	 * @throws SemanticException
	 * @throws IncompatibleTypesException
	 * @see org.gdms.driver.ReadAccess#getScope(int)
	 */
	public Number[] getScope(int dimension) throws DriverException,
			IncompatibleTypesException {
		return internalDataSource.getScope(dimension);
	}

	/**
	 * @return
	 * @throws IOException
	 * @see org.gdms.data.DataSource#getWhereFilter()
	 */
	@SuppressWarnings("deprecation")
	public long[] getWhereFilter() throws IOException {
		return internalDataSource.getWhereFilter();
	}

	/**
	 * @throws DriverException
	 * @throws SemanticException
	 * @see org.gdms.data.DataSource#insertEmptyRow()
	 */
	public void insertEmptyRow() throws DriverException {
		internalDataSource.insertEmptyRow();
	}

	/**
	 * @param index
	 * @throws DriverException
	 * @see org.gdms.data.DataSource#insertEmptyRowAt(long)
	 */
	public void insertEmptyRowAt(long index) throws DriverException {
		internalDataSource.insertEmptyRowAt(index);
	}

	/**
	 * @param values
	 * @throws DriverException
	 * @throws SemanticException
	 * @see org.gdms.data.DataSource#insertFilledRow(org.gdms.data.values.Value[])
	 */
	public void insertFilledRow(Value[] values) throws DriverException {
		internalDataSource.insertFilledRow(values);
	}

	/**
	 * @param index
	 * @param values
	 * @throws DriverException
	 * @see org.gdms.data.DataSource#insertFilledRowAt(long,
	 *      org.gdms.data.values.Value[])
	 */
	public void insertFilledRowAt(long index, Value[] values)
			throws DriverException {
		internalDataSource.insertFilledRowAt(index, values);
	}

	/**
	 * @return
	 * @see org.gdms.data.DataSource#isEditable()
	 */
	public boolean isEditable() {
		return internalDataSource.isEditable();
	}

	/**
	 * @return
	 * @see org.gdms.data.DataSource#isModified()
	 */
	public boolean isModified() {
		return internalDataSource.isModified();
	}

	/**
	 * @return
	 * @see org.gdms.data.DataSource#isOpen()
	 */
	public boolean isOpen() {
		return internalDataSource.isOpen();
	}

	/**
	 * @throws DriverException
	 * @see org.gdms.data.DataSource#open()
	 */
	public void open() throws DriverException {
		internalDataSource.open();
	}

	/**
	 * @throws DriverException
	 * @see org.gdms.data.DataSource#redo()
	 */
	public void redo() throws DriverException {
		internalDataSource.redo();
	}

	/**
	 * @param listener
	 * @see org.gdms.data.DataSource#removeEditionListener(org.gdms.data.edition.EditionListener)
	 */
	public void removeEditionListener(EditionListener listener) {
		internalDataSource.removeEditionListener(listener);
	}

	/**
	 * @param index
	 * @throws DriverException
	 * @throws SemanticException
	 * @see org.gdms.data.DataSource#removeField(int)
	 */
	public void removeField(int index) throws DriverException {
		internalDataSource.removeField(index);
	}

	/**
	 * @param listener
	 * @see org.gdms.data.DataSource#removeMetadataEditionListener(org.gdms.data.edition.MetadataEditionListener)
	 */
	public void removeMetadataEditionListener(MetadataEditionListener listener) {
		internalDataSource.removeMetadataEditionListener(listener);
	}

	/**
	 * @param ds
	 * @throws IllegalStateException
	 * @throws DriverException
	 * @throws SemanticException
	 * @see org.gdms.data.DataSource#saveData(org.gdms.data.DataSource)
	 */
	public void saveData(DataSource ds) throws IllegalStateException,
			DriverException {
		internalDataSource.saveData(ds);
	}

	/**
	 * @param dsf
	 * @see org.gdms.data.DataSource#setDataSourceFactory(org.gdms.data.DataSourceFactory)
	 */
	public void setDataSourceFactory(DataSourceFactory dsf) {
		internalDataSource.setDataSourceFactory(dsf);
	}

	/**
	 * @param dispatchingMode
	 * @see org.gdms.data.DataSource#setDispatchingMode(int)
	 */
	public void setDispatchingMode(int dispatchingMode) {
		internalDataSource.setDispatchingMode(dispatchingMode);
	}

	/**
	 * @param index
	 * @param name
	 * @throws DriverException
	 * @see org.gdms.data.DataSource#setFieldName(int, java.lang.String)
	 */
	public void setFieldName(int index, String name) throws DriverException {
		if (internalDataSource.getFieldIndexByName(name) == -1) {
			internalDataSource.setFieldName(index, name);
		} else {
			try {
				throw new Exception("The field " + name + " already exists.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param row
	 * @param fieldId
	 * @param value
	 * @throws DriverException
	 * @throws SemanticException
	 * @see org.gdms.data.DataSource#setFieldValue(long, int,
	 *      org.gdms.data.values.Value)
	 */
	public void setFieldValue(long row, int fieldId, Value value)
			throws DriverException {
		internalDataSource.setFieldValue(row, fieldId, value);
	}

	/**
	 * @throws DriverException
	 * @see org.gdms.data.DataSource#undo()
	 */
	public void undo() throws DriverException {
		internalDataSource.undo();
	}

	/**
	 * @throws DriverException
	 * @see org.gdms.data.DataSource#queryIndex(java.lang.String, IndexQuery)
	 */
	public Iterator<Integer> queryIndex(IndexQuery indexQuery)
			throws DriverException {
		return internalDataSource.queryIndex(indexQuery);
	}

	/**
	 * @see org.gdms.data.DataSource#getCommiter()
	 */
	public Commiter getCommiter() {
		return internalDataSource.getCommiter();
	}

	public void printStack() {
		System.out.println("<" + this.getClass().getName() + ">");
		getDataSource().printStack();
		System.out.println("</" + this.getClass().getName() + ">");
	}

	public String[] getReferencedSources() {
		return internalDataSource.getReferencedSources();
	}

	public Source getSource() {
		return internalDataSource.getSource();
	}

	public void addDataSourceListener(DataSourceListener listener) {
		internalDataSource.addDataSourceListener(listener);
	}

	public void removeDataSourceListener(DataSourceListener listener) {
		internalDataSource.removeDataSourceListener(listener);
	}

	public void syncWithSource() throws DriverException {
		internalDataSource.syncWithSource();
	}

}
