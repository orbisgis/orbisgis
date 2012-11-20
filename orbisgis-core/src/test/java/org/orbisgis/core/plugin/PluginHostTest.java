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
package org.orbisgis.core.plugin;

import java.io.File;
import org.gdms.sql.function.FunctionManager;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.orbisgis.core.AbstractTest;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.plugin.gdms.Activator;
import org.orbisgis.core.plugin.gdms.DummyScalarFunction;

/**
 * Unit test of plugin-system
 * @author Nicolas Fortin
 */
public class PluginHostTest extends AbstractTest {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        registerDataManager();
    }
    
    @Test
    public void installGDMSFunctionBundle() throws Exception {
        DataManager dataManager = getDataManager();
        assertNotNull(dataManager);
        FunctionManager manager = dataManager.getDataSourceFactory().getFunctionManager();        
        PluginHost host = new PluginHost(new File("target"+File.separator+"plugins"));
        host.start();
        
        Activator gdmsPlugin = new Activator();
        gdmsPlugin.start(host.getHostBundleContext());
        // test if function exists
        assertTrue(manager.contains(DummyScalarFunction.class));
        
        host.getHostBundleContext().getBundle().stop();
        //end plugin host
        host.dispose();
        
        //test if the function has been successfully removed
        assertFalse(getDataManager().getDataSourceFactory().getFunctionManager().contains(DummyScalarFunction.class));
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }
    
    
    
}
