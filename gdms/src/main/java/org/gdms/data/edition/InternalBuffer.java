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
package org.gdms.data.edition;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;

public interface InternalBuffer {

	/**
	 * Inserts a row in the buffer and obtains a PhysicalDirection to access it
	 * in the future
	 *
	 * @param index
	 * @param newRow
	 * @return
	 */
	public PhysicalDirection insertRow(ValueCollection pk, Value[] newRow);

	/**
	 * Sets the specified field in the specified direction to the new value
	 *
	 * @param dir
	 * @param fieldId
	 * @param value
	 */
	public void setFieldValue(int row, int fieldId, Value value);

	/**
	 * Gets the value of a cell
	 *
	 * @param row
	 * @param fieldId
	 * @return
	 */
	public Value getFieldValue(int row, int fieldId);

	/**
	 * Notifies the internal buffer that a new field has been added to the
	 * DataSource
	 */
	public void addField();

	/**
	 * Notifies the internal buffer that the specified field has been removed
	 *
	 * @param index
	 */
	public Value[] removeField(int index);

	/**
	 * Restores a previously deleted field
	 *
	 * @param fieldIndex
	 * @param values
	 */
	public void restoreField(int fieldIndex, Value[] values);
}
