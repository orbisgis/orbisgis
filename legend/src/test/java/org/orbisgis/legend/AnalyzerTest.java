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
package org.orbisgis.legend;

import java.io.FileInputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import net.opengis.se._2_0.core.StyleType;
import org.apache.log4j.*;
import org.apache.log4j.varia.LevelRangeFilter;
import org.junit.After;
import org.junit.Before;
import org.orbisgis.core.Services;
import org.orbisgis.core.log.FailErrorManager;
import org.orbisgis.core.renderer.se.Style;

public abstract class AnalyzerTest {
        
        protected FailErrorManager failErrorManager;
        private Appender consoleAppender;
        
        public Style getStyle(String path) throws Exception {
            Unmarshaller u = Services.JAXBCONTEXT.createUnmarshaller();
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
