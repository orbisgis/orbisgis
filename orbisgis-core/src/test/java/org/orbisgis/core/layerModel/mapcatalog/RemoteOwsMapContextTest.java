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
package org.orbisgis.core.layerModel.mapcatalog;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.orbisgis.core.layerModel.OwsMapContext;

/**
 *
 * @author Nicolas Fortin
 */
public class RemoteOwsMapContextTest {
        
        /**
         * Test of getMapContext method, of class RemoteOwsMapContext.
         * @throws Exception 
         */
        @Test
        public void testGetMapContext() throws Exception {
                // Unused URL
                URL apiUrl = new URL("http://poulpe.heig-vd.ch/scapc2/serviceapi/index.php");
                ConnectionProperties cParams = new ConnectionProperties(apiUrl);
                
                RemoteMapContext rMapContext = new RemoteOwsMapContext(cParams);
                rMapContext.setId(0);
                rMapContext.getDescription().addTitle(Locale.getDefault(), "test save");
                rMapContext.getDescription().addAbstract(Locale.getDefault(), "save an existing project...");
                
                
                // Read the xml file in test resources
                FileInputStream inFile = new FileInputStream("src/test/resources/layerModel/mapcatalog/contextContent.xml");
                OwsMapContext mapContext = new OwsMapContext();
                mapContext.read(rMapContext.extractMapContent(inFile, "utf-8"));
                // Read the xml file in test resources
                FileInputStream owsIn = new FileInputStream("src/test/resources/layerModel/mapcatalog/owsContext.xml");
                OwsMapContext mapContextRef = new OwsMapContext();
                mapContextRef.read(owsIn);
        }
}
