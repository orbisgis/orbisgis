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
package org.orbisgis.core.beanshell;

import bsh.EvalError;
import bsh.Interpreter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.orbisgis.core.context.main.MainContext;
import org.orbisgis.core.plugin.BundleReference;
import org.orbisgis.core.plugin.BundleTools;
import org.orbisgis.core.plugin.PluginHost;
import org.orbisgis.core.workspace.CoreWorkspace;
import org.osgi.framework.Bundle;

/**
 *
 * @author Erwan Bocher
 */
public final class BeanshellScript {
        static MainContext mainContext;
        public static final String ARG_WORKSPACE = "-wks";
        public static final String ARG_APPFOLDER = "-af";
        public static final String ARG_DEBUG = "debug";

        /**
         * Entry point.
         * @param args
         * @throws EvalError
         * @throws FileNotFoundException
         */
        public static void main(String[] args) throws EvalError, FileNotFoundException {
                if (args.length == 0) {
                        printHelp();
                } else {
                        execute(args);
                }
        }

        /**
         * This class is used to load a datasourcefactory
         */
        public static void servicesRegister(Map<String,String> parameters) throws IllegalArgumentException {
                CoreWorkspace coreWorkspace = new CoreWorkspace();
                if(parameters.containsKey(ARG_APPFOLDER)) {
                    coreWorkspace.setApplicationFolder(new File(parameters.get(ARG_APPFOLDER)).getAbsolutePath());
                }
                if(parameters.containsKey(ARG_WORKSPACE)) {
                    coreWorkspace.setWorkspaceFolder(new File(parameters.get(ARG_WORKSPACE)).getAbsolutePath());
                } else {
                    File defaultWorkspace = coreWorkspace.readDefaultWorkspacePath();
                    if(defaultWorkspace==null) {
                        List<File> workspacesPath = coreWorkspace.readKnownWorkspacesPath();
                        if(!workspacesPath.isEmpty()) {
                            coreWorkspace.setWorkspaceFolder(workspacesPath.get(0).getAbsolutePath());
                        }
                    }
                }
                // Create workspace folder if it does no exists
                File workspace = new File(coreWorkspace.getWorkspaceFolder());
                if(!workspace.exists()) {
                    if(!workspace.mkdirs()) {
                        throw new IllegalArgumentException("Could not create workspace folder, check disk space and rights");
                    }
                }
                mainContext = new MainContext(parameters.containsKey(ARG_DEBUG),coreWorkspace,true);
                // Read user default workspace, or use predefined one
                // Launch OSGi
                mainContext.startBundleHost(new BundleReference[0]);
                // Show active bundles
                if(parameters.containsKey(ARG_DEBUG)) {
                    for (Bundle bundle : mainContext.getPluginHost().getHostBundleContext().getBundles()) {
                        System.out.println(
                                "[" + bundle.getBundleId() + "]\t"
                                        + BundleTools.getStateString(bundle.getState())
                                        + bundle.getSymbolicName());
                    }
                }
                // Init BDD
                try {
                    mainContext.initDataBase("","");
                } catch (SQLException ex) {
                    throw new IllegalArgumentException("Cannot connect to the database "+ex.getLocalizedMessage(),ex);
                }
        }

        /**
         *
         * @param offset Read args from this index
         * @param args command line arguments
         * @return Map of key value of arguments
         */
        private static Map<String,String> parseArgs(int offset,String[] args) {
            //Pairs
            Map<String,String> pairs = new HashMap<String, String>();
            for(int i=offset;i<args.length;i++) {
                String key = args[i];
                if(i+1<args.length && key.startsWith("-")) {
                    i++;
                    pairs.put(key,args[i]);
                } else {
                    pairs.put(key,"");
                }
            }
            return pairs;
        }

        /**
         * Here the method to execute the beanshell script
         * @param  args
         * @throws EvalError
         * @throws FileNotFoundException 
         */
        private static void execute(String[] args) throws EvalError, FileNotFoundException {
                String script = args[0];
                if (script != null && !script.isEmpty()) {
                        File file = new File(script);
                        if (!file.isFile()){
                                printHelp();
                        }
                        else if (!file.exists()) {
                                System.err.println("The file doesn't exist.");
                        } else {
                                try {
                                        servicesRegister(parseArgs(1,args));
                                        Interpreter interpreter = new Interpreter();
                                        interpreter.setOut(System.out);
                                        interpreter.setClassLoader(mainContext.getClass().getClassLoader());
                                        interpreter.set("bsh.args", args);
                                        interpreter.eval("setAccessibility(true)");
                                        FileReader reader = new FileReader(file);
                                        interpreter.eval(reader);
                                } finally {
                                        mainContext.dispose();
                                }
                        }
                } else {
                        System.err.print("The second parameter must be not null.\n");
                }
        }

        /**
         * Print the help associated to this executable.
         */
        public static void printHelp() {
                System.out.print(getHelp());
        }

        /**
         * Get the help associated to this executable.
         */
        public static String getHelp() {
                return "Beanshell script arguments. The first argument must be  a path to the script file.\n" +
                        "orbisshell.sh scriptpath [options]\n" +
                        "Options :\n" +
                        "\t"+ARG_WORKSPACE+" path\tWorkspace folder\n" +
                        "\t"+ARG_APPFOLDER+" path\tApplication folder\n" +
                        "\t"+ARG_DEBUG+"\t\tDebug mode\n";
        }

        private BeanshellScript() {
        }
}
