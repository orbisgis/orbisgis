/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier
 * SIG" team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
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
 * For more information, please consult: <http://www.orbisgis.org/> or contact
 * directly: info_at_ orbisgis.org
 */
package org.orbisgis.view;

import java.awt.GraphicsEnvironment;
import java.util.List;
import javax.swing.*;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.gdms.data.DataSource;
import org.gdms.source.SourceEvent;
import org.gdms.source.SourceListener;
import org.gdms.source.SourceRemovalEvent;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orbisgis.core.workspace.CoreWorkspace;
import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.view.beanshell.BeanShellFrame;
import org.orbisgis.view.beanshell.BshConsolePanel;
import org.orbisgis.view.docking.DockingPanel;
import org.orbisgis.view.main.Core;

import static org.junit.Assert.assertTrue;

/**
 * This class must be used to run orbisgis core
 * @author ebocher
 */
public class CoreBaseTest {

        public static Core instance;

        /**
         * Test of startup method, of class Core.
         *
         * @throws InterruptedException
         */
        @BeforeClass
        public static void setUp() throws Exception {
                System.out.println("startup");
                if (!GraphicsEnvironment.isHeadless()) {
                        CoreWorkspace coreWorkspace = new CoreWorkspace();
                        coreWorkspace.setWorkspaceFolder("target/workspace/");
                        coreWorkspace.setApplicationFolder("target/app_folder/");
                        instance = new Core(coreWorkspace, true, new NullProgressMonitor());
                        instance.startup(new NullProgressMonitor());
                        SwingUtilities.invokeAndWait(new org.orbisgis.view.CoreBaseTest.DummyThread());
                }
        }

        /**
         * This runnable is just to wait the execution of other runnables
         */
        public static class DummyThread implements Runnable {

                @Override
                public void run() {
                }
        }

        /**
         * Test of shutdown method, of class Core.
         */
        @AfterClass
        public static void tearDown() throws Exception {
                if (!GraphicsEnvironment.isHeadless()) {
                        SwingUtilities.invokeAndWait(new org.orbisgis.view.CoreBaseTest.DummyThread());
                        System.out.println("dispose");
                        instance.dispose();
                        instance = null;
                }
        }
}
