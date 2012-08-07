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
package org.orbisgis.core.ui.views.geocognition;

import org.junit.Before;
import org.junit.Test;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Ignore;
import org.orbisgis.core.ApplicationInfo;
import org.orbisgis.core.OrbisGISApplicationInfo;
import org.orbisgis.core.OrbisgisUIServices;
import org.orbisgis.core.Services;
import org.orbisgis.core.errorManager.DefaultErrorManager;
import org.orbisgis.core.errorManager.ErrorManager;
import org.orbisgis.core.geocognition.DefaultGeocognition;
import org.orbisgis.core.geocognition.GeocognitionElement;
import org.orbisgis.core.geocognition.GeocognitionFilter;
import org.orbisgis.core.geocognition.mapContext.GeocognitionMapContextFactory;
import org.orbisgis.core.geocognition.symbology.GeocognitionLegendFactory;
import org.orbisgis.core.geocognition.symbology.GeocognitionSymbolFactory;
import org.orbisgis.core.ui.TestWorkspace;
import org.orbisgis.core.ui.plugins.views.geocognition.GeocognitionView;
import org.orbisgis.core.workspace.Workspace;

import static org.junit.Assert.*;

public class GeocognitionTest {

        private static final String STARTUP_GEOCOGNITION_XML = "startup.geocognition.xml";
        private DefaultGeocognition gc;

        @Before
        public void setUp() throws Exception {
                Services.registerService(ErrorManager.class, "",
                        new DefaultErrorManager());
                TestWorkspace workspace = new TestWorkspace();
                workspace.setWorkspaceFolder("target");
                Services.registerService(Workspace.class, "", workspace);
                Services.registerService(ApplicationInfo.class,
                                "Gets information about the application: "
                                + "name, version, etc.",
                                new OrbisGISApplicationInfo());
                OrbisgisUIServices.installServices();

                gc = new DefaultGeocognition();
                gc.addElementFactory(new GeocognitionSymbolFactory());
                gc.addElementFactory(new GeocognitionLegendFactory());
                gc.addElementFactory(new GeocognitionMapContextFactory());
        }

        @Ignore
        @Test
        public void testLoadAndCheckInitialGeocognition() throws Exception {
                InputStream geocognitionStream = GeocognitionElement.class.getResourceAsStream(GeocognitionTest.STARTUP_GEOCOGNITION_XML);
                gc.read(geocognitionStream);
                GeocognitionElement[] elems = gc.getElements(new GeocognitionFilter() {

                        @Override
                        public boolean accept(GeocognitionElement element) {
                                return true;
                        }
                });

                for (GeocognitionElement elem : elems) {
                        assertNotNull(elem.getObject());
                }
        }

        @Test
        public void testLoadInitialMap() throws Exception {
                InputStream geocognitionStream = GeocognitionElement.class.getResourceAsStream(GeocognitionTest.STARTUP_GEOCOGNITION_XML);
                gc.read(geocognitionStream);
                assertNotNull(gc.getGeocognitionElement(GeocognitionView.FIRST_MAP));
        }

        private String getContent(File file) throws FileNotFoundException,
                IOException {
                FileInputStream fis = new FileInputStream(file);
                DataInputStream dis = new DataInputStream(fis);
                byte[] buffer = new byte[dis.available()];
                dis.readFully(buffer);
                String content = new String(buffer);
                return content;
        }
}
