 /*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.gdms.drivers;

import org.junit.Before;
import org.gdms.driver.FileDriver;
import java.io.File;
import org.gdms.TestBase;
import org.gdms.driver.FileDriverRegister;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.shapefile.ShapefileDriver;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for the FileDriverRegister.
 * @author alexis
 */


public class FileDriverRegisterTest extends TestBase {
        
        @Before
        @Override
        public void setUp() throws Exception {
                super.setUp();
                setWritingTests(true);
        }
        
        @Test
        public void testAddNull(){
                //Our source is a single, simple, ShapeFile
                File shape = new File(TestBase.internalData + "landcover2000.shp");
                FileDriverRegister fdr = new FileDriverRegister();
                try{
                        fdr.addFile(shape, null);
                        assertTrue(false);
                } catch (DriverLoadException e){
                        assertTrue(true);
                }
                
        }
        
        @Test
        public void testAdd() throws Exception{
                File shape = new File(TestBase.internalData + "landcover2000.shp");
                FileDriver d = new ShapefileDriver();
                FileDriverRegister fdr = new FileDriverRegister();
                fdr.addFile(shape, d);
                assertTrue(fdr.contains(shape));
                assertTrue(fdr.getDriver(shape)==d);
        }        
        
        @Test
        public void testAddTwice() throws Exception{
                File shape = new File(TestBase.internalData + "landcover2000.shp");
                FileDriver d = new ShapefileDriver();
                FileDriverRegister fdr = new FileDriverRegister();
                fdr.addFile(shape, d);
                assertTrue(fdr.contains(shape));
                assertTrue(fdr.getDriver(shape)==d);
                FileDriver d2 = new ShapefileDriver();
                fdr.addFile(shape, d2);
                assertTrue(fdr.contains(shape));
                assertTrue(fdr.getDriver(shape)==d2);
                assertFalse(fdr.getDriver(shape)==d);
        }
        
        @Test
        public void testRemove() throws Exception{
                File shape = new File(TestBase.internalData + "landcover2000.shp");
                FileDriver d = new ShapefileDriver();
                FileDriverRegister fdr = new FileDriverRegister();
                fdr.addFile(shape, d);
                fdr.removeFile(shape);
                assertFalse(fdr.contains(shape));
        }
}
