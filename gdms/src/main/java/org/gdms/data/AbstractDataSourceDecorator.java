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
 * Copyright (C) 2007-2014 IRSTV FR CNRS 2488
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
package org.gdms.data;

import java.util.Iterator;
import org.cts.crs.CoordinateReferenceSystem;
import org.gdms.data.edition.Commiter;
import org.gdms.data.edition.EditionListener;
import org.gdms.data.edition.MetadataEditionListener;
import org.gdms.data.indexes.IndexQuery;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DataSet;
import org.gdms.driver.Driver;
import org.gdms.driver.DriverException;
import org.gdms.source.Source;

/**
 * A base class for any DataSource decorator.
 */
public abstract class AbstractDataSourceDecorator extends AbstractDataSource {

        private DataSource internalDataSource;

        /**
         * Creates a new decorator on the specified DataSource instance.
         *
         * @param internalDataSource a DataSource to decorate
         */
        public AbstractDataSourceDecorator(final DataSource internalDataSource) {
                this.internalDataSource = internalDataSource;
        }

        /**
         * @return the internal DataSource object
         */
        public final DataSource getDataSource() {
                return internalDataSource;
        }

        @Override
        public void addEditionListener(EditionListener listener) {
                internalDataSource.addEditionListener(listener);
        }

        @Override
        public void addField(String name, Type driverType) throws DriverException {
                if (internalDataSource.getFieldIndexByName(name) == -1) {
                        internalDataSource.addField(name, driverType);
                } else {
                        throw new DriverException("The field " + name + " already exists.");
                }
        }

        @Override
        public void addMetadataEditionListener(MetadataEditionListener listener) {
                internalDataSource.addMetadataEditionListener(listener);
        }

        @Override
        public void close() throws DriverException {
                internalDataSource.close();
        }

        @Override
        public boolean canRedo() {
                return internalDataSource.canRedo();
        }

        @Override
        public boolean canUndo() {
                return internalDataSource.canUndo();
        }

        @Override
        public String check(int fieldId, Value value) throws DriverException {
                return internalDataSource.check(fieldId, value);
        }

        @Override
        public void commit() throws DriverException, NonEditableDataSourceException {
                internalDataSource.commit();
        }

        @Override
        public void deleteRow(long rowId) throws DriverException {
                internalDataSource.deleteRow(rowId);
        }

        @Override
        public DataSourceFactory getDataSourceFactory() {
                return internalDataSource.getDataSourceFactory();
        }

        @Override
        public Metadata getMetadata() throws DriverException {
                return internalDataSource.getMetadata();
        }

        @Override
        public int getDispatchingMode() {
                return internalDataSource.getDispatchingMode();
        }

        @Override
        public Driver getDriver() {
                return internalDataSource.getDriver();
        }

        @Override
        public Value getFieldValue(long rowIndex, int fieldId)
                throws DriverException {
                return internalDataSource.getFieldValue(rowIndex, fieldId);
        }

        @Override
        public String getName() {
                return internalDataSource.getName();
        }

        @Override
        public long getRowCount() throws DriverException {
                return internalDataSource.getRowCount();
        }

        @Override
        public Number[] getScope(int dimension) throws DriverException {
                return internalDataSource.getScope(dimension);
        }

        @Override
        public void insertEmptyRow() throws DriverException {
                internalDataSource.insertEmptyRow();
        }

        @Override
        public void insertEmptyRowAt(long index) throws DriverException {
                internalDataSource.insertEmptyRowAt(index);
        }

        @Override
        public void insertFilledRow(Value[] values) throws DriverException {
                internalDataSource.insertFilledRow(values);
        }

        @Override
        public void insertFilledRowAt(long index, Value[] values)
                throws DriverException {
                internalDataSource.insertFilledRowAt(index, values);
        }

        @Override
        public boolean isEditable() {
                return internalDataSource.isEditable();
        }

        @Override
        public boolean isModified() {
                return internalDataSource.isModified();
        }

        @Override
        public boolean isOpen() {
                return internalDataSource.isOpen();
        }

        @Override
        public void open() throws DriverException {
                internalDataSource.open();
        }

        @Override
        public void redo() throws DriverException {
                internalDataSource.redo();
        }

        @Override
        public void removeEditionListener(EditionListener listener) {
                internalDataSource.removeEditionListener(listener);
        }

        @Override
        public void removeField(int index) throws DriverException {
                internalDataSource.removeField(index);
        }

        @Override
        public void removeMetadataEditionListener(MetadataEditionListener listener) {
                internalDataSource.removeMetadataEditionListener(listener);
        }

        @Override
        public void saveData(DataSet ds) throws DriverException {
                internalDataSource.saveData(ds);
        }

        @Override
        public final void setDataSourceFactory(DataSourceFactory dsf) {
                internalDataSource.setDataSourceFactory(dsf);
        }

        @Override
        public void setDispatchingMode(int dispatchingMode) {
                internalDataSource.setDispatchingMode(dispatchingMode);
        }

        @Override
        public void setFieldName(int index, String name) throws DriverException {
                if (internalDataSource.getFieldIndexByName(name) == -1) {
                        internalDataSource.setFieldName(index, name);
                } else {
                        throw new DriverException("The field " + name + " already exists.");
                }
        }

        @Override
        public void setFieldValue(long row, int fieldId, Value value)
                throws DriverException {
                internalDataSource.setFieldValue(row, fieldId, value);
        }

        @Override
        public void undo() throws DriverException {
                internalDataSource.undo();
        }

        @Override
        public Iterator<Integer> queryIndex(IndexQuery indexQuery)
                throws DriverException {
                return internalDataSource.queryIndex(indexQuery);
        }

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
        public CoordinateReferenceSystem getCRS() throws DriverException {
                return internalDataSource.getCRS();
        }
}
