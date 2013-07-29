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
package org.orbisgis.core.context.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.varia.LevelRangeFilter;
import org.orbisgis.core.Services;
import org.orbisgis.core.plugin.BundleReference;
import org.orbisgis.core.plugin.BundleTools;
import org.orbisgis.core.plugin.PluginHost;
import org.orbisgis.core.workspace.CoreWorkspace;
import org.orbisgis.sputilities.JDBCUrlParser;
import org.orbisgis.sputilities.SFSUtilities;
import org.osgi.framework.Bundle;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jdbc.DataSourceFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.sql.DataSource;

/**
 * @class MainContext
 * The larger surrounding part of OrbisGis base 
 * This is the entry class for the OrbisGis context,
 * It contains instance needed to manage an OrbisGis project.
 */


public class MainContext {
    private static final Logger LOGGER = Logger.getLogger(MainContext.class);
    private static final I18n I18N = I18nFactory.getI18n(MainContext.class);
    private CoreWorkspace coreWorkspace;
    private boolean debugMode;
    private static String CONSOLE_LOGGER = "ConsoleLogger";
    private DataSource dataSource;
    private PluginHost pluginHost;
    private static final int BUNDLE_STABILITY_TIMEOUT = 3000;
    private static Map<String,String> URI_DRIVER_TO_OSGI_DRIVER = new HashMap<String, String>();
    static {
        URI_DRIVER_TO_OSGI_DRIVER.put("h2","H2 JDBC Driver");
        URI_DRIVER_TO_OSGI_DRIVER.put("postgresql","Postgresql");
    }

    /**
     * Single parameter constructor
     * Take use.home as a default application folder
     * @param debugMode 
     */
    public MainContext(boolean debugMode) {
            this(debugMode,null, true);
    }

    /**
     * @return The plugin host instance or Null if
     */
    public PluginHost getPluginHost() {
        return pluginHost;
    }

    /**
     * Init and start bundle host
     * @param bundleReferences Additional bundles and per-bundle launching instructions.
     */
    public void startBundleHost(BundleReference[] bundleReferences) {
        pluginHost = new PluginHost(new File(getCoreWorkspace().getPluginCache()));
        pluginHost.init();
        // Install built-in bundles
        BundleTools.installBundles(pluginHost.getHostBundleContext(), bundleReferences);
        // Start bundles
        pluginHost.start();
        LOGGER.info(I18N.tr("Waiting for bundle stability"));
        pluginHost.waitForBundlesStableState(BUNDLE_STABILITY_TIMEOUT);
    }

    /**
     * Constructor of the workspace
     * @param debugMode Use the Debug logging on console output
     * @param customWorkspace Do not use a default folders for
     * application initialization
     * @param initLogger if this context handles logging. Set to false to let the calling application
     * configure log4j.
     */
    public MainContext(boolean debugMode, CoreWorkspace customWorkspace, boolean initLogger) {
        this.debugMode = debugMode;
        if(customWorkspace!=null) {
                coreWorkspace = customWorkspace;
        } else {
                coreWorkspace = new CoreWorkspace();
        }
        //Redirect root logging to console
        if (initLogger) {
                initFileLogger(coreWorkspace);
        }
        registerServices();
    }

    /**
     * @param password Empty or contain the database password if not provided in u=URI
     *                 @throws SQLException If the connection to a DataBase cannot be done
     */
    public void initDataBase(String userName, String password) throws SQLException {
        String jdbcConnectionReference = coreWorkspace.getJDBCConnectionReference();
        if(!jdbcConnectionReference.isEmpty()) {
            Properties properties = JDBCUrlParser.parse(jdbcConnectionReference);
            if(!userName.isEmpty()) {
                properties.setProperty(DataSourceFactory.JDBC_USER,userName);
            }
            if(!password.isEmpty()) {
                properties.setProperty(DataSourceFactory.JDBC_PASSWORD, password);
            }
            String driverName = jdbcConnectionReference.split(":")[1];
            // Get OSGi service
            Collection<ServiceReference<DataSourceFactory>> serviceReferences;
            try {
                serviceReferences =
                        pluginHost.getHostBundleContext().getServiceReferences(DataSourceFactory.class,null);
            } catch (InvalidSyntaxException ex) {
                throw new SQLException(String.format("JDBC Driver Service of %s not found",driverName));
            }
            if(serviceReferences==null || serviceReferences.isEmpty()) {
                throw new SQLException("Could not find any database driver");
            }
            // Find the specific driver ex: h2 or postgis
            ServiceReference<DataSourceFactory> dbDriverReference=null;
            for(ServiceReference<DataSourceFactory> serviceReference : serviceReferences) {
                Object property = serviceReference.getProperty(DataSourceFactory.OSGI_JDBC_DRIVER_NAME);
                if(property instanceof String && ((String) property).equalsIgnoreCase(URI_DRIVER_TO_OSGI_DRIVER.get(driverName))) {
                    dbDriverReference = serviceReference;
                    break;
                }
            }
            if(dbDriverReference==null) {
                throw new SQLException(String.format("The database driver %s is not available",driverName));
            }
            // Referenced driver is found, use it to create a DataSource.
            try {
                DataSourceFactory dataSourceFactory = pluginHost.getHostBundleContext().getService(dbDriverReference);
                dataSource = SFSUtilities.wrapSpatialDataSource(dataSourceFactory.createDataSource(properties));
                // Register the connection factory in service hosts
                Services.registerService(DataSource.class,"OrbisGIS main DataSource",dataSource);
                // Register DataSource, will be used to register spatial features
                pluginHost.getHostBundleContext().registerService(DataSource.class,dataSource,null);
            } finally {
                pluginHost.getHostBundleContext().ungetService(dbDriverReference);
            }
        } else {
            throw new SQLException("DataBase path not found");
        }
    }

    /**
     * Register Services
     */
    private void registerServices() {
        Services.registerService(CoreWorkspace.class, I18N.tr("Contains folders path"),
                        coreWorkspace);
    }
    
    /**
     * Save the persistent state of all services.
     * Use this function before closing the core to retrieve the same state on
     * the next core initialisation.
     * - Save the list of registered data source
     */
    public void saveStatus() {
    }

    /**
     * @return The data source where OrbisGIS data are stored.
     * Null if {@link #initDataBase(String, String)}
     * has not been called or failed.
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * Free resources
     */
    public void dispose() {
        // Stop plugin framework
        if(pluginHost!=null) {
            try {
                pluginHost.stop();
            } catch (Exception ex) {
                LOGGER.error(ex.getLocalizedMessage(),ex);
            }
        }
        // Unlink loggers
        Logger.getRootLogger().removeAllAppenders();
    }

    /**
     * Return the core path information.
     * @return CoreWorkspace instance
     */
    public CoreWorkspace getCoreWorkspace() {
        return coreWorkspace;
    }

    /**
     * Application is running in a verbose mode
     * @return 
     */
    public boolean isDebugMode() {
        return debugMode;
    }

    /**
     * Set Application running in a verbose mode
     * @param debugMode The new mode
     */
    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
        setFilterLevel((LevelRangeFilter)Logger.getRootLogger().getAppender(CONSOLE_LOGGER).getFilter(),debugMode);
    }
    /**
     * Set the output filter corresponding to the verbose mode
     */
    private static void setFilterLevel(LevelRangeFilter consoleFilter,boolean debugMode) {
        if(debugMode) {
            consoleFilter.setLevelMin(Level.DEBUG);
        }else{
            consoleFilter.setLevelMin(Level.INFO);
        }        
    }
    /**
     * Console output to info level min
     */
    public static Filter initConsoleLogger(boolean debugMode) {
        Logger root = Logger.getRootLogger();
        ConsoleAppender appender = new ConsoleAppender(
        new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN));
        appender.setName(CONSOLE_LOGGER);
        LevelRangeFilter consoleFilter = new LevelRangeFilter();
        setFilterLevel(consoleFilter,debugMode);
        consoleFilter.setLevelMax(Level.FATAL);
        appender.addFilter(consoleFilter);
        root.addAppender(appender);
        return consoleFilter;
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
