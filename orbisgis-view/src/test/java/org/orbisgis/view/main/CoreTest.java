/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *
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
 * info _at_ orbisgis.org
 */
package org.orbisgis.view.main;

import bibliothek.gui.Dockable;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.orbisgis.view.docking.DummyViewPanel;
import org.orbisgis.view.geocatalog.Catalog;
/**
 * Unit Test of org.orbisgis.view.main.Core
 */
public class CoreTest extends TestCase {
    Core instance;
    public CoreTest(String testName) {
        super(testName);
        
    }   

    /**
     * Test of startup method, of class Core.
     */
    @Before@Override
    public void setUp() {
        System.out.println("startup");
        instance = new Core();
        instance.startup();
    }
    /**
     * Test the propagation of DataSource content change to the GeoCatalog List
     */
    public void testGeoCatalogLinkWithDataSourceManager() {
        Catalog geoCatalog = instance.getGeoCatalog();
        //Retrieve the number of DataSource shown in the list
        int nbsource = geoCatalog.getSourceList().getModel().getSize();
    }
    /**
     * Test propagation of docking parameters modifications
     */
    public void testDockingParameterChange() {
        String newTitle = "new dummy name";
        //Create the instance of the panel
        DummyViewPanel dummyPanel = new DummyViewPanel();
        
        //Show the panel has a new docking item
        instance.getDockManager().show(dummyPanel, instance.getDockManager().getScreen(), null);
        //Retrieve the DockingFrame dock instance for the dummy instance
        Dockable dockedDummy = instance.getDockManager().getDockable(dummyPanel);
        
        //Test if the original title is shown
        assertTrue(dockedDummy.getTitleText().equals(DummyViewPanel.OLD_TITLE));
        
        //Change the docking title from the panel
        dummyPanel.setTitle(newTitle);
        
        //Test if the new title is shown on the DockingFrames
        assertTrue(dockedDummy.getTitleText().equals(newTitle));
        
    }
    /**
     * Test of shutdown method, of class Core.
     */
    @After@Override
    public void tearDown() {
        System.out.println("dispose");
        instance.dispose();
    }
}
