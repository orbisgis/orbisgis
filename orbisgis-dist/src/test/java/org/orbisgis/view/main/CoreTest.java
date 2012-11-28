/**
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
package org.orbisgis.view.main;

import java.awt.GraphicsEnvironment;
import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orbisgis.core.workspace.CoreWorkspace;
import org.orbisgis.progress.NullProgressMonitor;
        
/**
 * Unit Tests of org.orbisgis.view.main.Core.
 */
public class CoreTest {
    private static Core instance;
    /**
     * Test of startup method, of class Core.
     * @throws InterruptedException
     * @throws InvocationTargetException  
     */
    @BeforeClass
    public static void setUp() throws Exception {
        System.out.println("startup");
        if(!GraphicsEnvironment.isHeadless()) {
            CoreWorkspace coreWorkspace = new CoreWorkspace();
            coreWorkspace.setWorkspaceFolder("target/workspace/");
            instance = new Core(coreWorkspace,true,new NullProgressMonitor());
            instance.startup(new NullProgressMonitor());
            SwingUtilities.invokeAndWait(new DummyThread());
        }
    }    
    
    /**
     * Test adding custom filter factory to the GeoCatalog
     */
    @Test
    public void testBuiltInBundleActivation() throws Exception {
            
    }
    
   /**
    * This runnable is just to wait the execution of other runnables
    */
    private static class DummyThread implements Runnable {
        @Override
        public void run(){
        }
    }
    /**
     * Test of shutdown method, of class Core.
     */
    @AfterClass
    public static void tearDown() throws Exception {
        if(!GraphicsEnvironment.isHeadless()) {
            SwingUtilities.invokeAndWait(new DummyThread());
            System.out.println("dispose");
            instance.dispose();
            instance = null;
        }
    }
}
