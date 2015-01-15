/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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

package org.orbisgis.logwriter;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import org.orbisgis.frameworkapi.CoreWorkspace;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.LoggerContext;

import java.io.File;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

@Component(immediate = true)
public class LogFileWriter {
    private FileAppender<ILoggingEvent> fileAppender;

    @Reference
    public void setConfigurationAdmin(ConfigurationAdmin configurationAdmin) {
        try {
            Configuration configuration = configurationAdmin.getConfiguration("org.ops4j.pax.logging", null);
            Dictionary<String, Object> config = configuration.getProperties();
            if(config == null) {
                config = new Hashtable<>();
            }
            config.put("org.ops4j.pax.logging.logback.config.file", "conf/logback.xml");
            configuration.update(config);
        } catch (IOException ex) {
            // Ignore
        }
    }

    public void unsetConfigurationAdmin(ConfigurationAdmin configurationAdmin) {

    }

    /*
    @Reference
    public void setCoreWorkspace(CoreWorkspace coreWorkspace) {
        ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
        if(loggerFactory instanceof LoggerContext) {
            LoggerContext loggerContext = (LoggerContext)loggerFactory;
            fileAppender = new FileAppender<>();
            fileAppender.setContext(loggerContext);
            fileAppender.setName("timestamp");
            // set the file name
            fileAppender.setFile(coreWorkspace.getApplicationFolder()+ File.pathSeparator + coreWorkspace.getLogFile());

            PatternLayoutEncoder encoder = new PatternLayoutEncoder();
            encoder.setContext(loggerContext);
            encoder.setPattern("%r %thread %level - %msg%n");
            encoder.start();

            fileAppender.setEncoder(encoder);
            fileAppender.start();

            // attach the rolling file appender to the logger of your choice
            Logger logbackLogger = loggerContext.getLogger("Main");
            logbackLogger.addAppender(fileAppender);
        }
    }
    public void unsetCoreWorkspace(CoreWorkspace coreWorkspace) {
        ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
        if(fileAppender != null && loggerFactory instanceof LoggerContext) {
            LoggerContext loggerContext = (LoggerContext)loggerFactory;
            // attach the rolling file appender to the logger of your choice
            Logger logbackLogger = loggerContext.getLogger("Main");
            logbackLogger.detachAppender(fileAppender);
            fileAppender.stop();
        }
    }
    */
}
