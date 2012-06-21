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
package org.gdms.data.types;

import java.lang.reflect.Field;
import org.gdms.TestBase;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author alexis
 */
public class TypeFactoryTest extends TestBase {

        @Test
        public void testIsVectorial() {
                assertTrue(TypeFactory.isVectorial(Type.LINESTRING));
                assertTrue(TypeFactory.isVectorial(Type.POINT));
                assertTrue(TypeFactory.isVectorial(Type.POLYGON));
                assertTrue(TypeFactory.isVectorial(Type.MULTIPOINT));
                assertTrue(TypeFactory.isVectorial(Type.MULTILINESTRING));
                assertTrue(TypeFactory.isVectorial(Type.MULTIPOLYGON));
                assertTrue(TypeFactory.isVectorial(Type.GEOMETRY));
                assertTrue(TypeFactory.isVectorial(Type.GEOMETRYCOLLECTION));
                assertTrue(TypeFactory.isVectorial(Type.NULL));
                assertFalse(TypeFactory.isVectorial(Type.BINARY));

                assertFalse(TypeFactory.isVectorial(Type.BINARY));
                assertFalse(TypeFactory.isVectorial(Type.BOOLEAN));
                assertFalse(TypeFactory.isVectorial(Type.BYTE));
                assertFalse(TypeFactory.isVectorial(Type.DATE));
                assertFalse(TypeFactory.isVectorial(Type.DOUBLE));
                assertFalse(TypeFactory.isVectorial(Type.FLOAT));
                assertFalse(TypeFactory.isVectorial(Type.INT));
                assertFalse(TypeFactory.isVectorial(Type.LONG));
                assertFalse(TypeFactory.isVectorial(Type.SHORT));
                assertFalse(TypeFactory.isVectorial(Type.STRING));
                assertFalse(TypeFactory.isVectorial(Type.TIMESTAMP));
                assertFalse(TypeFactory.isVectorial(Type.TIME));
                assertFalse(TypeFactory.isVectorial(Type.RASTER));
                assertFalse(TypeFactory.isVectorial(Type.COLLECTION));

        }

        /**
         * We check that all the int fields found in Type can be used to
         * generate a human readable description. We assume here that the
         * Type interface only has int fields, and that these fields are all
         * associated to a type code.
         * @throws Exception
         */
        @Test
        public void testHumanReadableType() throws Exception {
                Class typeClass = Class.forName("org.gdms.data.types.Type");
                Field[] fieldArray = typeClass.getDeclaredFields();
                for(Field f : fieldArray){
                        int i = f.getInt(typeClass);
                        Type t = TypeFactory.createType(i);
                        assertTrue(t.getHumanType().equalsIgnoreCase(f.getName()));
                }
                assertTrue(true);
        }
}
