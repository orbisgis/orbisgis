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
package org.gdms.sql.function;

import java.util.HashMap;

import org.gdms.sql.function.alphanumeric.BooleanFunction;
import org.gdms.sql.function.alphanumeric.ConcatenateFunction;
import org.gdms.sql.function.alphanumeric.Count;
import org.gdms.sql.function.alphanumeric.DateFunction;
import org.gdms.sql.function.alphanumeric.IntFunction;
import org.gdms.sql.function.alphanumeric.LengthFunction;
import org.gdms.sql.function.alphanumeric.Max;
import org.gdms.sql.function.alphanumeric.Sum;
import org.gdms.sql.function.spatial.convert.Boundary;
import org.gdms.sql.function.spatial.convert.Centroid;
import org.gdms.sql.function.spatial.convert.Envelope;
import org.gdms.sql.function.spatial.geometryProperties.Area;
import org.gdms.sql.function.spatial.geometryProperties.Dimension;
import org.gdms.sql.function.spatial.geometryProperties.GeometryN;
import org.gdms.sql.function.spatial.geometryProperties.GeometryType;
import org.gdms.sql.function.spatial.geometryProperties.GetZValue;
import org.gdms.sql.function.spatial.geometryProperties.IsEmpty;
import org.gdms.sql.function.spatial.geometryProperties.IsSimple;
import org.gdms.sql.function.spatial.geometryProperties.Length;
import org.gdms.sql.function.spatial.geometryProperties.NumPoints;
import org.gdms.sql.function.spatial.io.AsWKT;
import org.gdms.sql.function.spatial.io.GeomFromText;
import org.gdms.sql.function.spatial.operators.Buffer;
import org.gdms.sql.function.spatial.operators.Intersection;
import org.gdms.sql.function.spatial.operators.GeomUnion;
import org.gdms.sql.function.spatial.predicats.Contains;
import org.gdms.sql.function.spatial.predicats.Equals;
import org.gdms.sql.function.spatial.predicats.Intersects;

/**
 * DOCUMENT ME!
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class FunctionManager {
	private static HashMap<String, Function> nameFunction = new HashMap<String, Function>();
	static {
		addFunction(new ConcatenateFunction());
		addFunction(new DateFunction());
		addFunction(new BooleanFunction());
		addFunction(new Count());
		addFunction(new Sum());
		addFunction(new LengthFunction());
		addFunction(new Max());
		addFunction(new Buffer());
		addFunction(new Intersects());
		addFunction(new Contains());
		addFunction(new Intersection());
		addFunction(new GeomUnion());
		addFunction(new Envelope());
		addFunction(new GeomFromText());
		addFunction(new AsWKT());
		addFunction(new Area());
		addFunction(new Length());
		addFunction(new NumPoints());
		addFunction(new Dimension());
		addFunction(new GeometryType());
		addFunction(new IsEmpty());
		addFunction(new IsSimple());
		addFunction(new Boundary());
		addFunction(new GeometryN());
		addFunction(new Equals());
		addFunction(new IntFunction());
		addFunction(new GetZValue());
		addFunction(new Centroid());
	}

	/**
	 * Add a new function to the SQL engine
	 *
	 * @param function
	 *            function
	 *
	 * @throws RuntimeException
	 *             
	 */
	public static void addFunction(Function function) {
		if (nameFunction.get(function.getName()) != null) {
			throw new RuntimeException("Function " + function.getName()
					+ " already exists");
		}

		nameFunction.put(function.getName(), function);
	}

	/**
	 * Obtiene la funcion de nombre name
	 *
	 * @param name
	 *            nombre de la funcion que se quiere obtener
	 *
	 * @return funci�n o null si no hay ninguna funci�n que devuelva dicho
	 *         nombre
	 */
	public static Function getFunction(String name) {
		Function func = nameFunction.get(name);

		if (func == null) {
			throw new IllegalArgumentException("Function " + name
					+ " does not exists");
		} else {
			Function ret = func.cloneFunction();
			if (ret == null) {
				throw new RuntimeException("Bad clone method for " + name);
			} else {
				return ret;
			}
		}
	}

}
