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
package org.gdms.sql.function.spatial.predicats;

import java.util.ArrayList;
import java.util.Iterator;

import org.gdms.data.DataSource;
import org.gdms.data.edition.PhysicalDirection;
import org.gdms.data.indexes.SpatialIndexQuery;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.spatial.GeometryValue;
import org.gdms.sql.function.ComplexFunction;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

public class Contains implements ComplexFunction {

	public Function cloneFunction() {
		return new Contains();
	}

	public Value evaluate(Value[] args) throws FunctionException {
		GeometryValue gv = (GeometryValue) args[0];
		GeometryValue gv1 = (GeometryValue) args[1];
		boolean result = gv.getGeom().contains(gv1.getGeom());

		return ValueFactory.createValue(result);
	}

	public String getName() {
		return "Contains";
	}

	public int getType(int[] types) {

		return Type.BOOLEAN;
	}

	public boolean isAggregate() {
		return false;
	}

	public Iterator<PhysicalDirection> filter(Value[] args,
			String[] fieldNames, DataSource tableToFilter,
			ArrayList<Integer> argsFromTableToIndex) throws DriverException {
		if ((args[0] == null) && (args[1] == null)) {
			return null;
		}
		int argFromTableToIndex = argsFromTableToIndex.get(0);
		int knownValue = (argFromTableToIndex + 1) % 2;
		GeometryValue value = (GeometryValue) args[knownValue];
		SpatialIndexQuery query = new SpatialIndexQuery(value.getGeom()
				.getEnvelopeInternal(), fieldNames[argFromTableToIndex]);
		return tableToFilter.queryIndex(query);
	}

	public String getDescription() {
		
		return "Return true is the geometry A is contained into the geometry B";
	}
}
