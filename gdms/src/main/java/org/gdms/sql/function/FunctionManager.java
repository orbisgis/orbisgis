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
import org.gdms.sql.function.spatial.convert.Enveloppe;
import org.gdms.sql.function.spatial.geometryProperties.Area;
import org.gdms.sql.function.spatial.geometryProperties.Dimension;
import org.gdms.sql.function.spatial.geometryProperties.GeometryN;
import org.gdms.sql.function.spatial.geometryProperties.GeometryType;
import org.gdms.sql.function.spatial.geometryProperties.IsEmpty;
import org.gdms.sql.function.spatial.geometryProperties.IsSimple;
import org.gdms.sql.function.spatial.geometryProperties.Length;
import org.gdms.sql.function.spatial.geometryProperties.NumGeometries;
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
		addFunction(new Enveloppe());
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
		addFunction(new NumGeometries());
		addFunction(new GeometryN());
		addFunction(new Equals());
		addFunction(new IntFunction());
	}

	/**
	 * A�ade una nueva funci�n al sistema
	 *
	 * @param function
	 *            funci�n
	 *
	 * @throws RuntimeException
	 *             DOCUMENT ME!
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
