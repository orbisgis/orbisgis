/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. 
 * 
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 * 
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
 * 
 * This file is part of OrbisGIS.
 * 
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 * 
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.view.toc.actions.cui;

import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author Alexis Guéganno
 */
public class SimpleGeometryTypeTest {

        @Test
        public void testValidGeometryTypes(){
                assertTrue(SimpleGeometryType.getSimpleType(
                        TypeFactory.createType(Type.POINT))
                        ==SimpleGeometryType.POINT);
                assertTrue(SimpleGeometryType.getSimpleType(
                        TypeFactory.createType(Type.MULTIPOINT))
                        ==SimpleGeometryType.POINT);
                assertTrue(SimpleGeometryType.getSimpleType(
                        TypeFactory.createType(Type.LINESTRING))
                        ==SimpleGeometryType.LINE);
                assertTrue(SimpleGeometryType.getSimpleType(
                        TypeFactory.createType(Type.MULTILINESTRING))
                        ==SimpleGeometryType.LINE);
                assertTrue(SimpleGeometryType.getSimpleType(
                        TypeFactory.createType(Type.POLYGON))
                        ==SimpleGeometryType.POLYGON);
                assertTrue(SimpleGeometryType.getSimpleType(
                        TypeFactory.createType(Type.MULTIPOLYGON))
                        ==SimpleGeometryType.POLYGON);
                assertTrue(SimpleGeometryType.getSimpleType(
                        TypeFactory.createType(Type.GEOMETRY))
                        ==SimpleGeometryType.ALL);
                assertTrue(SimpleGeometryType.getSimpleType(
                        TypeFactory.createType(Type.GEOMETRYCOLLECTION))
                        ==SimpleGeometryType.ALL);
        }

        @Test
        public void testInvalidTypes(){
                int i;
                try{
                        i = SimpleGeometryType.getSimpleType(TypeFactory.createType(Type.BINARY));
                        fail();
                } catch(IllegalArgumentException iae){
                }
                try{
                        i = SimpleGeometryType.getSimpleType(TypeFactory.createType(Type.BOOLEAN));
                        fail();
                } catch(IllegalArgumentException iae){
                }
                try{
                        i = SimpleGeometryType.getSimpleType(TypeFactory.createType(Type.BYTE));
                        fail();
                } catch(IllegalArgumentException iae){
                }
                try{
                        i = SimpleGeometryType.getSimpleType(TypeFactory.createType(Type.DATE));
                        fail();
                } catch(IllegalArgumentException iae){
                }
                try{
                        i = SimpleGeometryType.getSimpleType(TypeFactory.createType(Type.DOUBLE));
                        fail();
                } catch(IllegalArgumentException iae){
                }
                try{
                        i = SimpleGeometryType.getSimpleType(TypeFactory.createType(Type.FLOAT));
                        fail();
                } catch(IllegalArgumentException iae){
                }
                try{
                        i = SimpleGeometryType.getSimpleType(TypeFactory.createType(Type.INT));
                        fail();
                } catch(IllegalArgumentException iae){
                }
                try{
                        i = SimpleGeometryType.getSimpleType(TypeFactory.createType(Type.LONG));
                        fail();
                } catch(IllegalArgumentException iae){
                }
                try{
                        i = SimpleGeometryType.getSimpleType(TypeFactory.createType(Type.SHORT));
                        fail();
                } catch(IllegalArgumentException iae){
                }
                try{
                        i = SimpleGeometryType.getSimpleType(TypeFactory.createType(Type.STRING));
                        fail();
                } catch(IllegalArgumentException iae){
                }
                try{
                        i = SimpleGeometryType.getSimpleType(TypeFactory.createType(Type.TIMESTAMP));
                        fail();
                } catch(IllegalArgumentException iae){
                }
                try{
                        i = SimpleGeometryType.getSimpleType(TypeFactory.createType(Type.TIME));
                        fail();
                } catch(IllegalArgumentException iae){
                }
                try{
                        i = SimpleGeometryType.getSimpleType(TypeFactory.createType(Type.RASTER));
                        fail();
                } catch(IllegalArgumentException iae){
                }
                try{
                        i = SimpleGeometryType.getSimpleType(TypeFactory.createType(Type.STREAM));
                        fail();
                } catch(IllegalArgumentException iae){
                }
                try{
                        i = SimpleGeometryType.getSimpleType(TypeFactory.createType(Type.COLLECTION));
                        fail();
                } catch(IllegalArgumentException iae){
                }
                assertTrue(true);
        }

}
