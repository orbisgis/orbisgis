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
package org.orbisgis.sif;

/**
 * Interface to implement by user interface that provides methods to access to
 * the interface as if it was a row in a database
 *
 * @author Fernando Gonzalez Cortes
 *
 */
public interface SQLUIPanel extends UIPanel {

	public static final int STRING = 0;

	public static final int INT = 1;

	public static final int DOUBLE = 2;

	/**
	 * Gets the names of the fields that are filled in this user interface
	 *
	 * @return
	 */
	String[] getFieldNames();

	/**
	 * Gets the types of the fields in the same order as the field names
	 *
	 * @return
	 */
	int[] getFieldTypes();

	/**
	 * Expressions in sql that have to be true in order to the input to be
	 * valid. The syntax is the one of the where SQL clause without the 'where'
	 * keyword
	 *
	 * @return
	 */
	String[] getValidationExpressions();

	/**
	 * Gets a error message to show to the user when one of the validation
	 * expressions is not true. Each component of the returned array matches the
	 * same component in the array of expressions
	 *
	 * @return
	 */
	String[] getErrorMessages();

	/**
	 * Gets the values of the user input in the same order as the field names
	 *
	 * @return
	 */
	String[] getValues();

	/**
	 * Sets the value of a component in the user interface
	 *
	 * @param fieldName
	 * @param fieldValue
	 */
	void setValue(String fieldName, String fieldValue);

	/**
	 * Gets an id used for persistence. if it's null, the user interface values
	 * won't be stored at disk
	 *
	 * @return
	 */
	String getId();

	/**
	 * @return True if the dialog have to show the favorites panel or not
	 */
	boolean showFavorites();

}
