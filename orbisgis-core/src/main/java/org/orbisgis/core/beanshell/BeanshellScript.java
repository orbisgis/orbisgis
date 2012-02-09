/** OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
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
 * info@orbisgis.org
 */
package org.orbisgis.core.beanshell;

import bsh.EvalError;
import bsh.Interpreter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import org.gdms.data.SQLDataSourceFactory;
import org.gdms.data.WarningListener;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.DefaultDataManager;
import org.orbisgis.core.Services;

/**
 *
 * @author ebocher
 */
public class BeanshellScript {

        /**
         * Entry point.
         * @param args
         * @throws EvalError
         * @throws FileNotFoundException
         */
        public static void main(String[] args) throws EvalError, FileNotFoundException {
                if (args.length == 0) {
                        printHelp();
                } else if (args.length == 2) {
                        if (args[0].equals("-f")) {
                                String script = args[1];
                                if (script != null && !script.isEmpty()) {
                                        execute(script);
                                }
                                else {
                                    System.out.println("The second parameter must be not null.");    
                                }
                        } else {
                                printHelp();
                        }
                }
        }

        /**
         * This class is used to load a datasourcefactory
         */
        public static void servicesRegister() {

                //Register the datasource
                SQLDataSourceFactory dsf = new SQLDataSourceFactory();

                // Pipeline the warnings in gdms to the warning system in the
                // application
                dsf.setWarninglistener(new WarningListener() {

                        @Override
                        public void throwWarning(String msg) {
                                System.out.println(msg);
                        }

                        @Override
                        public void throwWarning(String msg, Throwable t, Object source) {
                                System.out.println(msg);
                        }
                });

                dsf.loadPlugins();

                // Installation of the service
                Services.registerService(
                        DataManager.class,
                        "Access to the sources, to its properties (indexes, etc.) and its contents, either raster or vectorial",
                        new DefaultDataManager(dsf));

        }

        /**
         * Here the method to execute the beanshell script
         * @param script
         * @throws EvalError
         * @throws FileNotFoundException 
         */
        private static void execute(String script) throws EvalError, FileNotFoundException {
                File file = new File(script);

                if (!file.exists()) {
                        System.out.println("The file doesn't exist.");
                } else {
                        servicesRegister();
                        Interpreter interpreter = new Interpreter();
                        interpreter.setOut(System.out);
                        DataManager dm = Services.getService(DataManager.class);
                        interpreter.setClassLoader(dm.getDataSourceFactory().getClass().getClassLoader());
                        interpreter.set("dsf", dm.getDataSourceFactory());
                        interpreter.eval("setAccessibility(true)");
                        FileReader reader = new FileReader(file);
                        interpreter.eval(reader);
                }
        }

        /**
         * Print the help associated to this executable.
         */
        public static void printHelp(){
                System.out.print(getHelp());
        }

        /**
         * Get the help associated to this executable.
         */
        public static String getHelp(){
                return "Beanshell script Parameter :  [-f <path script file>]\n";
        }
}
