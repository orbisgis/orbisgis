/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend;

import java.io.FileInputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import net.opengis.se._2_0.StyleType;
import org.apache.log4j.*;
import org.apache.log4j.varia.LevelRangeFilter;
import org.junit.After;
import org.junit.Before;
import org.orbisgis.core.log.FailErrorManager;
import org.orbisgis.core.renderer.se.Style;

public abstract class AnalyzerTest {

        protected FailErrorManager failErrorManager;
        private Appender consoleAppender;
        
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
                Logger.getRootLogger().addAppender(failErrorManager);
                consoleAppender = initConsoleLogger();
        }
        
        @After
        public void tearDown() {
            Logger.getRootLogger().removeAppender(failErrorManager);
            Logger.getRootLogger().removeAppender(consoleAppender);
        }
        
    /**
     * Console output to info level min
     */
    private Appender initConsoleLogger() {
        Logger root = Logger.getRootLogger();
        ConsoleAppender appender = new ConsoleAppender(
        new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN));
        root.addAppender(appender);
        return appender;
    }
}
