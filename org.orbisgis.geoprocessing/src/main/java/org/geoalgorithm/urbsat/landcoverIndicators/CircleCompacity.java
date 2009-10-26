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
package org.geoalgorithm.urbsat.landcoverIndicators;

import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.spatial.geometry.properties.AbstractSpatialPropertyFunction;

import com.vividsolutions.jts.geom.Geometry;

public class CircleCompacity extends AbstractSpatialPropertyFunction {
	private final static double DPI = 2 * Math.PI;

	public Value evaluateResult(final Value[] args) throws FunctionException {
		final Geometry geomBuild = args[0].getAsGeometry();
		final double sBuild = geomBuild.getArea();
		final double pBuild = geomBuild.getLength();
		// final double ratioBuild = sBuild / pBuild;

		final double correspondingCircleRadius = Math.sqrt(sBuild / Math.PI);
		// final double sCircle = sBuild;
		final double pCircle = DPI * correspondingCircleRadius;
		// final double ratioCircle = sCircle / pCircle;

		// return ValueFactory.createValue(ratioCircle / ratioBuild);
		return ValueFactory.createValue(pBuild / pCircle);
	}

	public String getDescription() {
		return "Calculate the compacity of each building's geometry compared "
				+ "to the circle (the one that as the area of the building)";
	}

	public String getName() {
		return "CircleCompacity";
	}

	public Type getType(Type[] argsTypes) {
		return TypeFactory.createType(Type.DOUBLE);
	}

	public boolean isAggregate() {
		return false;
	}

	public String getSqlOrder() {
		return "select CircleCompacity(the_geom) from myBuildingsTable;";
	}

}