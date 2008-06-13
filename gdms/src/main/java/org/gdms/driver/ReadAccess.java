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
package org.gdms.driver;

import org.gdms.data.values.Value;

/**
 * Interface that defines the read methods in gdms
 *
 * @author Fernando Gonzalez Cortes
 */
public interface ReadAccess {

	public static final int X = 0;

	public static final int Y = 1;

	public static final int Z = 2;

	public static final int TIME = 3;

	/**
	 * Obtiene el valor que se encuentra en la fila y columna indicada
	 *
	 * @param rowIndex
	 *            fila
	 * @param fieldId
	 *            columna
	 *
	 * @return subclase de Value con el valor del origen de datos. Never null
	 *         (use ValueFactory.createNullValue() instead)
	 *
	 * @throws DriverException
	 *             Si se produce un error accediendo al DataSource
	 */
	public abstract Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException;

	/**
	 * Get the number of elements in the source of data
	 *
	 * @return
	 *
	 * @throws DriverException
	 *             If some error happens accessing the data source
	 */
	public abstract long getRowCount() throws DriverException;

	/**
	 * returns the scope of the data source.
	 *
	 * @param dimension
	 *            Currently X, Y, Z or can be anything that a driver
	 *            implementation is waiting for, for example: TIME
	 * @return An array two elements indicating the bounds of the dimension. Can
	 *         return null if the source is not bounded or the bounds are not
	 *         known
	 * @throws DriverException
	 */
	Number[] getScope(int dimension) throws DriverException;
}
