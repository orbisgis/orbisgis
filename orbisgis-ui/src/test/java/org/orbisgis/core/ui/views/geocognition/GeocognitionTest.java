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
