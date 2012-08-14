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
package org.orbisgis.core.ui.plugins.views.beanShellConsole.commands;

import bsh.CallStack;
import bsh.Interpreter;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * This class is used to load a JAR into the current classloader.
 * If the file doesn't exist nothing is reported.
 * @author ebocher
 */
public class AddJAR {

        /**
         * Implement the command action.
         */
        public static void invoke(Interpreter env, CallStack callstack, String filePath) throws Exception {
                execute(filePath);
        }

        public static void execute(String filePath) throws Exception {
                File file = new File(filePath);
                if(file.exists()){
                addSoftwareLibrary(new File(filePath));
                }
        }

        private static void addSoftwareLibrary(File file) throws Exception {
                Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
                method.setAccessible(true);
                method.invoke(ClassLoader.getSystemClassLoader(), new Object[]{file.toURI().toURL()});
        }
}
