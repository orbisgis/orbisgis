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
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC, scientific researcher, Fernando GONZALEZ
 * CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 **/
package org.gdms.data.edition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gdms.data.AbstractDataSourceDecorator;
import org.gdms.data.DataSource;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.edition.DeleteCommand.DeleteCommandInfo;
import org.gdms.data.indexes.DataSourceIndex;
import org.gdms.data.indexes.DefaultAlphaQuery;
import org.gdms.data.indexes.IndexEditionManager;
import org.gdms.data.indexes.IndexException;
import org.gdms.data.indexes.IndexManager;
import org.gdms.data.indexes.IndexQuery;
import org.gdms.data.indexes.ResultIterator;
import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.Schema;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.DataSet;
import org.gdms.source.CommitListener;
import org.orbisgis.progress.NullProgressMonitor;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import org.gdms.data.indexes.FullIterator;
import org.gdms.data.indexes.IndexQueryException;
import org.gdms.data.types.TypeFactory;
import org.gdms.source.SourceManager;

/**
 * Adds edition capabilities to a DataSource.
 * 
 * @author Fernando Gonzalez Cortes
 * @author Antoine Gourlay
 */
public final class EditionDecorator extends AbstractDataSourceDecorator implements
        CommitListener {

        private List<PhysicalRowAddress> rowsDirections;
        private EditionListenerSupport editionListenerSupport;
        private boolean dirty;
        private boolean undoRedo;
        private List<EditionInfo> editionActions;
        private List<DeleteEditionInfo> deletedPKs;
        private InternalBuffer internalBuffer;
        private MetadataEditionListenerSupport mdels;
        private List<Field> fields;
        private ModifiedMetadata modifiedMetadata;
        private List<String> fieldsToDelete;
        private Commiter commiter;
        private IndexEditionManager indexEditionManager;
        private Envelope cachedScope;
        //The history of commands on this decorator
        private CommandStack cs;
        private boolean initialized;

        /**
         * Creates a new instance of the decorator on the given DataSource.
         * @param internalDataSource a DataSource
         */
        public EditionDecorator(DataSource internalDataSource) {
                super(internalDataSource);
                this.editionListenerSupport = new EditionListenerSupport(this);
                mdels = new MetadataEditionListenerSupport(this);
                this.commiter = getCommiter();
                this.indexEditionManager = new IndexEditionManager(
                        getDataSourceFactory(), this);
        }

        @Override
        public void deleteRow(long rowId) throws DriverException {
                initializeEdition();
                DeleteCommand command = new DeleteCommand((int) rowId, this);
                cs.put(command);
        }

        DeleteCommandInfo doDeleteRow(long rowId) throws DriverException {
                dirty = true;
                deleteInIndex((int) rowId);
                PhysicalRowAddress dir = rowsDirections.remove((int) rowId);
                EditionInfo ei = editionActions.get((int) rowId);
                DeleteEditionInfo dei = null;
                if (ei instanceof OriginalEditionInfo) {
                        dei = new DeleteEditionInfo(((OriginalEditionInfo) ei).getPK());
                        deletedPKs.add(dei);
                }
                editionActions.remove((int) rowId);
                cachedScope = null;

                editionListenerSupport.callDeleteRow(rowId, undoRedo);

                return new DeleteCommand.DeleteCommandInfo(dir, rowId, dei, ei);
        }

        void undoDeleteRow(PhysicalRowAddress dir, long rowId,
                DeleteEditionInfo dei, EditionInfo ei) throws DriverException {
                rowsDirections.add((int) rowId, dir);
                if (dei != null) {
                        deletedPKs.remove(dei);
                }
                editionActions.add((int) rowId, ei);
                insertInIndex(getRow(rowId), (int) rowId);

                cachedScope = null;

                editionListenerSupport.callInsert(rowId, true);
        }

        @Override
        public Number[] getScope(int dimension) throws DriverException {
                if (cachedScope == null) {
                        boolean open = isOpen();
                        if (!open) {
                                open();
                        }
                        for (int i = 0; i < getRowCount(); i++) {
                                Metadata m = getMetadata();
                                for (int j = 0; j < m.getFieldCount(); j++) {
                                        int typeCode = m.getFieldType(j).getTypeCode();
                                        Envelope r = null;
                                        if ((typeCode & Type.GEOMETRY) != 0) {
                                                Value v = getFieldValue(i, j);
                                                if ((v != null) && (!v.isNull())) {
                                                        r = v.getAsGeometry().getEnvelopeInternal();
                                                }
                                        } else if (typeCode == Type.RASTER) {
                                                Value v = getFieldValue(i, j);
                                                if ((v != null) && (!v.isNull())) {
                                                        r = v.getAsRaster().getMetadata().getEnvelope();
                                                }
                                        }
                                        if (r != null) {
                                                if (cachedScope == null) {
                                                        cachedScope = new Envelope(r);
                                                } else {
                                                        cachedScope.expandToInclude(r);
                                                }
                                        }
                                }
                        }
                        if (!open) {
                                close();
                        }

                }

                if (cachedScope == null) {
                        return new Number[]{0, 0};
                } else {
                        if (dimension == DataSet.X) {
                                return new Number[]{cachedScope.getMinX(),
                                                cachedScope.getMaxX()};
                        } else if (dimension == DataSet.Y) {
                                return new Number[]{cachedScope.getMinY(),
                                                cachedScope.getMaxY()};
                        } else {
                                throw new UnsupportedOperationException("Not yet implemented");
                        }
                }
        }

        private void deleteInIndex(int rowIndex) throws DriverException {
                if (indexEditionManager != null) {
                        try {
                                for (DataSourceIndex index : indexEditionManager.getDataSourceIndexes()) {
                                        Metadata metadata = getMetadata();
                                        for (int i = 0; i < metadata.getFieldCount(); i++) {
                                                if (metadata.getFieldName(i).equals(
                                                        index.getFieldName())) {
                                                        Value v = getFieldValue(rowIndex, i);
                                                        index.deleteRow(v, rowIndex);
                                                }
                                        }
                                }
                        } catch (IndexException e) {
                                throw new DriverException(e);
                        }
                }
        }

        @Override
        public void insertFilledRow(Value[] values) throws DriverException {
                for (int i = 0; i < values.length; i++) {
                        if (values[i] == null) {
                                values[i] = ValueFactory.createNullValue();
                        }
                }

                insertFilledRowAt(getRowCount(), values);
        }

        private void insertInIndex(Value[] values, int rowIndex)
                throws DriverException {
                if (indexEditionManager != null) {
                        try {
                                for (DataSourceIndex index : indexEditionManager.getDataSourceIndexes()) {
                                        Metadata metadata = getMetadata();
                                        for (int i = 0; i < metadata.getFieldCount(); i++) {
                                                if (metadata.getFieldName(i).equals(
                                                        index.getFieldName())) {
                                                        index.insertRow(values[i], rowIndex);
                                                }
                                        }
                                }
                        } catch (IndexException e) {
                                throw new DriverException("Cannot update index", e);
                        }
                }
        }

        @Override
        public void insertEmptyRow() throws DriverException {
                insertFilledRow(getEmptyRow());
        }

        private Value[] getEmptyRow() throws DriverException {
                Value[] row = new Value[getFieldCount()];

                for (int i = 0; i < row.length; i++) {
                        row[i] = ValueFactory.createNullValue();
                }

                return row;
        }

        /**
         * Gets the values of the original row
         *
         * @param dir 
         *            address of the row to be retrieved
         *
         * @return Row values
         *
         * @throws DriverException
         *             if the operation fails
         */
        private Value[] getOriginalRow(PhysicalRowAddress dir)
                throws DriverException {
                ArrayList<Value> ret = new ArrayList<Value>();

                for (int i = 0; i < getFields().size(); i++) {
                        int originalIndex = getFields().get(i).getOriginalIndex();
                        if (originalIndex == -1) {
                                ret.add(ValueFactory.createNullValue());
                        } else {
                                ret.add(dir.getFieldValue(originalIndex));
                        }
                }

                return ret.toArray(new Value[ret.size()]);
        }

        @Override
        public void setFieldValue(long row, int fieldId, Value value)
                throws DriverException {
                initializeEdition();
                ModifyCommand command = new ModifyCommand((int) row, this, value,
                        fieldId);
                cs.put(command);
        }

        ModifyCommand.ModifyInfo doSetFieldValue(long row, int fieldId, Value value)
                throws DriverException {
                // convert value
                Value val = value;
                if (val == null) {
                        val = ValueFactory.createNullValue();
                }
                Type fieldType = getMetadata().getFieldType(fieldId);
                if (!val.isNull() && (fieldType.getTypeCode() != val.getType())) {
                        val = val.toType(fieldType.getTypeCode());
                }

                // write check
                checkConstraints(val, fieldId);

                // Do modification
                ModifyCommand.ModifyInfo ret;
                PhysicalRowAddress dir = rowsDirections.get((int) row);
                dirty = true;
                setFieldValueInIndex((int) row, fieldId, getFieldValue(row, fieldId),
                        val);
                if (dir instanceof OriginalRowAddress) {
                        Value[] original = getOriginalRow(dir);
                        Value previousValue = original[fieldId];
                        original[fieldId] = val;
                        PhysicalRowAddress newDirection = internalBuffer.insertRow(dir.getPK(), original);
                        rowsDirections.set((int) row, newDirection);
                        UpdateEditionInfo info = new UpdateEditionInfo(dir.getPK(),
                                rowsDirections.get((int) row));
                        EditionInfo ei = editionActions.set((int) row, info);
                        ret = new ModifyCommand.ModifyInfo((OriginalRowAddress) dir, ei,
                                (InternalBufferRowAddress) newDirection, previousValue, row,
                                fieldId);
                } else {
                        Value previousValue = dir.getFieldValue(fieldId);
                        ((InternalBufferRowAddress) dir).setFieldValue(fieldId, val);
                        /*
                         * We don't modify the EditionInfo because is an insertion that
                         * already points to the internal buffer
                         */
                        ret = new ModifyCommand.ModifyInfo(null, null,
                                (InternalBufferRowAddress) dir, previousValue, row, fieldId);
                }
                cachedScope = null;

                editionListenerSupport.callSetFieldValue(row, fieldId, undoRedo);

                return ret;
        }

        void undoSetFieldValue(OriginalRowAddress previousDir,
                EditionInfo previousInfo, InternalBufferRowAddress dir,
                Value previousValue, int fieldId, long row) throws DriverException {
                if (previousDir != null) {
                        setFieldValueInIndex((int) row, fieldId,
                                getFieldValue(row, fieldId), previousValue);
                        rowsDirections.set((int) row, previousDir);
                        editionActions.set((int) row, previousInfo);
                } else {
                        setFieldValueInIndex((int) row, fieldId,
                                getFieldValue(row, fieldId), previousValue);
                        dir.setFieldValue(fieldId, previousValue);
                }

                cachedScope = null;
                editionListenerSupport.callSetFieldValue(row, fieldId, true);
        }

        private void setFieldValueInIndex(int rowIndex, int fieldId,
                Value oldValue, Value value) throws DriverException {
                if (indexEditionManager != null) {
                        try {
                                for (DataSourceIndex index : indexEditionManager.getDataSourceIndexes()) {
                                        if (index.getFieldName().equals(
                                                getMetadata().getFieldName(fieldId))) {
                                                index.setFieldValue(oldValue, value, rowIndex);
                                        }
                                }
                        } catch (IndexException e) {
                                throw new DriverException(e);
                        }
                }
        }

        @Override
        public void insertEmptyRowAt(long rowIndex) throws DriverException {
                insertFilledRowAt(rowIndex, getEmptyRow());
        }

        @Override
        public void insertFilledRowAt(long rowIndex, Value[] values)
                throws DriverException {
                initializeEdition();
                InsertAtCommand command = new InsertAtCommand((int) rowIndex, this,
                        values);
                cs.put(command);
        }

        void doInsertAt(long rowIndex, Value[] values) throws DriverException {
                // Check value count
                int fc = getFieldCount();
                if (values.length != fc) {
                        throw new IllegalArgumentException(
                                "Wrong number of values. Expected: " + fc);
                }

                // Convert value
                for (int i = 0; i < values.length; i++) {
                        Type type = getMetadata().getFieldType(i);
                        values[i] = castValue(type, values[i]);
                }

                // Check constraints
                for (int i = 0; i < values.length; i++) {
                        // Check uniqueness
                        checkConstraints(values[i], i);
                }

                // Perform modifications
                dirty = true;
                insertInIndex(values, (int) rowIndex);
                PhysicalRowAddress dir = internalBuffer.insertRow(null, values);
                rowsDirections.add((int) rowIndex, dir);
                InsertEditionInfo iei = new InsertEditionInfo(dir);
                editionActions.add((int) rowIndex, iei);
                cachedScope = null;

                editionListenerSupport.callInsert(rowIndex, undoRedo);
        }

        private Value castValue(Type type, Value value) {
                Value newValue = value;
                if (!value.isNull() && type.getTypeCode() != value.getType()
                        && TypeFactory.canBeCastTo(value.getType(), type.getTypeCode())) {
                        newValue = value.toType(type.getTypeCode());
                }
                return newValue;
        }

        private void checkConstraints(Value value, int fieldId)
                throws DriverException {
                String error = check(fieldId, value);
                if (error != null) {
                        throw new DriverException(error);
                }
        }

        void undoInsertAt(long rowIndex) throws DriverException {
                deleteInIndex((int) rowIndex);
                rowsDirections.remove((int) rowIndex);
                editionActions.remove((int) rowIndex);
                cachedScope = null;

                editionListenerSupport.callDeleteRow(rowIndex, true);
        }

        @Override
        public void addEditionListener(EditionListener listener) {
                editionListenerSupport.addEditionListener(listener);
        }

        @Override
        public void removeEditionListener(EditionListener listener) {
                editionListenerSupport.removeEditionListener(listener);
        }

        @Override
        public int getDispatchingMode() {
                return editionListenerSupport.getDispatchingMode();
        }

        @Override
        public void setDispatchingMode(int dispatchingMode) {
                editionListenerSupport.setDispatchingMode(dispatchingMode);
        }

        @Override
        public boolean isModified() {
                return dirty;
        }

        @Override
        public void open() throws DriverException {
                getDataSource().open();
                initialize();

                SourceManager sm = getDataSourceFactory().getSourceManager();
                sm.addCommitListener(this);
        }

        private void initialize() {
                initialized = false;
                dirty = false;
                undoRedo = false;
                fields = null;
                modifiedMetadata = null;
                internalBuffer = new MemoryInternalBuffer(this);
                fieldsToDelete = new ArrayList<String>();
                deletedPKs = new ArrayList<DeleteEditionInfo>();
                editionActions = new ArrayList<EditionInfo>();
                cs = new CommandStack();
        }

        private void initializeEdition() throws DriverException {
                if (!initialized) {
                        long rowCount = getDataSource().getRowCount();

                        // build alpha indexes on unique fields
                        Metadata m = getMetadata();
                        for (int i = 0; i < m.getFieldCount(); i++) {
                                Type type = m.getFieldType(i);
                                if (type.getBooleanConstraint(Constraint.UNIQUE)
                                        || type.getBooleanConstraint(Constraint.PK)) {
                                        IndexManager indexManager = getDataSourceFactory().getIndexManager();
                                        try {
                                                if (!indexManager.isIndexed(getName(), m.getFieldName(i))) {
                                                        indexManager.buildIndex(getName(), m.getFieldName(i),
                                                                new NullProgressMonitor());
                                                }
                                        } catch (NoSuchTableException e) {
                                                throw new DriverException("table not found: "
                                                        + getName(), e);
                                        } catch (IndexException e) {
                                                throw new DriverException(
                                                        "Cannot create index on unique fields", e);
                                        }
                                }
                        }

                        // initialize directions
                        rowsDirections = new ArrayList<PhysicalRowAddress>();
                        for (int i = 0; i < rowCount; i++) {
                                PhysicalRowAddress dir = new OriginalRowAddress(getDataSource(),
                                        i);
                                rowsDirections.add(dir);
                                editionActions.add(new NoEditionInfo(getPK(i), i));
                        }

                        Number[] xScope = getDataSource().getScope(DataSet.X);
                        Number[] yScope = getDataSource().getScope(DataSet.Y);
                        if ((xScope != null) && (yScope != null)) {
                                cachedScope = new Envelope(new Coordinate(xScope[0].doubleValue(), yScope[0].doubleValue()),
                                        new Coordinate(xScope[1].doubleValue(), yScope[1].doubleValue()));
                        } else {
                                cachedScope = null;
                        }

                        initialized = true;
                }
        }

        @Override
        public Value getFieldValue(long rowIndex, int fieldId)
                throws DriverException {
                if (isModified()) {
                        PhysicalRowAddress physicalAddress = rowsDirections.get((int) rowIndex);
                        if (physicalAddress instanceof OriginalRowAddress) {
                                int originalIndex = getFields().get(fieldId).getOriginalIndex();
                                if (originalIndex == -1) {
                                        return ValueFactory.createNullValue();
                                } else {
                                        return physicalAddress.getFieldValue(originalIndex);
                                }
                        } else {
                                return physicalAddress.getFieldValue(fieldId);
                        }
                } else {
                        return getDataSource().getFieldValue(rowIndex, fieldId);
                }
        }

        @Override
        public long getRowCount() throws DriverException {
                if (initialized) {
                        return rowsDirections.size();
                } else {
                        return getDataSource().getRowCount();
                }
        }

        @Override
        public void close() throws DriverException {
                freeResources();
                getDataSource().close();
                indexEditionManager.cancel();

                SourceManager sm = getDataSourceFactory().getSourceManager();
                sm.removeCommitListener(this);
        }

        private void freeResources() {
                internalBuffer = null;
                if (rowsDirections != null) {
                        rowsDirections.clear();
                        editionActions.clear();
                        deletedPKs.clear();
                }
                if (cs != null) {
                        cs.clear();
                }
        }

        @Override
        public void commit() throws DriverException, NonEditableDataSourceException {
                if (!getDriver().isCommitable()) {
                        throw new NonEditableDataSourceException(
                                "The driver has no write capabilities");
                }

                if (commiter != null) {
                        SourceManager dsm = getDataSourceFactory().getSourceManager();
                        dsm.fireIsCommiting(getName(), this);
                        boolean rebuildIndexes = commiter.commit(rowsDirections,
                                getFieldNames(), getSchemaActions(), editionActions,
                                deletedPKs, this);
                        dsm.fireCommitDone(getName());

                        try {
                                indexEditionManager.commit(rebuildIndexes);
                        } catch (DriverException e) {
                                throw new DriverException("Cannot update indexes", e);
                        }
                } else {
                        throw new UnsupportedOperationException("DataSource not editable");
                }
        }

        private List<EditionInfo> getSchemaActions() throws DriverException {
                List<EditionInfo> ret = new ArrayList<EditionInfo>();

                for (int i = 0; i < getFields().size(); i++) {
                        Field f = getFields().get(i);
                        if (f.getOriginalIndex() == -1) {
                                ret.add(new AddFieldInfo(f.getName(), f.getType()));
                        } else {
                                String originalName = getDataSource().getMetadata().getFieldName(f.getOriginalIndex());
                                if (!f.getName().equals(originalName)) {
                                        ret.add(new ChangeFieldNameInfo(originalName, f.getName()));
                                }
                        }
                }

                for (int i = 0; i < fieldsToDelete.size(); i++) {
                        ret.add(new RemoveFieldInfo(fieldsToDelete.get(i)));
                }

                return ret;
        }

        @Override
        public boolean isEditable() {
                if (commiter != null) {
                        return getDataSource().isEditable();
                } else {
                        return false;
                }
        }

        public void startUndoRedoAction() {
                undoRedo = true;
        }

        public void endUndoRedoAction() {
                undoRedo = false;
        }

        private synchronized List<Field> getFields() throws DriverException {
                if (null == fields) {
                        ArrayList<Field> fieldArray = new ArrayList<Field>();
                        Metadata metadata = getDataSource().getMetadata();
                        if (metadata == null) {
                                throw new DriverException("Error initializing metadata; cannot get the metadata of"
                                        + " the inner source.");
                        }
                        final int fc = metadata.getFieldCount();

                        for (int i = 0; i < fc; i++) {
                                fieldArray.add(new Field(i, metadata.getFieldName(i),
                                        getDataSource().getFieldType(i)));
                        }
                        fields = fieldArray;
                }
                return fields;
        }

        /**
         * Special Metadata class that supports Edition of its own schema through the {@link EditionDecorator}.
         */
        public class ModifiedMetadata implements Metadata {

                @Override
                public int getFieldCount() throws DriverException {
                        return getFields().size();
                }

                @Override
                public Type getFieldType(int fieldId) throws DriverException {
                        return getFields().get(fieldId).getType();
                }

                @Override
                public String getFieldName(int fieldId) throws DriverException {
                        return getFields().get(fieldId).getName();
                }

                @Override
                public int getFieldIndex(String fieldName) throws DriverException {
                        for (Field field : getFields()) {
                                if (field.getName().equals(fieldName)) {
                                        return field.getOriginalIndex();
                                }
                        }
                        return -1;
                }

                @Override
                public Schema getSchema() {
                        // TODO: change this - 12/08/2010
                        // an editable source is removed from its schema
                        // not good
                        return null;
                }

                @Override
                public String[] getFieldNames() throws DriverException {
                        final List<Field> fields1 = getFields();
                        ArrayList<String> fieldNames = new ArrayList<String>();
                        for (Field field : fields1) {
                                fieldNames.add(field.getName());
                        }
                        return fieldNames.toArray(new String[fieldNames.size()]);
                }
        }

        @Override
        public Metadata getMetadata() throws DriverException {
                return getModifiedMetadata();
        }

        private Metadata getModifiedMetadata() {
                if (modifiedMetadata == null) {
                        modifiedMetadata = new ModifiedMetadata();
                }
                return modifiedMetadata;
        }

        @Override
        public void addField(String name, Type type) throws DriverException {
                initializeEdition();
                AddFieldCommand command = new AddFieldCommand(this, name, type);
                cs.put(command);
        }

        void doAddField(String name, Type driverType) throws DriverException {
                dirty = true;
                internalBuffer.addField();
                getFields().add(new Field(-1, name, driverType));
                mdels.callAddField(getFields().size() - 1);
        }

        void undoAddField() throws DriverException {
                int fieldCount = getFields().size() - 1;
                internalBuffer.removeField(fieldCount);
                getFields().remove(fieldCount);
                mdels.callRemoveField(fieldCount);
        }

        @Override
        public void removeField(int index) throws DriverException {
                initializeEdition();
                DelFieldCommand command = new DelFieldCommand(this, index);
                cs.put(command);
        }

        DelFieldCommand.DelFieldInfo doRemoveField(int index)
                throws DriverException {
                if (getFields().get(index).getType().isRemovable()) {
                        dirty = true;

                        Field toDelete = getFields().get(index);

                        if (toDelete.getOriginalIndex() != -1) {
                                fieldsToDelete.add(toDelete.getName());
                        }

                        Value[] values = internalBuffer.removeField(index);
                        getFields().remove(index);
                        mdels.callRemoveField(index);

                        try {
                                return new DelFieldCommand.DelFieldInfo(getDataSourceFactory(),
                                        index, toDelete, values);
                        } catch (IOException e) {
                                throw new DriverException(e);
                        }
                } else {
                        throw new DriverException("The field cannot be deleted");
                }
        }

        void undoDeleteField(int fieldIndex, Field field, Value[] values)
                throws DriverException {
                getFields().add(fieldIndex, field);
                fieldsToDelete.remove(field.getName());
                internalBuffer.restoreField(fieldIndex, values);
                mdels.callAddField(fieldIndex);
        }

        @Override
        public void setFieldName(int index, String name) throws DriverException {
                initializeEdition();
                SetFieldNameCommand command = new SetFieldNameCommand(this, index, name);
                cs.put(command);
        }

        void doSetFieldName(int index, String name) throws DriverException {
                dirty = true;
                getFields().get(index).setName(name);
                mdels.callModifyField(index);
        }

        void undoSetFieldName(String previousName, int fieldIndex)
                throws DriverException {
                getFields().get(fieldIndex).setName(previousName);
                mdels.callModifyField(fieldIndex);
        }

        @Override
        public void addMetadataEditionListener(MetadataEditionListener listener) {
                mdels.addEditionListener(listener);
        }

        @Override
        public void removeMetadataEditionListener(MetadataEditionListener listener) {
                mdels.removeEditionListener(listener);
        }

        @Override
        public boolean canRedo() {
                return cs.canRedo();
        }

        @Override
        public boolean canUndo() {
                return cs.canUndo();
        }

        @Override
        public void redo() throws DriverException {
                undoRedo = true;
                if (!cs.canRedo()) {
                        throw new IllegalStateException("There is no action to redo");
                }
                cs.redo();
                undoRedo = false;
        }

        @Override
        public void undo() throws DriverException {
                undoRedo = true;
                if (!cs.canUndo()) {
                        throw new IllegalStateException("There is no action to undo");
                }
                cs.undo();
                undoRedo = false;
        }

        @Override
        public Iterator<Integer> queryIndex(IndexQuery indexQuery)
                throws DriverException {
                if (!isModified()) {
                        return getDataSource().queryIndex(indexQuery);
                } else {
                        try {
                                int[] result = indexEditionManager.query(indexQuery);
                                if (result == null) {
                                        return new FullIterator(this);
                                } else {
                                        return new ResultIterator(result);
                                }
                        } catch (IndexException e) {
                                throw new DriverException("Cannot access modified index", e);
                        } catch (IndexQueryException e) {
                                throw new DriverException("Cannot query the index", e);
                        }
                }
        }

        @Override
        public void commitDone(String name) throws DriverException {
                initialize();
                editionListenerSupport.callSync();
        }

        @Override
        public void syncWithSource() throws DriverException {
                getDataSource().syncWithSource();
                indexEditionManager.cancel();
                freeResources();
                initialize();
                editionListenerSupport.callSync();
        }

        @Override
        public void isCommiting(String name, Object source) throws DriverException {
                if (isModified() && name.equals(getName()) && (source != this)) {
                        throw new DriverException("Cannot commit the source. "
                                + "Another edition already in process");
                }
        }
        
        private boolean checkGeometry(int valueType, int fieldType){
                switch(fieldType){
                        case Type.POINT :
                        case Type.LINESTRING :
                        case Type.POLYGON :
                        case Type.MULTIPOINT :
                        case Type.MULTILINESTRING :
                        case Type.MULTIPOLYGON :
                                return valueType == fieldType || valueType == Type.NULL;
                        case Type.GEOMETRYCOLLECTION :
                        case Type.GEOMETRY :
                                return (valueType & fieldType) != 0;
                        default :
                                return false;
                }
        }

        @Override
        public String check(int fieldId, Value value) throws DriverException {
                // In order to build pk indexes
                initializeEdition();
                // Check special case of auto-increment not-null fields
                Type type = getMetadata().getFieldType(fieldId);
                boolean autoIncrement = type.getBooleanConstraint(Constraint.AUTO_INCREMENT);
                if (autoIncrement && type.getBooleanConstraint(Constraint.NOT_NULL)) {
                        if (value.isNull()) {
                                return null;
                        }
                }
                int fieldType = type.getTypeCode();
                //Test geometry types.
                if(TypeFactory.isVectorial(type.getTypeCode())){
                        int valueType = value.getType();
                        
                        if(!checkGeometry(valueType, fieldType)){
                                return "Can't put a "+TypeFactory.getTypeName(valueType)+" in a "
                                        +TypeFactory.getTypeName(fieldType) +" column.";
                        }
                }
                // Cast value
                Value val = castValue(type, value);
                int broadType = TypeFactory.getBroaderType(fieldType, val.getType());
                if(val.getType() != broadType && val.getType() != Type.NULL && !checkGeometry(val.getType(), fieldType)){
                        return "Can't cast a "+TypeFactory.getTypeName(value.getType())
                                +" to a "+TypeFactory.getTypeName(fieldType);
                }

                // Check constraints
                String fieldName = getMetadata().getFieldName(fieldId);
                String error = type.check(value);
                if (error != null) {
                        return "Value at field " + getFieldName(fieldId) + " is not valid:"
                                + error;
                }

                // Check uniqueness
                if (type.getBooleanConstraint(Constraint.UNIQUE)
                        || type.getBooleanConstraint(Constraint.PK)) {
                        // We assume a geometry field can't have unique constraint
                        IndexQuery iq = new DefaultAlphaQuery(fieldName, value);
                        Iterator<Integer> it = queryIndex(iq);
                        while (it.hasNext()) {
                                if (getFieldValue(it.next(), fieldId).equals(value).getAsBoolean()) {
                                        return fieldName + " column doesn't admit duplicates: "
                                                + value;
                                }
                        }
                }

                return null;
        }
}
