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
