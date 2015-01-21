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
package org.orbisgis.logwriter;

import org.orbisgis.frameworkapi.CoreWorkspace;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Generate LOGBACK configuration using CoreWorkspace data
 * @author Nicolas Fortin
 */
@Component(immediate = true)
public class WorkspaceLogConfigGenerator implements ManagedService{
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkspaceLogConfigGenerator.class);

    /**
     * Configuration Admin service dependency.
     */
    ConfigurationAdmin pm;
    CoreWorkspace coreWorkspace;

    @Reference
    public void setPm(ConfigurationAdmin pm) {
        this.pm = pm;
    }

    public void unsetPm(ConfigurationAdmin pm) {
        this.pm = null;
    }

    @Reference
    public void setCoreWorkspace(CoreWorkspace coreWorkspace) {
        this.coreWorkspace = coreWorkspace;
    }

    public void unsetCoreWorkspace(CoreWorkspace coreWorkspace) {
        this.coreWorkspace = null;
    }

    @Activate
    public void activate() {
        try {
            Configuration configuration = pm.getConfiguration("org.ops4j.pax.logging", null);
            Dictionary<String, Object> props = configuration.getProperties();
            if(props == null) {
                props =  new Hashtable<>();
            }

            File logFile = new File(coreWorkspace.getApplicationFolder()+File.separator+coreWorkspace.getLogFile());
            String configFile = coreWorkspace.getApplicationFolder()+ File.separator + "logback.xml";
            try(InputStream stream = WorkspaceLogConfigGenerator.class.getResourceAsStream("logback.xml");
                InputStreamReader inputStreamReader = new InputStreamReader(stream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                FileWriter fileWriter = new FileWriter(configFile)) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    fileWriter.write(line.replace("WORKSPACELOGFILE", logFile.getAbsolutePath()) + "\n");
                }
            }
            props.put("org.ops4j.pax.logging.logback.config.file", configFile);
            configuration.update(props);
        } catch (IOException ex) {
            LOGGER.error(ex.getLocalizedMessage(), ex);
        }
    }

    @Override
    public void updated(Dictionary<String, ?> properties) throws ConfigurationException {

    }
}
