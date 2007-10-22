/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALES CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALES CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.data;

import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.ConstraintNames;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

public class StatusCheckDecorator extends AbstractDataSourceDecorator {

	public StatusCheckDecorator(DataSource ds) {
		super(ds);
	}

	public void addField(String name, Type driverType) throws DriverException {
		if (isOpen()) {
			getDataSource().addField(name, driverType);
		} else {
			throw new ClosedDataSourceException(
					"The data source must be open to call this method");
		}
	}

	public String check(int fieldId, Value value) throws DriverException {
		if (isOpen()) {
			return getDataSource().check(fieldId, value);
		} else {
			throw new ClosedDataSourceException(
					"The data source must be open to call this method");
		}
	}

	public void commit() throws DriverException, FreeingResourcesException,
			NonEditableDataSourceException {
		if (isOpen()) {
			if (isEditable()) {
				getDataSource().commit();
			} else {
				throw new NonEditableDataSourceException();
			}
		} else {
			throw new ClosedDataSourceException(
					"The data source must be open to call this method");
		}
	}

	public void deleteRow(long rowId) throws DriverException {
		if (isOpen()) {
			getDataSource().deleteRow(rowId);
		} else {
			throw new ClosedDataSourceException(
					"The data source must be open to call this method");
		}
	}

	public Metadata getMetadata() throws DriverException {
		if (isOpen()) {
			return getDataSource().getMetadata();
		} else {
			throw new ClosedDataSourceException(
					"The data source must be open to call this method");
		}
	}

	public int getFieldIndexByName(String fieldName) throws DriverException {
		if (isOpen()) {
			return getDataSource().getFieldIndexByName(fieldName);
		} else {
			throw new ClosedDataSourceException(
					"The data source must be open to call this method");
		}
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		if (isOpen()) {
			return getDataSource().getFieldValue(rowIndex, fieldId);
		} else {
			throw new ClosedDataSourceException(
					"The data source must be open to call this method");
		}
	}

	public long getRowCount() throws DriverException {
		if (isOpen()) {
			return getDataSource().getRowCount();
		} else {
			throw new ClosedDataSourceException(
					"The data source must be open to call this method");
		}
	}

	public Number[] getScope(int dimension) throws DriverException {
		if (isOpen()) {
			return getDataSource().getScope(dimension);
		} else {
			throw new ClosedDataSourceException(
					"The data source must be open to call this method");
		}
	}

	public void insertEmptyRow() throws DriverException {
		if (isOpen()) {
			getDataSource().insertEmptyRow();
		} else {
			throw new ClosedDataSourceException(
					"The data source must be open to call this method");
		}
	}

	public void insertEmptyRowAt(long index) throws DriverException {
		if (isOpen()) {
			getDataSource().insertEmptyRowAt(index);
		} else {
			throw new ClosedDataSourceException(
					"The data source must be open to call this method");
		}
	}

	public void insertFilledRow(Value[] values) throws DriverException {
		if (isOpen()) {
			getDataSource().insertFilledRow(values);
		} else {
			throw new ClosedDataSourceException(
					"The data source must be open to call this method");
		}
	}

	public void insertFilledRowAt(long index, Value[] values)
			throws DriverException {
		if (isOpen()) {
			getDataSource().insertFilledRowAt(index, values);
		} else {
			throw new ClosedDataSourceException(
					"The data source must be open to call this method");
		}
	}

	public boolean isModified() {
		if (isOpen()) {
			return getDataSource().isModified();
		} else {
			throw new ClosedDataSourceException(
					"The data source must be open to call this method");
		}
	}

	public void redo() throws DriverException {
		if (isOpen()) {
			getDataSource().redo();
		} else {
			throw new ClosedDataSourceException(
					"The data source must be open to call this method");
		}
	}

	public void removeField(int index) throws DriverException {
		if (isOpen()) {
			getDataSource().removeField(index);
		} else {
			throw new ClosedDataSourceException(
					"The data source must be open to call this method");
		}
	}

	public void saveData(DataSource ds) throws IllegalStateException,
			DriverException {
		if (isOpen()) {
			throw new IllegalStateException(
					"The data source must be closed to call this method");
		} else {
			getDataSource().saveData(ds);
		}
	}

	public void setFieldName(int index, String name) throws DriverException {
		if (isOpen()) {
			getDataSource().setFieldName(index, name);
		} else {
			throw new ClosedDataSourceException(
					"The data source must be open to call this method");
		}
	}

	public void setFieldValue(long row, int fieldId, Value value)
			throws DriverException {
		if (isOpen()) {
			Type fieldType = getMetadata().getFieldType(fieldId);
			if ((fieldType.getConstraint(ConstraintNames.READONLY) != null)
					|| (fieldType.getConstraint(ConstraintNames.AUTO_INCREMENT) != null)) {
				throw new DriverException(
						"A read only or autoincrement field cannot be modified");
			} else {
				getDataSource().setFieldValue(row, fieldId, value);
			}
		} else {
			throw new ClosedDataSourceException(
					"The data source must be open to call this method");
		}
	}

	public void undo() throws DriverException {
		if (isOpen()) {
			getDataSource().undo();
		} else {
			throw new ClosedDataSourceException(
					"The data source must be open to call this method");
		}
	}
}