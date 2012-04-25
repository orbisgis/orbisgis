/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer, Maxence LAURENT,
 * computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
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
package org.gdms.data.types;

/**
 * Interface to represent the data type definition. While Type interface
 * describes the concrete type a concrete field in a concrete data source has,
 * this interface describes the data type of a data source type. This is: a
 * name, the list of constraints that the fields can have.
 *
 * @author Fernando Gonzalez Cortes
 */
public interface TypeDefinition {
	/**
	 * Get the name of the data type
	 *
	 * @return the typeName
	 */
	String getTypeName();

	/**
	 * Get the names of the constraint this data type can have
	 *
	 * @return the constraints
	 */
	int[] getValidConstraints();

	/**
	 * Instantiates a new data type that matches this definition. For example,
	 * if this instance describes the String type in dbase, this method can
	 * return a string type with a default length (mandatory constraint)
	 *
	 * @throws InvalidTypeException
	 *             If the type has some mandatory constraints
	 * @return
	 */
	Type createType();

	/**
	 * Instantiates a new data type that matches this definition. For example,
	 * if this instance describes the String type in dbase, this method can
	 * return a string type with a default length (mandatory constraint)
	 *
	 * @param constraints
	 *            Specifies an array of Constraint objects
	 *
	 * @throws InvalidTypeException
	 *             If the constraints are not suitable to this data type
	 *             definition
	 * @return
	 */
	Type createType(Constraint[] constraints);
}