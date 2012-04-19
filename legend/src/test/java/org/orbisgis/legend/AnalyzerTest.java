/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend;

import java.io.FileInputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import net.opengis.se._2_0.core.StyleType;
import org.junit.Before;
import org.orbisgis.core.OrbisgisCoreServices;
import org.orbisgis.core.Services;
import org.orbisgis.core.errorManager.ErrorListener;
import org.orbisgis.core.errorManager.ErrorManager;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.core.ui.plugins.views.output.OutputManager;

/**
 *
 * @author alexis
 */
public abstract class AnalyzerTest {

        protected FailErrorManager failErrorManager;

        public Style getStyle(String path) throws Exception {
            JAXBContext jaxbContext = JAXBContext.newInstance(StyleType.class);
            Unmarshaller u = jaxbContext.createUnmarshaller();
            JAXBElement<StyleType> ftsElem = (JAXBElement<StyleType>) u.unmarshal(
                    new FileInputStream(path));
            return new Style(ftsElem, null);
        }

        @Before
        public void setUp() throws Exception {
                failErrorManager = new FailErrorManager();
                Services.registerService(ErrorManager.class, "", failErrorManager);
                Services.registerService(OutputManager.class, "output", new ConsoleOutputManager());

                OrbisgisCoreServices.installServices();
        }

        protected class FailErrorManager implements ErrorManager {

                private boolean ignoreWarnings;
                private boolean ignoreErrors;

                public void setIgnoreWarnings(boolean ignore) {
                        this.ignoreWarnings = ignore;
                }

                public void addErrorListener(ErrorListener listener) {
                }

                public void error(String userMsg) {
                        if (!ignoreErrors) {
                                throw new RuntimeException(userMsg);
                        }
                }

                public void error(String userMsg, Throwable exception) {
                        if (!ignoreErrors) {
                                throw new RuntimeException(userMsg, exception);
                        }
                }

                public void removeErrorListener(ErrorListener listener) {
                }

                public void warning(String userMsg, Throwable exception) {
                        if (!ignoreWarnings) {
                                throw new RuntimeException(userMsg, exception);
                        }
                }

                public void warning(String userMsg) {
                        if (!ignoreWarnings) {
                                throw new RuntimeException(userMsg);
                        }
                }

                public void setIgnoreErrors(boolean b) {
                        this.ignoreErrors = b;
                }
        }
}
