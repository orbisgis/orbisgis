/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY, Adelin PIAU
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
package org.gdms.data;

import java.util.Iterator;

import org.gdms.data.edition.Commiter;
import org.gdms.data.edition.EditionListener;
import org.gdms.data.edition.MetadataEditionListener;
import org.gdms.data.indexes.IndexQuery;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.Driver;
import org.gdms.driver.DataSet;
import org.gdms.source.Source;

/**
 * This is the base class for any DataSourceDecorator.
 *
 */
public class AbstractDataSourceDecorator extends AbstractDataSource {

        private DataSource internalDataSource;

        public AbstractDataSourceDecorator(final DataSource internalDataSource) {
                this.internalDataSource = internalDataSource;
        }

        /**
         * @return the internalDataSource
         */
        public final DataSource getDataSource() {
                return internalDataSource;
        }

        /**
         * @param listener
         * @see org.gdms.data.DataSource#addEditionListener(org.gdms.data.edition.EditionListener)
         */
        @Override
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
        @Override
        public void addField(String name, Type driverType) throws DriverException {
                if (internalDataSource.getFieldIndexByName(name) == -1) {
                        internalDataSource.addField(name, driverType);
                } else {
                        throw new DriverException("The field " + name + " already exists.");
                }
        }

        /**
         * @param listener
         * @see org.gdms.data.DataSource#addMetadataEditionListener(org.gdms.data.edition.MetadataEditionListener)
         */
        @Override
        public void addMetadataEditionListener(MetadataEditionListener listener) {
                internalDataSource.addMetadataEditionListener(listener);
        }

        /**
         * @throws DriverException
         * @throws AlreadyClosedException
         * @see org.gdms.data.DataSource#close()
         */
        @Override
        public void close() throws DriverException {
                internalDataSource.close();
        }

        /**
         * @return
         * @see org.gdms.data.DataSource#canRedo()
         */
        @Override
        public boolean canRedo() {
                return internalDataSource.canRedo();
        }

        /**
         * @return
         * @see org.gdms.data.DataSource#canUndo()
         */
        @Override
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
        @Override
        public String check(int fieldId, Value value) throws DriverException {
                return internalDataSource.check(fieldId, value);
        }

        /**
         * @throws DriverException
         * @throws NonEditableDataSourceException
         * @see org.gdms.data.DataSource#commit()
         */
        @Override
        public void commit() throws DriverException, NonEditableDataSourceException {
                internalDataSource.commit();
        }

        /**
         * @param rowId
         * @throws DriverException
         * @see org.gdms.data.DataSource#deleteRow(long)
         */
        @Override
        public void deleteRow(long rowId) throws DriverException {
                internalDataSource.deleteRow(rowId);
        }

        /**
         * @return
         * @see org.gdms.data.DataSource#getDataSourceFactory()
         */
        @Override
        public DataSourceFactory getDataSourceFactory() {
                return internalDataSource.getDataSourceFactory();
        }

        /**
         * @return
         * @throws DriverException
         * @see org.gdms.data.DataSource#getMetadata()
         */
        @Override
        public Metadata getMetadata() throws DriverException {
                return internalDataSource.getMetadata();
        }

        /**
         * @return
         * @see org.gdms.data.DataSource#getDispatchingMode()
         */
        @Override
        public int getDispatchingMode() {
                return internalDataSource.getDispatchingMode();
        }

        /**
         * @return
         * @see org.gdms.data.DataSource#getDriver()
         */
        @Override
        public Driver getDriver() {
                return internalDataSource.getDriver();
        }

        /**
         * @param rowIndex
         * @param fieldId
         * @return
         * @throws DriverException
         * @see org.gdms.driver.DataSet#getFieldValue(long, int)
         */
        @Override
        public Value getFieldValue(long rowIndex, int fieldId)
                throws DriverException {
                return internalDataSource.getFieldValue(rowIndex, fieldId);
        }

        /**
         * @return
         * @see org.gdms.data.DataSource#getName()
         */
        @Override
        public String getName() {
                return internalDataSource.getName();
        }

        /**
         * @return
         * @throws DriverException
         * @see org.gdms.driver.DataSet#getRowCount()
         */
        @Override
        public long getRowCount() throws DriverException {
                return internalDataSource.getRowCount();
        }

        /**
         * @param dimension
         * @return
         * @throws DriverException
         * @see org.gdms.driver.DataSet#getScope(int)
         */
        @Override
        public Number[] getScope(int dimension) throws DriverException {
                return internalDataSource.getScope(dimension);
        }

        /**
         * @throws DriverException
         * @see org.gdms.data.DataSource#insertEmptyRow()
         */
        @Override
        public void insertEmptyRow() throws DriverException {
                internalDataSource.insertEmptyRow();
        }

        /**
         * @param index
         * @throws DriverException
         * @see org.gdms.data.DataSource#insertEmptyRowAt(long)
         */
        @Override
        public void insertEmptyRowAt(long index) throws DriverException {
                internalDataSource.insertEmptyRowAt(index);
        }

        /**
         * @param values
         * @throws DriverException
         * @see org.gdms.data.DataSource#insertFilledRow(org.gdms.data.values.Value[])
         */
        @Override
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
        @Override
        public void insertFilledRowAt(long index, Value[] values)
                throws DriverException {
                internalDataSource.insertFilledRowAt(index, values);
        }

        /**
         * @return
         * @see org.gdms.data.DataSource#isEditable()
         */
        @Override
        public boolean isEditable() {
                return internalDataSource.isEditable();
        }

        /**
         * @return
         * @see org.gdms.data.DataSource#isModified()
         */
        @Override
        public boolean isModified() {
                return internalDataSource.isModified();
        }

        /**
         * @return
         * @see org.gdms.data.DataSource#isOpen()
         */
        @Override
        public boolean isOpen() {
                return internalDataSource.isOpen();
        }

        /**
         * @throws DriverException
         * @see org.gdms.data.DataSource#open()
         */
        @Override
        public void open() throws DriverException {
                internalDataSource.open();
        }

        /**
         * @throws DriverException
         * @see org.gdms.data.DataSource#redo()
         */
        @Override
        public void redo() throws DriverException {
                internalDataSource.redo();
        }

        /**
         * @param listener
         * @see org.gdms.data.DataSource#removeEditionListener(org.gdms.data.edition.EditionListener)
         */
        @Override
        public void removeEditionListener(EditionListener listener) {
                internalDataSource.removeEditionListener(listener);
        }

        /**
         * @param index
         * @throws DriverException
         * @see org.gdms.data.DataSource#removeField(int)
         */
        @Override
        public void removeField(int index) throws DriverException {
                internalDataSource.removeField(index);
        }

        /**
         * @param listener
         * @see org.gdms.data.DataSource#removeMetadataEditionListener(org.gdms.data.edition.MetadataEditionListener)
         */
        @Override
        public void removeMetadataEditionListener(MetadataEditionListener listener) {
                internalDataSource.removeMetadataEditionListener(listener);
        }

        /**
         * @param ds
         * @throws IllegalStateException
         * @throws DriverException
         * @see org.gdms.data.DataSource#saveData(org.gdms.data.DataSource)
         */
        @Override
        public void saveData(DataSource ds) throws DriverException {
                internalDataSource.saveData(ds);
        }

        /**
         * @param dsf
         * @see org.gdms.data.DataSource#setDataSourceFactory(org.gdms.data.DataSourceFactory)
         */
        @Override
        public final void setDataSourceFactory(DataSourceFactory dsf) {
                internalDataSource.setDataSourceFactory(dsf);
        }

        /**
         * @param dispatchingMode
         * @see org.gdms.data.DataSource#setDispatchingMode(int)
         */
        @Override
        public void setDispatchingMode(int dispatchingMode) {
                internalDataSource.setDispatchingMode(dispatchingMode);
        }

        /**
         * @param index
         * @param name
         * @throws DriverException
         * @see org.gdms.data.DataSource#setFieldName(int, java.lang.String)
         */
        @Override
        public void setFieldName(int index, String name) throws DriverException {
                if (internalDataSource.getFieldIndexByName(name) == -1) {
                        internalDataSource.setFieldName(index, name);
                } else {
                        throw new DriverException("The field " + name + " already exists.");
                }
        }

        /**
         * @param row
         * @param fieldId
         * @param value
         * @throws DriverException
         * @see org.gdms.data.DataSource#setFieldValue(long, int,
         *      org.gdms.data.values.Value)
         */
        @Override
        public void setFieldValue(long row, int fieldId, Value value)
                throws DriverException {
                internalDataSource.setFieldValue(row, fieldId, value);
        }

        /**
         * @throws DriverException
         * @see org.gdms.data.DataSource#undo()
         */
        @Override
        public void undo() throws DriverException {
                internalDataSource.undo();
        }

        /**
         * @param indexQuery
         * @throws DriverException
         * @see org.gdms.data.DataSource#queryIndex(java.lang.String, IndexQuery)
         */
        @Override
        public Iterator<Integer> queryIndex(IndexQuery indexQuery)
                throws DriverException {
                return internalDataSource.queryIndex(indexQuery);
        }

        /**
         * @see org.gdms.data.DataSource#getCommiter()
         */
        @Override
        public Commiter getCommiter() {
                return internalDataSource.getCommiter();
        }

        @Override
        public void printStack() {
                System.out.println("<" + this.getClass().getName() + ">");
                getDataSource().printStack();
                System.out.println("</" + this.getClass().getName() + ">");
        }

        @Override
        public String[] getReferencedSources() {
                return internalDataSource.getReferencedSources();
        }

        @Override
        public Source getSource() {
                return internalDataSource.getSource();
        }

        @Override
        public void addDataSourceListener(DataSourceListener listener) {
                internalDataSource.addDataSourceListener(listener);
        }

        @Override
        public void removeDataSourceListener(DataSourceListener listener) {
                internalDataSource.removeDataSourceListener(listener);
        }

        @Override
        public void syncWithSource() throws DriverException {
                internalDataSource.syncWithSource();
        }

        @Override
        public DataSet getDriverTable() {
                return internalDataSource.getDriverTable();
        }

        @Override
        public String getDriverTableName() {
                return internalDataSource.getDriverTableName();
        }

        @Override
        public int getSRID() throws DriverException {
                return internalDataSource.getSRID();
        }
}
