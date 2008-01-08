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
package org.gdms;

import junit.framework.TestCase;

import org.gdms.data.DataSource;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.ConstraintNames;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.instruction.IncompatibleTypesException;

public class BaseTest extends TestCase {

	/**
	 * Gets the contents of the DataSource
	 *
	 * @param ds
	 * @return
	 * @throws DriverException
	 */
	public Value[][] getDataSourceContents(DataSource ds)
			throws DriverException {
		Value[][] ret = new Value[(int) ds.getRowCount()][ds.getMetadata()
				.getFieldCount()];
		for (int i = 0; i < ret.length; i++) {
			for (int j = 0; j < ret[i].length; j++) {
				ret[i][j] = ds.getFieldValue(i, j);
			}
		}

		return ret;
	}

	/**
	 * Compares the two values for testing purposes. This means that two null
	 * values are always equal though its equals method returns always false
	 *
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static boolean equals(Value v1, Value v2) {
		if (v1.isNull()) {
			return v2.isNull();
		} else {
			try {
				return v1.equals(v2).getAsBoolean();
			} catch (IncompatibleTypesException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Compares the two arrays of values for testing purposes. This means that
	 * two null values are always equal though its equals method returns always
	 * false
	 *
	 * @param row1
	 * @param row2
	 * @return
	 */
	public static boolean equals(Value[] row1, Value[] row2) {
		for (int i = 0; i < row2.length; i++) {
			if (!equals(row1[i], row2[i])) {
				return false;
			}
		}

		return true;
	}

	/**
	 * The same as the equals(Value[] row1, Value[] row2) version but it doesn't
	 * compares the READ_ONLY fields
	 *
	 * @param row1
	 * @param row2
	 * @return
	 * @throws DriverException
	 */
	public static boolean equals(Value[] row1, Value[] row2, Metadata metadata)
			throws DriverException {
		for (int i = 0; i < row2.length; i++) {
			if (metadata.getFieldType(i)
					.getConstraint(ConstraintNames.READONLY) == null) {
				if (!equals(row1[i], row2[i])) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Compares the two arrays of values for testing purposes. This means that
	 * two null values are always equal though its equals method returns always
	 * false
	 *
	 * @param content1
	 * @param content2
	 * @return
	 */
	public static boolean equals(Value[][] content1, Value[][] content2) {
		for (int i = 0; i < content1.length; i++) {
			if (!equals(content1[i], content2[i])) {
				return false;
			}
		}

		return true;
	}

}
