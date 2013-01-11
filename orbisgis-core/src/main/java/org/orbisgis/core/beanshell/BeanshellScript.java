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
import org.gdms.data.DataSourceFactory;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.DefaultDataManager;
import org.orbisgis.core.Services;

/**
 *
 * @author Erwan Bocher
 */
public final class BeanshellScript {

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
        public static void servicesRegister() {

                //Register the datasource
                DataSourceFactory dsf = new DataSourceFactory();

                dsf.loadPlugins();

                // Installation of the service
                Services.registerService(
                        DataManager.class,
                        "Access to the sources, to its properties (indexes, etc.) and its contents, either raster or vectorial",
                        new DefaultDataManager(dsf));

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
                                System.out.println("The file doesn't exist.");
                        } else {
                                servicesRegister();
                                Interpreter interpreter = new Interpreter();
                                interpreter.setOut(System.out);
                                DataManager dm = Services.getService(DataManager.class);
                                interpreter.setClassLoader(dm.getDataSourceFactory().getClass().getClassLoader());
                                interpreter.set("bsh.args", args);
                                interpreter.set("dsf", dm.getDataSourceFactory());
                                interpreter.eval("setAccessibility(true)");
                                FileReader reader = new FileReader(file);
                                interpreter.eval(reader);
                        }
                } else {
                        System.out.print("The second parameter must be not null.\n");
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
                return "Beanshell script arguments. The first argument must be  a path to the script file.\n";
        }

        private BeanshellScript() {
        }
}
