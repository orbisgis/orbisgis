/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
@Component
public class WorkspaceLogConfigGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkspaceLogConfigGenerator.class);
    private static final String KEY_CONFIG_FILE = "org.ops4j.pax.logging.logback.config.file";
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
        File logFile = new File(coreWorkspace.getApplicationFolder()+File.separator+coreWorkspace.getLogFile());
        String configFile = coreWorkspace.getApplicationFolder()+ File.separator + "logback.xml";
        try {
            Configuration configuration = pm.getConfiguration("org.ops4j.pax.logging", null);
            Dictionary<String, Object> props = configuration.getProperties();
            if(props == null) {
                props =  new Hashtable<>();
            }
            if(props.get(KEY_CONFIG_FILE) == null) {
                if (!(new File(configFile).exists())) {
                    try (InputStream stream = WorkspaceLogConfigGenerator.class.getResourceAsStream("logback.xml");
                         InputStreamReader inputStreamReader = new InputStreamReader(stream);
                         BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                         FileWriter fileWriter = new FileWriter(configFile)) {
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            fileWriter.write(line.replace("WORKSPACELOGFILE", logFile.getAbsolutePath()) + "\n");
                        }
                        fileWriter.flush();
                    }
                }
                props.put(KEY_CONFIG_FILE, configFile);
                configuration.update(props);
            }
        } catch (IOException ex) {
            LOGGER.error(ex.getLocalizedMessage(), ex);
        }
    }
}
