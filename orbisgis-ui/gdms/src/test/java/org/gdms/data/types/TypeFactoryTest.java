package org.gdms.data.types;

import org.gdms.TestBase;
import org.junit.Test;
import static org.junit.Assert.*;

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
}
