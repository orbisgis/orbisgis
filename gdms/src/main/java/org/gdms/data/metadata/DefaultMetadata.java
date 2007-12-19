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
package org.gdms.data.metadata;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.gdms.data.types.Constraint;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.driver.DriverException;

/**
 * Class that implements the Metadata interface with the contents specified in
 * the constructor and setter methods
 *
 * @author Fernando Gonzalez Cortes
 */
public class DefaultMetadata implements Metadata {

	private List<Type> fieldsTypes;

	private List<String> fieldsNames;

	/**
	 * Creates a DefaultMetadata instance with no fields
	 */
	public DefaultMetadata() {
		this.fieldsTypes = new LinkedList<Type>();
		this.fieldsNames = new LinkedList<String>();
	}

	/**
	 * Creates a DefaultMetadata instance with the specified field names and
	 * field types
	 *
	 * @param fieldsTypes
	 * @param fieldsNames
	 */
	public DefaultMetadata(Type[] fieldsTypes, String[] fieldsNames) {
		this.fieldsTypes = new LinkedList<Type>(Arrays.asList(fieldsTypes));
		this.fieldsNames = new LinkedList<String>(Arrays.asList(fieldsNames));
	}

	/**
	 * Creates a DefaultMetadata instance with the same contents as the metadata
	 * instance specified as a parameter
	 *
	 * @param originalMetadata
	 * @throws DriverException
	 *             If there is some exception reading the metadata from the
	 *             parameter
	 */
	public DefaultMetadata(final Metadata originalMetadata)
			throws DriverException {
		this();
		final int fc = originalMetadata.getFieldCount();

		for (int i = 0; i < fc; i++) {
			fieldsTypes.add(originalMetadata.getFieldType(i));
			fieldsNames.add(originalMetadata.getFieldName(i));
		}
	}

	public int getFieldCount() {
		return fieldsTypes.size();
	}

	public Type getFieldType(int fieldId) {
		return fieldsTypes.get(fieldId);
	}

	public String getFieldName(int fieldId) {
		return fieldsNames.get(fieldId);
	}

	/**
	 * Adds a field to the instance. This field will be taken into account by
	 * the getFieldXXX methods that implement the Metadata interface
	 *
	 * @param fieldName
	 * @param typeCode
	 * @throws InvalidTypeException
	 *             If the specified type code is not a valid type code
	 */
	public void addField(final String fieldName, final int typeCode) {
		fieldsNames.add(fieldName);
		fieldsTypes.add(TypeFactory.createType(typeCode));
	}

	/**
	 * Adds a field to the instance. This field will be taken into account by
	 * the getFieldXXX methods that implement the Metadata interface
	 *
	 * @param fieldName
	 * @param typeCode
	 * @throws InvalidTypeException
	 *             If the specified type code is not a valid type code or the
	 *             specified constraints are not valid for the given type
	 */
	public void addField(final String fieldName, final int typeCode,
			final Constraint[] constraints) throws InvalidTypeException {
		fieldsNames.add(fieldName);
		fieldsTypes.add(TypeFactory.createType(typeCode, constraints));
	}

	/**
	 * Adds a field to the instance. This field will be taken into account by
	 * the getFieldXXX methods that implement the Metadata interface. This
	 * method gives the type a name to be displayed to the user
	 *
	 * @param fieldName
	 * @param typeCode
	 * @throws InvalidTypeException
	 *             If the specified type code is not a valid type code
	 */
	public void addField(final String fieldName, final int typeCode,
			final String typeName) throws InvalidTypeException {
		fieldsNames.add(fieldName);
		fieldsTypes.add(TypeFactory.createType(typeCode, typeName));
	}

	/**
	 * Adds a field to the instance. This field will be taken into account by
	 * the getFieldXXX methods that implement the Metadata interface. This
	 * method gives the type a name to be displayed to the user
	 *
	 * @param fieldName
	 * @param typeCode
	 * @throws InvalidTypeException
	 *             If the specified type code is not a valid type code or the
	 *             specified constraints are not valid for the given type
	 */
	public void addField(final String fieldName, final int typeCode,
			final String typeName, final Constraint[] constraints)
			throws InvalidTypeException {
		fieldsNames.add(fieldName);
		fieldsTypes
				.add(TypeFactory.createType(typeCode, typeName, constraints));
	}

	/**
	 * Inserts a field into the specified position. This field will be taken
	 * into account by the getFieldXXX methods that implement the Metadata
	 * interface.
	 *
	 * @param index
	 * @param fieldName
	 * @param typeCode
	 * @throws InvalidTypeException
	 *             If the specified type code is not a valid type code
	 */
	public void addField(int index, String fieldName, int typeCode)
			throws InvalidTypeException {
		fieldsNames.add(index, fieldName);
		fieldsTypes.add(index, TypeFactory.createType(typeCode));
	}

	/**
	 * Inserts a field into the specified position. This field will be taken
	 * into account by the getFieldXXX methods that implement the Metadata
	 * interface.
	 *
	 * @param index
	 * @param fieldName
	 * @param typeCode
	 * @throws InvalidTypeException
	 *             If the specified type code is not a valid type code or the
	 *             specified constraints are not valid for the given type
	 */
	public void addField(int index, String fieldName, int typeCode,
			Constraint[] constraints) throws InvalidTypeException {
		fieldsNames.add(index, fieldName);
		fieldsTypes.add(index, TypeFactory
				.createType(typeCode, "", constraints));
	}
}