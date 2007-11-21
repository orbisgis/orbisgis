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
package org.gdms.driver.memory;

import java.util.ArrayList;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.DefaultTypeDefinition;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectReadWriteDriver;
import org.gdms.source.SourceManager;

public class ObjectMemoryDriver implements ObjectReadWriteDriver {

	protected ArrayList<ArrayList<Value>> contents = new ArrayList<ArrayList<Value>>();

	private String[] columnsNames;

	private Type[] columnsTypes;

	public static final String DRIVER_NAME = "Memory driver";

	/**
	 * Create a new empty source of data in memory. The source will have as many
	 * columns as specified in the 'columnsNames' parameter. The values in this
	 * array are the names of the columns and the values in the 'columnsTypes'
	 * array are constants in the org.gdms.data.values.Value interface and
	 * specify the type of each column.
	 *
	 * @param types
	 */
	public ObjectMemoryDriver(String[] columnsNames, Type[] columnsTypes) {
		this.columnsNames = columnsNames;
		this.columnsTypes = columnsTypes;
	}

	public ObjectMemoryDriver() {
		this.columnsNames = new String[0];
		this.columnsTypes = new Type[0];
	}

	public ObjectMemoryDriver(final Metadata metadata) throws DriverException {
		this.columnsNames = new String[metadata.getFieldCount()];
		this.columnsTypes = new Type[metadata.getFieldCount()];
		for (int i = 0; i < columnsNames.length; i++) {
			columnsNames[i] = metadata.getFieldName(i);
			columnsTypes[i] = metadata.getFieldType(i);
		}
	}

	public ObjectMemoryDriver(final DataSource dataSource)
			throws DriverException {
		this(dataSource.getMetadata());
		dataSource.open();
		write(dataSource);
		dataSource.cancel();
	}

	public void write(DataSource dataSource) throws DriverException {
		ArrayList<ArrayList<Value>> newContents = new ArrayList<ArrayList<Value>>();
		for (int i = 0; i < dataSource.getRowCount(); i++) {
			Value[] row = dataSource.getRow(i);
			ArrayList<Value> rowArray = new ArrayList<Value>();
			for (int j = 0; j < row.length; j++) {
				rowArray.add(row[j]);
			}
			newContents.add(rowArray);
		}

		contents = newContents;
		columnsNames = dataSource.getFieldNames();
		columnsTypes = new Type[columnsNames.length];
		for (int i = 0; i < columnsTypes.length; i++) {
			columnsTypes[i] = dataSource.getFieldType(i);
		}
	}

	public void start() throws DriverException {

	}

	public void stop() throws DriverException {

	}

	public Metadata getMetadata() throws DriverException {
		return getMetadataObject();
	}

	private DefaultMetadata getMetadataObject() {
		return new DefaultMetadata(columnsTypes, columnsNames);
	}

	public void setDataSourceFactory(DataSourceFactory dsf) {
	}

	public String getName() {
		return DRIVER_NAME;
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		return contents.get((int) rowIndex).get(fieldId);
	}

	public long getRowCount() throws DriverException {
		return contents.size();
	}

	public Number[] getScope(int dimension) throws DriverException {
		return null;
	}

	public boolean isCommitable() {
		return true;
	}

	public TypeDefinition[] getTypesDefinitions() throws DriverException {
		try {
			return new TypeDefinition[] {
					new DefaultTypeDefinition("BINARY", Type.BINARY),
					new DefaultTypeDefinition("BOOLEAN", Type.BOOLEAN),
					new DefaultTypeDefinition("BYTE", Type.BYTE),
					new DefaultTypeDefinition("DATE", Type.DATE),
					new DefaultTypeDefinition("DOUBLE", Type.DOUBLE),
					new DefaultTypeDefinition("FLOAT", Type.FLOAT),
					new DefaultTypeDefinition("INT", Type.INT),
					new DefaultTypeDefinition("LONG", Type.LONG),
					new DefaultTypeDefinition("SHORT", Type.SHORT),
					new DefaultTypeDefinition("STRING", Type.STRING),
					new DefaultTypeDefinition("TIMESTAMP", Type.TIMESTAMP),
					new DefaultTypeDefinition("TIME", Type.TIME),
					new DefaultTypeDefinition("GEOMETRY", Type.GEOMETRY) };
		} catch (InvalidTypeException e) {
			throw new DriverException("Invalid type");
		}
	}

	public void addValues(Value[] values) {
		ArrayList<Value> row = new ArrayList<Value>();
		for (Value value : values) {
			row.add(value);
		}
		contents.add(row);
	}

	public int getType() {
		int type = SourceManager.MEMORY;
		for (int i = 0; i < getMetadataObject().getFieldCount(); i++) {
			Type fieldType = getMetadataObject().getFieldType(i);
			if (fieldType.getTypeCode() == Type.GEOMETRY) {
				type = type | SourceManager.VECTORIAL;
			}
		}
		return type;
	}
}