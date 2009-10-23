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

/**
 * @author bocher
 */

package org.geoalgorithm.urbsat.landcoverIndicators;

import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

public class Chaillou implements Function {
	private static final double HEIGHT1 = 15;
	private static final double HEIGHT2 = 30;
	private static final double DENSITY1 = 0.15;
	private static final double DENSITY2 = 0.3;

	public Value evaluate(Value[] args) throws FunctionException {
		if (args[0].isNull() || args[1].isNull()) {
			return ValueFactory.createNullValue();
		} else {
			final double buildDensity = args[0].getAsDouble();
			final double buildHeigthAverage = args[1].getAsDouble();
			int classLevel;
			if (buildHeigthAverage < HEIGHT1) {
				if (buildDensity < DENSITY1) {
					classLevel = 1;
				} else if (buildDensity < DENSITY2) {
					classLevel = 2;
				} else {
					classLevel = 3;
				}
			} else if (buildHeigthAverage < HEIGHT2) {
				if (buildDensity < DENSITY1) {
					classLevel = 4;
				} else if (buildDensity < DENSITY2) {
					classLevel = 5;
				} else {
					classLevel = 6;
				}
			} else {
				if (buildDensity < DENSITY1) {
					classLevel = 7;
				} else if (buildDensity < DENSITY2) {
					classLevel = 8;
				} else {
					classLevel = 9;
				}
			}
			return ValueFactory.createValue(classLevel);
		}
	}

	public String getName() {
		return "CHAILLOU";
	}

	public boolean isAggregate() {
		return true;
	}

	public String getDescription() {
		return "Compute the chaillou classification";
	}

	public String getSqlOrder() {
		return "select CHAILLOU(buildDensity, buildHeigthAverage) from myTable";
	}

	public Type getType(Type[] argsTypes) {
		return TypeFactory.createType(Type.INT);
	}

	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments(Argument.NUMERIC,
				Argument.NUMERIC) };
	}

	public Value getAggregateResult() {
		return null;
	}

}