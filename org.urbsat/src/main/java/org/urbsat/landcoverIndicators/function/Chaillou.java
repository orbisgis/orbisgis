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

package org.urbsat.landcoverIndicators.function;

import org.gdms.data.types.Type;
import org.gdms.data.values.NumericValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionValidator;

/**
 * @author Vladimir Peric
 */
public class Chaillou implements Function {
	public Value evaluate(Value[] args) throws FunctionException {
		FunctionValidator.failIfBadNumberOfArguments(this, args, 2);
		
		final double buildDensity = ((NumericValue) args[0]).doubleValue();
		final double buildHeigthAverage = ((NumericValue) args[1])
				.doubleValue();
		int classLevel = 0;

		if (buildHeigthAverage < 15 && buildDensity < 0.15) {
			classLevel = 1;
		} else if (buildHeigthAverage < 15 && buildDensity > 0.15
				&& buildDensity < 0.3) {
			classLevel = 2;
		} else if (buildHeigthAverage < 15 && buildDensity > 0.3) {
			classLevel = 3;
		} else if (buildHeigthAverage > 15 && buildHeigthAverage < 30
				&& buildDensity < 0.15) {
			classLevel = 4;
		} else if (buildHeigthAverage > 15 && buildHeigthAverage < 30
				&& buildDensity > 0.15 && buildDensity < 0.3) {
			classLevel = 5;
		} else if (buildHeigthAverage > 15 && buildHeigthAverage < 30
				&& buildDensity > 0.3) {
			classLevel = 6;
		} else if (buildHeigthAverage > 30 && buildDensity < 0.15) {
			classLevel = 7;
		} else if (buildHeigthAverage > 30 && buildDensity > 0.15
				&& buildDensity < 0.3) {
			classLevel = 8;
		} else if (buildHeigthAverage > 30 && buildDensity > 0.3) {
			classLevel = 9;
		} else {
		}

		return ValueFactory.createValue(classLevel);
	}

	public String getName() {
		return "Chaillou";
	}

	public boolean isAggregate() {
		return true;
	}

	public Function cloneFunction() {
		return new Chaillou();
	}

	public int getType(int[] types) {
		return Type.INT;
	}

	public String getDescription() {
		return "Compute the chaillou classification";
	}

	public String getSqlOrder() {
		return "select Chaillou(buildDensity,buildHeigthAverage) from myTable";
	}
}