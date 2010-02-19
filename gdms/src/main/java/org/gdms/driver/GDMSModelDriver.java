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
package org.gdms.driver;

import java.lang.reflect.Field;

import org.gdms.data.types.Constraint;
import org.gdms.data.types.DefaultTypeDefinition;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.types.TypeFactory;

public class GDMSModelDriver {

	public TypeDefinition[] getTypesDefinitions() {
		try {
			int[] typeCodes = TypeFactory.getTypes();
			String[] types = new String[typeCodes.length];
			for (int i = 0; i < types.length; i++) {
				types[i] = TypeFactory.getTypeName(typeCodes[i]);
			}
			TypeDefinition[] ret = new TypeDefinition[types.length];
			int[] constraints = getConstraints();
			for (int i = 0; i < ret.length; i++) {
				Field f;
				f = Type.class.getField(types[i].toUpperCase());
				int typeCode = f.getInt(null);
				ret[i] = new DefaultTypeDefinition(types[i], typeCode,
						constraints);
			}

			return ret;
		} catch (SecurityException e) {
			throw new RuntimeException("Cannot read GDMS types", e);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException("Cannot read GDMS types", e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Cannot read GDMS types", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Cannot read GDMS types", e);
		}
	}

	private int[] getConstraints() throws IllegalArgumentException,
			IllegalAccessException {
		Class<Constraint> constClass = Constraint.class;
		Field[] constCodes = constClass.getFields();
		int[] codes = new int[constCodes.length];
		int codesIndex = 0;
		for (int i = 0; i < constCodes.length; i++) {
			if ((!constCodes[i].getName().startsWith("CONSTRAINT_TYPE"))
					&& (!constCodes[i].getName().equals("ALL"))) {
				codes[codesIndex] = constCodes[i].getInt(null);
				codesIndex++;
			}
		}
		int[] ret = new int[codesIndex];
		System.arraycopy(codes, 0, ret, 0, codesIndex);

		return ret;
	}
}
