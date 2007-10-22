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
package org.gdms.sqlprocessor.indexes;

import java.util.HashMap;

import org.gdms.data.edition.PhysicalDirection;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

/**
 * Disk-based hashmap. The keys are Value objects and the values are
 * PhysicalDirections that contain those values. As the values
 * (PhysicalDirections) have constant length we store as many PhysicalDirections
 * as BUCKET_SIZE for the first bucket together at the beginning of the file.
 * After those ones we store the ones for the second bucket and so on. If the
 * there more PhysicalDirections than BUCKET_SIZE in a bucket we store a special
 * element pointing to the next set of BUCKET_SIZE PhysicalDirections.
 *
 * @author Fernando Gonzalez Cortes
 *
 */
public class DiskBasedHashMap {

	public static final int BUCKET_SIZE = 64;

	public static final int PHYSICAL_DIR = 0;

	public static final int REDIRECTION = 1;

	public HashMap<Value, PhysicalDirection> map = new HashMap<Value, PhysicalDirection>();

	private int fieldId;

	public DiskBasedHashMap(int fieldId) {
		this.fieldId = fieldId;
	}

	public void create(int numValues) {

	}

	public void insert(Value v, PhysicalDirection dir) {
		map.put(v, dir);
	}

	public PhysicalDirection[] getPosibleDirections(Value v) {
		return new PhysicalDirection[] { map.get(v) };
	}

	public boolean remove(PhysicalDirection dir) throws DriverException {
		return map.remove(dir.getFieldValue(fieldId)) != null;
	}

	public void setValue(Value oldValue, Value newValue, PhysicalDirection dir)
			throws DriverException {
		if (!remove(dir)) {
			throw new RuntimeException("PhisicalDirection is not at the index");
		}

		insert(newValue, dir);
	}

}
