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
package org.gdms.sql.function;

import org.gdms.data.types.Type;

/**
 * Specifies the type, description and validation of a sql function
 * 
 * @author Fernando Gonzalez Cortes
 * 
 */
public class Argument {

	public static final int TYPE_WHOLE_NUMBER = Type.BYTE | Type.SHORT
			| Type.INT | Type.LONG;
	public static final int TYPE_NUMERIC = TYPE_WHOLE_NUMBER | Type.FLOAT
			| Type.DOUBLE;
	public static final int TYPE_ALL = TYPE_NUMERIC | Type.BINARY
			| Type.BOOLEAN | Type.DATE | Type.GEOMETRY | Type.RASTER
			| Type.STRING | Type.TIME | Type.TIMESTAMP;
	public static final Argument BINARY = new Argument(Type.BINARY);
	public static final Argument BOOLEAN = new Argument(Type.BOOLEAN);
	public static final Argument BYTE = new Argument(Type.BYTE);
	public static final Argument DATE = new Argument(Type.DATE);
	public static final Argument DOUBLE = new Argument(Type.DOUBLE);
	public static final Argument FLOAT = new Argument(Type.FLOAT);
	public static final Argument GEOMETRY = new Argument(Type.GEOMETRY);
	public static final Argument INT = new Argument(Type.INT);
	public static final Argument LONG = new Argument(Type.LONG);
	public static final Argument RASTER = new Argument(Type.RASTER);
	public static final Argument SHORT = new Argument(Type.SHORT);
	public static final Argument STRING = new Argument(Type.STRING);
	public static final Argument TIME = new Argument(Type.TIME);
	public static final Argument TIMESTAMP = new Argument(Type.TIMESTAMP);
	public static final Argument WHOLE_NUMBER = new Argument(
			TYPE_WHOLE_NUMBER);
	public static final Argument NUMERIC = new Argument(TYPE_NUMERIC);
	public static final Argument ANY = new Argument(TYPE_ALL);

	private int typeCode;
	private String description;
	private ArgumentValidator argValidator;

	public Argument(int typeCode) {
		this(typeCode, getValidation(typeCode));
	}

	private static String getValidation(int typeCode) {
		if ((typeCode & TYPE_WHOLE_NUMBER) > 0) {
			return "Whole numeric parameter";
		} else if ((typeCode & TYPE_NUMERIC) > 0) {
			return "Numeric parameter";
		} else if (typeCode == Type.STRING) {
			return "String parameter";
		} else if (typeCode == Type.DATE) {
			return "Date parameter";
		} else if (typeCode == Type.TIME) {
			return "Time parameter";
		} else if (typeCode == Type.TIMESTAMP) {
			return "Timestamp parameter";
		} else if (typeCode == Type.GEOMETRY) {
			return "Geometry parameter";
		} else if (typeCode == Type.RASTER) {
			return "Raster parameter";
		} else if (typeCode == Type.BOOLEAN) {
			return "Boolean parameter";
		} else if (typeCode == Type.BINARY) {
			return "Binary parameter";
		} else {
			throw new IllegalArgumentException("Unknown type code: " + typeCode);
		}
	}

	public Argument(int typeCode, ArgumentValidator argumentValidator) {
		this(typeCode, getValidation(typeCode), argumentValidator);
	}

	public Argument(int typeCode, String description) {
		this(typeCode, description, null);
	}

	public Argument(int typeCode, String description,
			ArgumentValidator argValidator) {
		this.typeCode = typeCode;
		this.description = description;
		this.argValidator = argValidator;
	}

	public boolean isValid(Type type) {
		if ((type.getTypeCode() & this.typeCode) == 0) {
			return false;
		} else {
			if (argValidator != null) {
				return argValidator.isValid(type);
			} else {
				return true;
			}
		}
	}

	public String getDescription() {
		return description;
	}
}
