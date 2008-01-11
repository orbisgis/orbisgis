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
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
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
package org.gdms.sql.function;

import java.util.ArrayList;
import java.util.Iterator;

import org.gdms.data.DataSource;
import org.gdms.data.edition.PhysicalDirection;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

public interface ComplexFunction extends Function {

	/**
	 * This method is called by the sql engine to filter the DataSource
	 *
	 * @param args
	 *            the Values provided in the evaluate method. Some of the
	 *            elements of the Value array are set to null which means that
	 *            value cannot be used to filter the table. It's important to
	 *            notice the difference between an args element containing null
	 *            and another one containing NullValue. The first means it
	 *            cannot be used and the second means that the value can be used
	 *            but it is a null value
	 * @param fieldNames
	 *            The names of the fields of the args. If the argument is not a
	 *            direct reference to a field, the corresponding element in this
	 *            array contains a null
	 * @param from
	 *            Table to filter.
	 * @param argsFromTableToIndex
	 *            This array contains the indexes in the arguments array of the
	 *            values that belong to the table to filter. If there is no
	 *            argument belonging to the table to filter this method is not
	 *            called
	 * @return
	 * @throws DriverException
	 */
	public Iterator<PhysicalDirection> filter(Value[] args,
			String[] fieldNames, DataSource from,
			ArrayList<Integer> argsFromTableToIndex) throws DriverException;
}
