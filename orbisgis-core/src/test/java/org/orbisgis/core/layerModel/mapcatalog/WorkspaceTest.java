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
import java.io.FileReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Nicolas Fortin
 */
public class WorkspaceTest {
        /**
         * Test of parseXML method, of class Workspace.
         * @throws Exception 
         */
        @Test
        public void testParseXML() throws Exception {
                // Unused URL
                URL apiUrl = new URL("http://poulpe.heig-vd.ch/scapc2/serviceapi/index.php");
                ConnectionProperties cParams = new ConnectionProperties(apiUrl);
                // Expected result
                List<RemoteMapContext> expectedContext = new ArrayList<RemoteMapContext>();
                RemoteMapContext first = new RemoteOwsMapContext(cParams);
                first.setId(0);
                first.getDescription().addTitle(Locale.getDefault(), "test save");
                first.getDescription().addAbstract(Locale.getDefault(), "save an existing project...");
                expectedContext.add(first);
                RemoteMapContext second = new RemoteOwsMapContext(cParams);
                second.setId(5);
                second.getDescription().addTitle(Locale.getDefault(), "2 Layers,"
                        + " Elections fédérales 2007, Conseil National");
                second.getDescription().addAbstract(Locale.getDefault(),
                        "Les élections fédérales suisses de 2007 ont eu lieu"
                        + " le 21 octobre 2007. Elles ont permis le"
                        + " renouvellement des 200 membres qui composent"
                        + " le Conseil national (CN) et des 43 à 46 membres"
                        + " du Conseil des États (CE), élus le 24 octobre"
                        + " 2003, pour la 48e législature de quatre ans (2007-2011).");
                second.setDate((new SimpleDateFormat("yyyy-MM-dd")).parse("2012-09-20"));
                expectedContext.add(second);

                // Read the xml file in test resources
                FileReader inFile = new FileReader("src/test/resources/layerModel/mapcatalog/context.xml");
                BufferedReader in = new BufferedReader(inFile);
                XMLInputFactory factory = XMLInputFactory.newInstance();
                
                // Parse Data
                XMLStreamReader parser;
                parser = factory.createXMLStreamReader(in);
                Workspace mapCatalog =
                        new Workspace(cParams,
                        "default");
                List<RemoteMapContext> context = new ArrayList<RemoteMapContext>();
                mapCatalog.parseXML(context, parser);                        
                parser.close();
                
                assertEquals(context.size(), 2);
                assertTrue(context.containsAll(expectedContext));
        }
}
