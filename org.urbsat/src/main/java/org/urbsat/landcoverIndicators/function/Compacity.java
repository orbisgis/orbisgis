/*
 * UrbSAT is a set of spatial functionalities to build morphological
 * and aerodynamic urban indicators. It has been developed on
 * top of GDMS and OrbisGIS. UrbSAT is distributed under GPL 3
 * license. It is produced by the geomatic team of the IRSTV Institute
 * <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of UrbSAT.
 *
 * UrbSAT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UrbSAT is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UrbSAT. If not, see <http://www.gnu.org/licenses/>.
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
package org.urbsat.landcoverIndicators.function;

import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.spatial.geometryProperties.AbstractSpatialPropertyFunction;

import com.vividsolutions.jts.geom.Geometry;

public class Compacity extends AbstractSpatialPropertyFunction {
	public Value evaluateResult(final Value[] args) throws FunctionException {
		final Geometry geomBuild = args[0].getAsGeometry();
		final double sBuild = geomBuild.getArea();
		final double pBuild = geomBuild.getLength();
		// final double ratioBuild = sBuild / pBuild;

		final double correspondingCircleRadius = Math.sqrt(sBuild / Math.PI);
		// final double sCircle = sBuild;
		final double pCircle = 2 * Math.PI * correspondingCircleRadius;
		// final double ratioCircle = sCircle / pCircle;

		// return ValueFactory.createValue(ratioCircle / ratioBuild);
		return ValueFactory.createValue(pBuild / pCircle);
	}

	public String getDescription() {
		return "Calculate the compacity of each building's geometry compared to circle.";
	}

	public String getName() {
		return "CIRCLECOMPACITY";
	}

	public Type getType(Type[] argsTypes) {
		return TypeFactory.createType(Type.DOUBLE);
	}

	public boolean isAggregate() {
		return false;
	}

	public String getSqlOrder() {
		return "select CIRCLECOMPACITY(the_geom) from myBuildingsTable;";
	}
}