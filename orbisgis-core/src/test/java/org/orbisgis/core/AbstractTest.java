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
package org.orbisgis.core;

import org.orbisgis.core.log.FailErrorManager;
import java.io.File;
import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.source.SourceManager;
import org.junit.After;
import org.junit.Before;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.map.export.DefaultMapExportManager;
import org.orbisgis.core.map.export.MapExportManager;
import org.orbisgis.core.map.export.RectanglesScale;
import org.orbisgis.core.map.export.SingleLineScale;

public abstract class AbstractTest {

        protected FailErrorManager failErrorManager;
        private Appender consoleAppender;
        
        @Before
        public void setUp() throws Exception {
                failErrorManager = new FailErrorManager();
                consoleAppender = initConsoleLogger();
                Logger.getRootLogger().addAppender(failErrorManager);
                installExportServices();
        }
        
        @After
        public void tearDown() throws Exception {
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
        
	private void installExportServices() {
		DefaultMapExportManager mem = new DefaultMapExportManager();
		Services.registerService(MapExportManager.class,
				"Manages the export of MapContexts to different formats.", mem);
		mem.registerScale(SingleLineScale.class);
		mem.registerScale(RectanglesScale.class);
	}
        
        public static void registerDataManager(DataSourceFactory dsf) {
                // Installation of the service
                Services.registerService(
                        DataManager.class,
                        "Access to the sources, to its properties (indexes, etc.) and its contents, either raster or vectorial",
                        new DefaultDataManager(dsf));
        }

        public static void registerDataManager() {
                DataSourceFactory dsf = new DataSourceFactory("target/tempsGdms", "target/tempsGdms");
                registerDataManager(dsf);
        }

        protected SourceManager getsSourceManager() {
                return getDataManager().getSourceManager();
        }

        protected DataManager getDataManager() {
                return Services.getService(DataManager.class);
        }
        protected DataSource getDataSourceFromPath(String path) throws LayerException {
		String name = getDataManager().getDataSourceFactory().getSourceManager().nameAndRegister(new File(path));
		try {
			return getDataManager().getDataSourceFactory().getDataSource(name);
		} catch (DriverLoadException e) {
			throw new LayerException(e);
		} catch (NoSuchTableException e) {
			throw new LayerException(e);
		} catch (DataSourceCreationException e) {
			throw new LayerException(e);
		}
        }
        
}
