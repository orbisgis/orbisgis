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
package org.orbisgis.core.context.main;

import java.io.IOException;
import org.apache.log4j.*;
import org.apache.log4j.varia.LevelRangeFilter;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.DataSourceFinalizationException;
import org.gdms.data.SQLDataSourceFactory;
import org.orbisgis.core.context.SourceContext.SourceContext;
import org.orbisgis.core.workspace.CoreWorkspace;
/**
 * @class MainContext
 * @brief The larger surrounding part of OrbisGis base 
 * This is the entry class for the OrbisGis context,
 * It contains instance needed to manage an OrbisGis project.
 */


public class MainContext {
    private static final Logger LOGGER = Logger.getLogger(MainContext.class);
    private DataSourceFactory dataSourceFactory;
    private CoreWorkspace coreWorkspace;
    private SourceContext sourceContext;
    /**
     * Constructor of the workspace
     */
    public MainContext() {
        //Redirect root logging to console
        initConsoleLogger();      
        coreWorkspace = new CoreWorkspace();
        initFileLogger(coreWorkspace);
        dataSourceFactory = new SQLDataSourceFactory(coreWorkspace.getSourceFolder(), coreWorkspace.getTempFolder(), coreWorkspace.getPluginFolder());
        sourceContext = new SourceContext(dataSourceFactory.getSourceManager());
    }
    
    /**
     * Free resources
     */
    public void dispose() {
        sourceContext.dispose();
        try {
            dataSourceFactory.freeResources();
        } catch (DataSourceFinalizationException ex) {
            LOGGER.error("Unable to free gdms resources, continue..", ex);
        }
    }

    /**
     * Return the core path information.
     * @return CoreWorkspace instance
     */
    public CoreWorkspace getCoreWorkspace() {
        return coreWorkspace;
    }

    /**
     * Return the SourceContext.
     * @return SourceContext instance
     */
    public SourceContext getSourceContext() {
        return sourceContext;
    }

    /**
     * 
     * @return The data source factory instance
     */
    public DataSourceFactory getDataSourceFactory() {
        return dataSourceFactory;
    }
    
    /**
     * Console output to info level min
     */
    private void initConsoleLogger() {
        Logger root = Logger.getRootLogger();
        ConsoleAppender appender = new ConsoleAppender(
        new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN));
        LevelRangeFilter filter = new LevelRangeFilter();
        filter.setLevelMax(Level.FATAL);
        filter.setLevelMin(Level.INFO);
        appender.addFilter(filter);
        root.addAppender(appender);
    }
    /**
     * Initiate the logging system, called by MainContext constructor
     */
    private void initFileLogger(CoreWorkspace workspace) {
        //Init the file logging feature
        PatternLayout l = new PatternLayout("%5p [%t] (%F:%L) - %m%n");
        RollingFileAppender fa;
        try {
            fa = new RollingFileAppender(l,workspace.getLogPath());
            fa.setMaxFileSize("256KB");
            LevelRangeFilter filter = new LevelRangeFilter();
            filter.setLevelMax(Level.FATAL);
            filter.setLevelMin(Level.INFO);
            fa.addFilter(filter);
            Logger.getRootLogger().addAppender(fa);
        } catch (IOException e) {
                System.err.println("Init logger failed!");
                e.printStackTrace(System.err);
        }
    }
}
