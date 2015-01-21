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


import java.io.IOException;
import java.io.PrintStream;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;

import org.apache.felix.shell.Command;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * A Felix shell command updating a factory configuration inside a configuration admin.
 */
@Component(property = {"commandName="+UpdateConfiguration.COMMAND_NAME})
public class UpdateConfiguration implements Command {
    public static final String COMMAND_NAME = "update_conf";
    /**
     * Configuration Admin service dependency.
     */
    ConfigurationAdmin pm;


    @Reference
    public void setPm(ConfigurationAdmin pm) {
        this.pm = pm;
    }

    public void unsetPm(ConfigurationAdmin pm) {
        this.pm = pm;
    }

    /**
     * Set logback configuration
     */
    @Activate
    public void activate() {
        execute(COMMAND_NAME+" org.ops4j.pax.logging org.ops4j.pax.logging.logback.config.file=conf/logback.xml", System.out, System.err);
    }

    /**
     * Execute the command. The command pattern is:
     * update_conf <instance-name> <property-name=property-value>*
     * @param line : command line
     * @param out : out print stream
     * @param err : error print stream
     * @see org.apache.felix.shell.Command#execute(java.lang.String, java.io.PrintStream, java.io.PrintStream)
     */
    public void execute(String line, PrintStream out, PrintStream err) {
        // Parse the command line
        line = line.substring(getName().length()).trim();
        String[] args = line.split(" ");
        if(args.length < 1) { err.println(getUsage()); return; }

        String name = args[0];

        try {

            Configuration conf = pm.getConfiguration(name, null);
            Dictionary<String, Object> props = new Hashtable<>();

            for(int i = 1; i < args.length; i++) {
                String[] prop = args[i].split("=");
                if(prop.length != 2) { err.println(getUsage()); return; }
                props.put(prop[0], prop[1]);
            }

            conf.update(props);

            out.println("Update the configuration : " + props);

        } catch (IOException e) {
            err.println("An error occurs when updating a configuration : " + e.getMessage());
        }
    }

    /**
     * Gets the command name.
     * @return the command name (i.e. update_conf).
     * @see org.apache.felix.shell.Command#getName()
     */
    public String getName() {
        return COMMAND_NAME;
    }

    /**
     * Gets the command short description.
     * @return a short description about this command.
     * @see org.apache.felix.shell.Command#getShortDescription()
     */
    public String getShortDescription() {
        return "Command to update an existing configuration in the config admin";
    }

    /**
     * Gets command usage message.
     * @return the command pattern
     * @see org.apache.felix.shell.Command#getUsage()
     */
    public String getUsage() {
        return "update_conf <instance-name> <property-name=property-value>*";
    }

}