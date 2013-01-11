/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.driver;

import java.lang.reflect.Field;

import org.gdms.data.types.Constraint;
import org.gdms.data.types.DefaultTypeDefinition;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.types.TypeFactory;

public abstract class GDMSModelDriver extends AbstractDataSet {

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
                } catch (NoSuchFieldException e) {
                        throw new IllegalStateException("Cannot read GDMS types", e);
                } catch (IllegalAccessException e) {
                        throw new IllegalStateException("Cannot read GDMS types", e);
                }
        }

        private int[] getConstraints() throws IllegalAccessException {
                Class<Constraint> constClass = Constraint.class;
                Field[] constCodes = constClass.getFields();
                int[] codes = new int[constCodes.length];
                int codesIndex = 0;
                for (int i = 0; i < constCodes.length; i++) {
                        String constCodeName = constCodes[i].getName();
                        if ((!constCodeName.startsWith("CONSTRAINT_TYPE"))
                                && (!constCodeName.equals("ALL") && (!constCodeName.equals("GEOMETRY_TYPE")))) {
                                codes[codesIndex] = constCodes[i].getInt(null);
                                codesIndex++;
                        }
                }
                int[] ret = new int[codesIndex];
                System.arraycopy(codes, 0, ret, 0, codesIndex);

                return ret;
        }
}
